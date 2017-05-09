package cmd2;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import read.ReadFile;

public class CMD2_PSO {  
    /** 
     * 粒子群 
     */  
    CMD2_Particle[] swarm;  //粒子群
    CMD2_Particle global_best;//全局最优解  
    int pcount;//粒子的数量  
    
    List<CMD2_Particle> swarm1 = new ArrayList<CMD2_Particle>();
    List<CMD2_Particle> swarm2 = new ArrayList<CMD2_Particle>();
    List<CMD2_Particle> swarm3 = new ArrayList<CMD2_Particle>();
    
    List<CMD2_Particle> LeaderSet;
    List<CMD2_Particle> Archive;
    
    public static String name;
    
    /**
     * 排序
     * @param LeaderSet
     * @return
     */
    public List<CMD2_Particle> sortSet(List<CMD2_Particle> LeaderSet){
    	CMD2_Particle p;
        for (int i = 0; i < LeaderSet.size(); i++){
            for (int j = i+1; j < LeaderSet.size(); j++){
            	if(LeaderSet.get(i).pbest_fitness[0] < LeaderSet.get(j).pbest_fitness[0]){
            		p = LeaderSet.get(i);
            		LeaderSet.set(i, LeaderSet.get(j));
            		LeaderSet.set(j, p);
            	}
            }
        }
        return LeaderSet;
    }
    
    /**
     * 将粒子集按照拥挤距离排列
     * @param LeaderSet
     * @return
     * @throws IOException
     */
    public List<CMD2_Particle> crowdingSort(List<CMD2_Particle> LeaderSet) throws IOException{
        List<CMD2_Particle> temp = LeaderSet;
        CMD2_Particle p;
        for (int i = 0; i < LeaderSet.size()-1; i++){
        	double di;
        	if(i==0){
        		di = Double.POSITIVE_INFINITY;		//第一个和最后一个粒子拥挤距离为无穷大
        		LeaderSet.get(i).dit = di;
        	}else{
        		di = (temp.get(i+1).pbest_fitness[0]-temp.get(i-1).pbest_fitness[0])/ReadFile.getFeatureNum(name);
        		di+=temp.get(i+1).pbest_fitness[1]-temp.get(i-1).pbest_fitness[1];
        		LeaderSet.get(i).dit = di;
        	}
            for (int j = i+1; j < LeaderSet.size();j++){
            	double dj;
            	if(j==LeaderSet.size()-1){
            		dj = Double.POSITIVE_INFINITY;		//第一个和最后一个粒子拥挤距离为无穷大
            		LeaderSet.get(j).dit = di;
            	}else{
            		dj = (temp.get(j+1).pbest_fitness[0]-temp.get(j-1).pbest_fitness[0])/ReadFile.getFeatureNum(name);
            		dj+=temp.get(j+1).pbest_fitness[1]-temp.get(j-1).pbest_fitness[1];
            	}
            	if(di > dj){
            		p = LeaderSet.get(i);
            		LeaderSet.set(i, LeaderSet.get(j));
            		LeaderSet.set(j, p);
            	}
            }
        }
        return LeaderSet;
    }
    
    /** 
     * 粒子群初始化 
     * @param n 粒子的数量 
     * @throws IOException 
     */  
    public void init(int n,String name) throws IOException {
    	CMD2_PSO.name = name;
    	CMD2_Particle.dims = ReadFile.getFeatureNum(name); 
    	pcount = n;
        
    	Archive = new ArrayList<CMD2_Particle>();
    	LeaderSet = new ArrayList<CMD2_Particle>();
        swarm = new CMD2_Particle[pcount];  //粒子群
        global_best = new CMD2_Particle();
         
        for (int i = 0; i < pcount; ++i) { 
            swarm[i] = new CMD2_Particle();
            swarm[i].initial(CMD2_Particle.dims);
            swarm[i].evaluate();
            LeaderSet.add(swarm[i]);
        }
        
        
        int sp = swarm.length/3;
        for(int i = 0;i<swarm.length;i++){
        	if(i<sp){
        		swarm1.add(swarm[i]);
        	}else if(i>=sp){
        		if(i<2*sp){
        			swarm2.add(swarm[i]);
        		}else{
        			swarm3.add(swarm[i]);
        		}
        	}
        }
        
        //将LeaderSet中的粒子按照位置排序
        LeaderSet = sortSet(LeaderSet);
        
        //求拥挤距离,按拥挤距离排序
        LeaderSet = crowdingSort(LeaderSet);
    }  
    
    /**
     * 二元竞赛
     * @param LeaderSet
     * @return
     */
    public CMD2_Particle getGbest(List<CMD2_Particle> LeaderSet){
    	CMD2_Particle g = new CMD2_Particle();
    	if(LeaderSet.size()==1){
    		return LeaderSet.get(0);
    	}
    	int a = CMD2_Particle.randomNum(0, LeaderSet.size(), 1)[0];
    	for(int i=0;i<1;i++){
    		int b = CMD2_Particle.randomNum(0, LeaderSet.size(), 1)[0];
    		if(LeaderSet.get(a).dit>=LeaderSet.get(b).dit){
    			a=b;
    		}
    	}
    	g = LeaderSet.get(a);
		return g;
    }
    
    
    /** 
     * 粒子群的运行 
     * @throws IOException 
     */  
	public void run(int runtimes,int times) throws IOException {
        int index;  
        List<CMD2_Particle> record;
        int count = 1;
        CMD2_SortBest sb  =new CMD2_SortBest();
        int T=runtimes;
        while (runtimes > 0) {  
            index = -1; 
            //每个粒子更新位置和适应值
            
            for (int i = 0; i < swarm.length; i++) {
            	CMD2_Particle c = getGbest(LeaderSet);
            	swarm[i].gbest = c.pbest;
            	swarm[i].gbest_fitness = c.pbest_fitness;
                swarm[i].updatev(); 
            }
            
            
            //变异
            int sp = swarm.length/3;
            for(int i = 0;i<swarm.length;i++){
            	if(i>=sp){
            		if(i<2*sp){
            			swarm[i].mutation(swarm[i].Mr, 2*sp, swarm.length, swarm);
            		}else{
            			swarm[i].nonUniMutation(runtimes, T, swarm[i].Mr, swarm);
            		}
            	}
            }
            
            //评价
            for (int i = 0; i < swarm.length; ++i) {
                swarm[i].evaluate();
            }
	        
	        //update LS
	    	 for (int i = 0; i < pcount;i++){
	        	index = -1; 
	        	for (int j = 0; j < pcount; j++){
	            	if(swarm[j].pbest_fitness[0] <= swarm[i].pbest_fitness[0]&&swarm[j].pbest_fitness[1] < swarm[i].pbest_fitness[1]){
	            		index=1;
	            	}else if(swarm[j].pbest_fitness[0] < swarm[i].pbest_fitness[0]&&swarm[j].pbest_fitness[1] <= swarm[i].pbest_fitness[1]){
	            		index=1;
	            	}
	            }
	        	for(int k = 0;k<LeaderSet.size();k++){
	        		if(LeaderSet.get(k).pbest_fitness[0] == swarm[i].pbest_fitness[0]&&LeaderSet.get(k).pbest_fitness[1] == swarm[i].pbest_fitness[1]){
	        			index=1;
	        		}
	        	}
	            if(index == -1){
	            	double cf = 0;
	            	int cfn = 0;
	            	for(int k=0;k<LeaderSet.size();k++){
            			if(cf<LeaderSet.get(k).dit){
            				cf = LeaderSet.get(k).dit;
            				cfn = k;
            			}
	            	}
	            		LeaderSet.add(swarm[i]);
	                    
	                    
	                    //将粒子按照顺序排序
	                    LeaderSet = sortSet(LeaderSet);
	                    //求拥挤距离,按拥挤距离排序
	                    LeaderSet = crowdingSort(LeaderSet);
	                    
	                    if(cf>LeaderSet.get(swarm.length).dit){
	                    	LeaderSet.remove(cfn);
	                    }else{
	                    	LeaderSet.remove(swarm.length);
	                    }
	            	}
	        }
	    	 
	       	Archive.addAll(LeaderSet);
	       	record = Archive;
	       	if(count%10==0 || count==1){
	       		sb.getCMDResult(name, record, count+"-"+times);
	       	}
	       	count++;
            runtimes--;  
        }
    }
    /** 
     * 显示程序求解结果 
     */  
    public void showresult(List<CMD2_Particle> list) {
    	INNER:for(int i = 0 ; i<Archive.size() ; i++){
    		
    		int k=0;
    		for(CMD2_Particle p:list){
    			if(p.pbest_fitness[0]==Archive.get(i).pbest_fitness[0]){
    				if(p.pbest_fitness[1]<=Archive.get(i).pbest_fitness[1]){
    					continue INNER;
    				}else{
    					k=list.indexOf(p);
    				}
    			}
    		}
    		if(k>0){
    			list.remove(k);
    			
    		}
    		list.add(Archive.get(i));
    	}
    }  
}  