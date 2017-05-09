package cmd2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class CMD2_test {

	public static void main(String[] args) throws IOException {
		CMD2_PSO pso = new CMD2_PSO();
		List<CMD2_Particle> list = new ArrayList<CMD2_Particle>();
		
		for(int i=0;i<30;i++){
			pso.init(30,"zoo");
			pso.run(500,i);
			pso.showresult(list);
			System.out.println("CMDPSOFS2:"+CMD2_PSO.name+"第"+(i+1)+"次测试完成");
		}
		CMD2_SortBest sb = new CMD2_SortBest();
		sb.getCMDResult(CMD2_PSO.name,list,"All");
	}
}
