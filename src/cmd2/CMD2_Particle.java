package cmd2;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import read.ReadFile;

public class CMD2_Particle {  
	
	
	public static Random rnd; 
	
	
    public double[] pos;//粒子的位置，求解问题多少维，则此数组为多少维  
    public double[] v;//粒子的速度，维数同位置  
    public double Mr; //变异率
    public double dit;
    
    
    public double[] fitness;//粒子的适应度  0为位置 1为速度
    public double[] pbest;//粒子的历史最好位置  0为位置 1为速度
    public double[] gbest;//所有粒子找到的最好位置  0为位置 1为速度
    
    
    
    public double randnum1;
    public double randnum2;
    
    public static int dims;  //维度
    public double w;  
    public double c1;  
    public double c2;  
    
    public double[] gbest_fitness;//历史最优解  
    public double[] pbest_fitness;//历史最优解  
    /** 
     * 返回low—uper之间的数 (double)
     * @param low 下限 
     * @param uper 上限 
     * @return 返回low—uper之间的数 
     */  
    static double rand(double low, double uper) {
        rnd = new Random();
        return rnd.nextDouble() * (uper - low) + low;  
    }
    /**
     * 返回min和max之间的n个随机数
     * @param min
     * @param max
     * @param n
     * @return
     */
    static public int[] randomNum(int min, int max, int n){
    	
        if (n > (max - min + 1) || max < min) {  
               return null;  
           }  
        int[] result = new int[n];  
        int count = 0;  
        while(count < n) {  
            int num = (int) (Math.random() * (max - min)) + min;  
            boolean flag = true;  
            for (int j = 0; j < n; j++) {  
                if(num == result[j]){  
                    flag = false;
                    break;
                }
            }  
            if(flag){ 
            	result[count] = num;
                count++;  
            }  
        }  
        return result;  
    }  
    
    
    /** 
     * 初始化粒子 
     * @param dim 表示粒子的维数 
     * @throws IOException 
     */  
    public void initial(int dim) throws IOException {  
        pos = new double[dim]; 
        v = new double[dim];
        pbest = new double[dim];  
        dims = dim;
        for(int i=0;i<pos.length;i++){
        	pos[i]=CMD2_Particle.rand(0, 1);
        	pbest[i] = pos[i];										//初始化粒子的个体最优//初始化粒子的速度
        	v[i] = rand(-0.6, 0.6); 
        }	                        
        fitness = new double[2];					//适应值，这里有2个，第一个是特征数，第二个是错误率
        pbest_fitness = new double[2];
        pbest_fitness[0] = ReadFile.getFeatureNum(CMD2_PSO.name)+1;
        pbest_fitness[1] = 1;
        gbest_fitness = new double[2];
        gbest = new double[dim];
        
        Mr = 1/pos.length;
        randnum1 = rand(0,1);
        randnum2 = rand(0,1);
        c1 = rand(1.5,2.0);
        c2 = rand(1.5,2.0);
        w = rand(0.1,0.5);
        dit = 0;
    }  
    /** 
     * 评估函数值,同时记录历史最优位置 
     * @throws IOException 
     */  
    public void evaluate() throws IOException {

    	List<String> choose = new ArrayList<String>();
    	int j=1;
    	for(int i=0;i<pos.length;i++){
    		if(pos[i]>0.6){
    			choose.add(String.valueOf(j));
    		}
    		j++;
    	}
    	
		ReadFile rf = new ReadFile();
		rf.getFile(choose, "dataset\\Alltra--"+CMD2_PSO.name, "tra");
		rf.getFile(choose, "dataset\\Alltest--"+CMD2_PSO.name, "test");
		
		String[] trainArgs = {"tra"};						//directory of training file
		String modelFile = svm_train.main(trainArgs);
		String[] testArgs = {"test", modelFile, "result"};		//directory of test file, model file, result file
		Double accuracy = svm_predict.main(testArgs);
		Double errorRate = 1-accuracy;
    	
		fitness[0] = choose.size();
        fitness[1] = errorRate;
        
        //更新个体最优解
        if (fitness[0] < pbest_fitness[0]&&fitness[1] < pbest_fitness[1]) {
        	pbest_fitness[0] = fitness[0];
        	pbest_fitness[1] = fitness[1];
        	System.arraycopy(pos, 0, pbest, 0, pos.length);
        }else if(fitness[0] == pbest_fitness[0]&&fitness[1] < pbest_fitness[1]){
        	pbest_fitness[0] = fitness[0];
        	pbest_fitness[1] = fitness[1];
        	System.arraycopy(pos, 0, pbest, 0, pos.length);
        }else if(fitness[0] < pbest_fitness[0]&&fitness[1] == pbest_fitness[1]){
        	pbest_fitness[0] = fitness[0];
        	pbest_fitness[1] = fitness[1];
        	System.arraycopy(pos, 0, pbest, 0, pos.length);
        }
    }  
    /** 
     * 更新速度和位置 
     * @throws IOException 
     */  
    public void updatev() throws IOException {
        for (int i = 0; i < pos.length; i++) {
            v[i] = w * v[i] + c1 * randnum1 * (pbest[i] - pos[i])  
                    + c2 * randnum2 * (gbest[i] - pos[i]);  
            if (v[i] > 0.6) {  
                v[i] = CMD2_Particle.rand(-0.6, 0.6);  
            }  
            if (v[i] < -0.6) {  
                v[i] = CMD2_Particle.rand(-0.6, 0.6); 
            }  
            pos[i] = (pos[i] + v[i]);  
            if (pos[i] > 1) {
                pos[i] = CMD2_Particle.rand(0, 1);
            }
            if (pos[i] < 0) {  
                pos[i] = CMD2_Particle.rand(0, 1);
            }
        }
    }
    
    public void nonUniMutation(int t,int T,double Mr,CMD2_Particle[] swarm){
    	int b=3;
    	for(int i=0;i<pos.length;i++){
    		double r = CMD2_Particle.rand(0, 1);
    		double a = CMD2_Particle.rand(0, 1);
    		if (a<=Mr) {
    	    	double posi = CMD2_Particle.rand(0, 1);
    	    	double y ;
    	    	int e;
    	    	if (posi>0.5){
    	    		y=1-pos[i];
    	    		e=0;
    	    	}else{
    	    		y=pos[i]-0;
    	    		e=1;
    	    	}
    	    	double k = Math.pow(1-(t/T), b);
    	    	double del =  y*(1-(Math.pow(r, k)));
    	    	
    	    	if (e==0){
    	    		pos[i]=pos[i]+del;
    	    	}else{
    	    		pos[i]=pos[i]-del;
    	    	}
    	    	
    		}
    	}
    }
    
    
    public void mutation(double Mr,int min,int max,CMD2_Particle[] swarm) throws IOException{
    	double a = CMD2_Particle.rand(0, 1);
    	if (a<=Mr) {
    		double F=0.5;
        	CMD2_Particle mu = new CMD2_Particle();
        	mu.initial(dims);
        	for(int i=0;i<pos.length;i++){
            	int x1 = randomNum(min,max,1)[0]; 
            	int x2 = randomNum(min,max,1)[0]; 
            	int x3 = randomNum(min,max,1)[0]; 
            	while(x1 == i){
            		x1 = randomNum(min,max,1)[0]; 
            	}
            	while(x1 == x2 || x2 == i){
            		x2 = randomNum(min,max,1)[0]; 
            	}
            	while(x1 == x3 || x3 == i){
            		x3 = randomNum(min,max,1)[0]; 
            	}

            	double v = swarm[x3].pos[i]+(F*(swarm[x1].pos[i]-swarm[x2].pos[i]));
            	double a1 = CMD2_Particle.rand(0, 1);
            	if(v > 1||v < 0){
            		if(a1<0.5){
            			v = pos[i];					//越界处理
            		}else{
            			v = 0.5;
            		}
            		
            	}
            	mu.pos[i]=v;
        	}
        	mu.evaluate();
        	if(mu.fitness[0]<=fitness[0]&&mu.fitness[1]<=fitness[1]){
    			pos=mu.pos;
        	}
		}
    }
}  
