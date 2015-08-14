package Vote;

import java.io.*;
import java.util.*;

public class eventSimilarity {

	private static String EVENT_DEC_PATH = "dat/eventSim/";
	private static String CONFUSION_PATH = "dat/confusion/raw/";
	
	public static void main(String[] args)throws Exception
	{
		File folder = new File(EVENT_DEC_PATH);
		for(File file : folder.listFiles())
		{
			ComputeConfusions(2,file);
		}
	}
	
	private static void ComputeConfusions(int i, File file)throws Exception
	{
		System.out.println("Computing " + file.getName());
		BufferedReader br = new BufferedReader(new FileReader(file));
		List<String> eventDetected = new ArrayList<String>();
		List<Integer> eventCnter = new ArrayList<Integer>();
		String line = null;
		String current = null;
		while((line = br.readLine())!=null)
		{
			String[] splitted = line.split("\t");
			
			for(int j = 1; j <= i; ++j)
			{
				current = splitted[j].split("\\s+")[0];
				
				if(!eventDetected.contains(current))
				{
					eventDetected.add(current);
					eventCnter.add(1);
				}
				else
				{
					int idx = eventDetected.indexOf(current);
					eventCnter.set(idx, eventCnter.get(idx) + 1);
				}
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(CONFUSION_PATH + file.getName()));
		HashMap<String, Integer> eventRst = new HashMap<String,Integer>();
		for(int k = 0; k < eventDetected.size(); ++k)
		{
			eventRst.put(eventDetected.get(k), eventCnter.get(k));
		}
		
		ValueComparator bvc =  new ValueComparator(eventRst);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
		
        sorted_map.putAll(eventRst);
        for(Map.Entry<String,Integer> entry : sorted_map.entrySet())
		{
			bw.append(entry.getKey() + "\t" + entry.getValue());
			bw.newLine();
		}
        bw.close();
		br.close();
	}
	
}

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}