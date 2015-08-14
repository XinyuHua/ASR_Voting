package Vote;
import java.util.*;
import java.io.*;

public class eventConfusionDealer {
	private static final String EVENT_PATH = "dat/eventList.txt";
	private static final String CONFUSION_PATH  = "dat/confusion/raw/";
	private static final String NORMALIZED_CONFUSION_PATH  = "dat/confusion/normalized/";
	
	private static int eventNum;
	private static int[][] confusionMatrix;
	private static double[][] normalizedConfusionMatrix;
	private static List<String> eventName;
	private static String[][] topkConfused;
	private static double[][] topkConfusedValue;
	private static final int topK = 5;
	
	public eventConfusionDealer()throws Exception
	{
		LoadEvent();	
		LoadConfusionMat();
		normalizedConfusionMatrix = new double[eventNum][];
		for(int i = 0; i < eventNum; ++i)
		{
			normalizedConfusionMatrix[i] = new double[eventNum];
		}
		//NormalizedConfusionMatrixByRow();
		//WriteNormalizedConfusionMatrix();
		LoadNormalizedConfusionMatrix();
		computeTopkInNormalizedConfusionMat(topK);
	}
	
	public static void main(String[] args)throws Exception
	{
		eventConfusionDealer ecd = new eventConfusionDealer();
		String[] tmp = getTopKConfused("toilet");
		for(String st : tmp)
			System.out.println(st);
	}
	
	public static int getK()
	{
		return topK;
	}
	
	public static String[] getTopKConfused(String eventNam)
	{
		return getTopKConfused(eventName.indexOf(eventNam));
	}
	
	public static double[] getTopKConfusedValue(String eventNam)
	{
		return getTopKConfusedValue(eventName.indexOf(eventNam));
	}
	
	public static String[] getTopKConfused(int i)
	{
		return topkConfused[i];
	}
	
	public static double[] getTopKConfusedValue(int i)
	{
		return topkConfusedValue[i];
	}
	
	
	/**
	 * This method load normalized confusion matrix into normalizedConfusionMatrix[][]
	 * from file
	 * 
	 * @throws Exception
	 */
	private static void LoadNormalizedConfusionMatrix()throws Exception
	{
		File dir = new File(NORMALIZED_CONFUSION_PATH );
		for(File file: dir.listFiles())
		{
			String fileName = file.getName().replace("result_", "").replace(".txt", "");
			int eventN = eventName.indexOf(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			int idx = 0;
			while((line = br.readLine())!=null)
			{
				double current = Double.parseDouble(line);
				normalizedConfusionMatrix[eventN][idx] = current;
				idx++;
			}
			br.close();
		}
	}
	
	/**
	 * This method generates a row-normalized version of confusion matrix,
	 * and load it into normalizedConfusionMatrix[][]
	 * 
	 * @throws Exception
	 */
	private static void NormalizedConfusionMatrixByRow()throws Exception
	{
		for(int i = 0; i < eventNum; ++i)
		{
			int sum = 0;
			for(int j = 0; j < eventNum; ++j)
			{
				sum += confusionMatrix[i][j];
			}
			
			for(int j = 0; j < eventNum; ++j)
			{
				normalizedConfusionMatrix[i][j] = ((double)confusionMatrix[i][j])/sum;
			}
		}
		
	}
	
	/**
	 * This method write normalizedConfusionMatrix into files
	 * 
	 * @throws Exception
	 */
	private static void WriteNormalizedConfusionMatrix()throws Exception
	{
		for(int i = 0; i < eventNum; ++i)
		{
			File toWrite = new File(NORMALIZED_CONFUSION_PATH + "result_" + eventName.get(i) + ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(toWrite));
			for(int j = 0; j < eventNum; ++j)
			{
				bw.append(Double.toString(normalizedConfusionMatrix[i][j]));
				bw.newLine();
			}
			bw.close();
		}
	}
	
	
	private static void computeTopkInNormalizedConfusionMat(int k)
	{
		topkConfused = new String[eventNum][];
		topkConfusedValue = new double[eventNum][];
		
		for(int i = 0; i < eventNum; ++i)
		{
			topkConfused[i] = new String[k];
			topkConfusedValue[i] = new double[k];
			
			HashMap<String, Double> confusCol = new HashMap<String, Double>();
			
			for(int j = 0; j < eventNum; ++j)
			{
				confusCol.put(eventName.get(j), normalizedConfusionMatrix[j][i]);
			}
			
			ValueComparator2 bvc =  new ValueComparator2(confusCol);
	        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
			
	        sorted_map.putAll(confusCol);
	        int m = 0;
	        for(Map.Entry<String,Double> entry : sorted_map.entrySet())
			{
	        	topkConfused[i][m] = entry.getKey();
	        	topkConfusedValue[i][m] = entry.getValue();
	        	m++;
	        	if(m == k)
	        	{
	        		break;
	        	}
			}
		}
	}
	
	private static int[] confusionMatCol(String eventN)
	{
		int colNum = eventName.indexOf(eventN);
		int[] rst = new int[eventNum];
		for(int i = 0; i < eventNum; ++i)
		{
			rst[i] = confusionMatrix[i][colNum];
		}
		return rst;
	}
	
	
	private static void LoadEvent()throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(EVENT_PATH));
		eventName = new ArrayList<String>();
		String line = null;
		while((line = br.readLine())!=null)
		{
			eventName.add(line.trim());
		}
		eventNum = eventName.size();
		confusionMatrix = new int[eventNum][];
		for(int i = 0; i < eventNum; ++i)
			confusionMatrix[i] = new int[eventNum];
	}
	
	private static void LoadConfusionMat()throws Exception
	{
		File folder = new File(CONFUSION_PATH);
		for(File file : folder.listFiles())
		{
			String fileName = file.getName().replace("result_","").replace(".txt","").trim();
			int rowIdx = eventName.indexOf(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine())!=null)
			{
				String[] splitted = line.split("\\s+");
				int colIdx = eventName.indexOf(splitted[0]);
				confusionMatrix[rowIdx][colIdx] = Integer.parseInt(splitted[1]);
			}
			br.close();
		}
	}
}

class ValueComparator2 implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparator2(Map<String, Double> base) {
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