package Helper;
import java.io.*;
import java.util.*;
public class ResultDealer {

	private static String RAW_RST_PATH = "dat/sceneDetectionResult/result_xx.txt";
	private static String CNTED_RST_PATH = "dat/evaluation/counted.csv";
	private static int[] rstList;
	private static String[] sceneList;
	public ResultDealer()
	{
		rstList = new int[10];
		sceneList = new String[10];
	}
	
	public static void main(String[] args)throws Exception
	{
		ResultDealer rd = new  ResultDealer();
		rd.ReadRawData();
		rd.WriteProcessedData();
	}
	
	private static void WriteProcessedData()throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(CNTED_RST_PATH));
	/*	for(int i = 0; i < sceneList.length; ++i)
		{
			bw.append(sceneList[i]);
			bw.append(",");
			bw.append(Integer.toString(rstList[i]));
			bw.newLine();
		} */
		bw.append(" ");
		for(int i = 0; i < sceneList.length; ++i)
		{
			bw.append(sceneList[i]);
			bw.append(",");
		}
		bw.newLine();
		bw.append(" ");
		for(int i = 0; i < rstList.length; ++i)
		{
			bw.append(Integer.toString(rstList[i]));
			bw.append(",");
		}
		
		bw.close();
		
	}
	
	private static void ReadRawData()throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(RAW_RST_PATH));
		String line = null;
		int currentCounter = -1;
		String currentScene = "";

		while((line = br.readLine())!=null)
		{
			String[] tmp = line.split("\\s+");
			System.out.print(tmp[0] + "\t" + tmp[1]);
			if(!currentScene.equals(tmp[0].substring(0,tmp[0].indexOf("_"))))
			{
				currentCounter++;
				currentScene = tmp[0].substring(0,tmp[0].indexOf("_"));
				sceneList[currentCounter] = currentScene;
			}
			
			if(tmp[1].equals(currentScene))
			{
				rstList[currentCounter]++;
				System.out.print("\tyes");
			}
			System.out.println("");
		}
	}
}
