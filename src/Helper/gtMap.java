package Helper;
import java.io.*;
import java.util.*;

public class gtMap {

	private static String MAP_PATH = "dat/gtmap.txt";
	private static String LIST_PATH = "dat/eventListFromMap.txt";
	private static HashSet<String> eventSet;
	
	
	public static void main(String[] args)throws Exception
	{
		eventSet = new HashSet<String>();
		getListFromMap();
		writeList();
	}
	
	private static void writeList()throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(LIST_PATH));
		for(String st : eventSet)
		{
			bw.append(st);
			bw.newLine();
		}
		bw.close();
	}
	
	private static void getListFromMap()throws Exception
	{
		
		BufferedReader br = new BufferedReader(new FileReader(MAP_PATH));
		String line = null;
		while((line = br.readLine())!=null)
		{
			String[] splitted = line.split("\t");
			for(int i = 1; i < splitted.length; ++i)
			{
				String tmp = splitted[i].split("\\s+")[0];
				eventSet.add(tmp);
			}
		}
		br.close();
	}
}
