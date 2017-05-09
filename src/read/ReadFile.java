package read;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {
	private static BufferedReader bf = null;
	private PrintWriter pw = null;
	
	//�õ����ݼ���������
	public static int getFeatureNum(String name) throws IOException{
		bf=new BufferedReader(new FileReader("dataset\\Alltra--"+name));
		String[] num = bf.readLine().split(" ");
		String LastFeature = num[num.length-1].split(":")[0];
		return Integer.valueOf(LastFeature);
		
	}
	
	
	//���������ѡ�������Ž�ָ��·�����ļ����ݿ�������һ�������ļ�
	public void getFile(List choose,String path,String type) throws IOException{
		File f = new File(type);
		if(f.isFile()){
			f.delete();
		}
		f.createNewFile();
		bf=new BufferedReader(new FileReader(path));
		pw=new PrintWriter(new FileOutputStream(type, true));
		
		
		while(true){
			String data = bf.readLine();
			if(data==null||data.equals("")){
				break;//���������Ϊ�������
			}
			String[] dataSet = data.split(" ");//�������ݰ��ո��Ϊ����
			
			pw.write(dataSet[0]+" ");
			for(int i=1;i<dataSet.length;i++){
				String[] ele = dataSet[i].split(":");
				for(int j=0;j<choose.size();j++){
					if(ele[0].equals(choose.get(j))){
						pw.write(dataSet[i]+" ");
					}
				}
			}
			pw.write("\r\n");
			pw.flush();
			
		}
//		System.out.println(path+"��"+choose.size()+"����������ת�Ƶ�"+type+"�ɹ���");
		bf.close();
		pw.close();
	}
}
