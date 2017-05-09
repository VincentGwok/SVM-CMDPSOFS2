package cmd2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class RecordTime {
	public static void main(String[] args) throws IOException {
		CMD2_PSO pso = new CMD2_PSO();
		List<CMD2_Particle> list = new ArrayList<CMD2_Particle>();
		String path = "testfile//CMD2TimeRecord.dat";
		File f = new File(path);
		if(!f.isFile()){
			f.createNewFile();
		}
		PrintWriter pw = new PrintWriter(new FileOutputStream(path,true));
		long start = System.currentTimeMillis();

//		pso.init(30);
//		pso.run(100);
		
		double t = (System.currentTimeMillis()-start);
		double time = t/60000;
		pw.write("wine"+" running time��"+time);
		pw.write("\n");
		pw.close();
	}
}
