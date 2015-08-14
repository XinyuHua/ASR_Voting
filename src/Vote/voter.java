package Vote;
import java.io.*;
import java.util.*;

public class voter {
	
	private static String MAP_PATH;
	private static String EVENT_RST_PATH;
	private static String EVENT_RST_NODUP_PATH;
	private static String DEBUG_PATH = "dat/debug.txt";
	private static String SCENE_RST_PATH;
	private static final int dim = 60;
	private static final int clipNum = 3;
	private static final int sceneNum = 10;
	private static final boolean useTFIDF = true;
	private static String[][] eventDetected = new String[clipNum][];
	private static double[][] scoreForEvents = new double[clipNum][];
	private static String[][] eventDetectedWithConfusion = new String[clipNum][];
	private static double[][] scoreForEventsWithConfusion = new double[clipNum][];
	private static String[][] gtMapEvent = new String[10][];
	private static double[][] gtMapTFIDF = new double[10][];
	private static String[] Result = new String[clipNum];
	private static String[] SceneList = new String[10];
	private static String[] ClipName = new String[clipNum];
	
	public static void main(String[] args)throws Exception
	{
		ChooseEventRST(0);
		ReadMap();
		ReadEventRst(2);
		RemoveDuplicates();
	//	WriteNoDupEventDetectionResult();
	//	Debug();
		Vote();
	}
	
	private static void WriteNoDupEventDetectionResult()throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(EVENT_RST_NODUP_PATH));
		for(int i = 0; i < clipNum; ++i)
		{
			bw.append(ClipName[i]);
			for(int j = 0; j < dim; ++j)
			{
				String tmp = eventDetected[i][j];
				if(tmp == null || tmp.equals(""))
				{
					break;
				}
				
				bw.append("\t");
				bw.append(tmp);
				bw.append(" ");
				bw.append(Double.toString(scoreForEvents[i][j]));
			}
			bw.newLine();
		}
		bw.close();
	}
	
	private static void ChooseEventRST(int idx)
	{
		switch (idx){
			case 0: 
				EVENT_RST_PATH = "dat/eventDetectionResult/result_detail_specsub_debug.txt";
				EVENT_RST_NODUP_PATH = "dat/eventDetectionResult/result_detail_specsub_no_dup.txt";
				SCENE_RST_PATH = "dat/sceneDetectionResult/result_xx_confusion2.txt";
				MAP_PATH = "dat/gtMap/gtMap.txt";
				break;
			case 1:
				EVENT_RST_PATH = "dat/eventDetectionResult/result_8_12_2.txt";
				SCENE_RST_PATH = "dat/sceneDetectionResult/result_xy.txt";
				MAP_PATH = "dat/gtMap/map_7_21.txt";
				break;
		}
	}
	
	private static void ReadMap()throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(MAP_PATH));
		String line = null;
		int sceneNum = 0;
		while((line = br.readLine())!=null)
		{
			String[] splitted = line.split("\t");
			SceneList[sceneNum] = splitted[0];
			gtMapEvent[sceneNum] = new String[splitted.length - 1];
			gtMapTFIDF[sceneNum] = new double[splitted.length - 1];
			for(int i = 1; i < splitted.length; ++i)
			{
				gtMapEvent[sceneNum][i-1] = splitted[i].split("\\s+")[0];
				gtMapTFIDF[sceneNum][i-1] = Double.parseDouble(splitted[i].split("\\s+")[1]);
			}
			sceneNum++;
		}
		br.close();
	}
	
	private static void Debug()
	{
		for(int i = 0; i < clipNum; ++i)
		{
			System.out.println("-------------");
			for(int j = 0; j < dim; ++j)
			{
				
				if(eventDetected[i][j] == null)
					break;
				else if(eventDetected[i][j].equals(""))
					continue;
				System.out.print(eventDetected[i][j] + "\t");
				System.out.println(scoreForEvents[i][j]);
			}
			
		}
	}
	
	private static void RemoveDuplicates()
	{
		for(int i = 0; i < clipNum; ++i)
		{
			List<String> tmp = new ArrayList<String>();
			List<Double> tmpScore = new ArrayList<Double>();
			for(int j = 0; j < eventDetected[i].length; ++j)
			{				
				if(eventDetected[i][j] == null)				
					break;
							
				if(!tmp.contains(eventDetected[i][j]))
				{
					tmp.add(eventDetected[i][j]);
					tmpScore.add(scoreForEvents[i][j]);
				}
				else
				{
					int idx = tmp.indexOf(eventDetected[i][j]);
					if(scoreForEvents[i][j] > tmpScore.get(idx))
						tmpScore.set(idx,  scoreForEvents[i][j]);
				}
			}
			
			int k = 0;
			for(k = 0; k < tmp.size(); ++k)
			{
				eventDetected[i][k] = tmp.get(k);
				scoreForEvents[i][k] = tmpScore.get(k);
			}
			
			for(k = tmp.size(); k < dim; ++k)
			{
				eventDetected[i][k] = "";
				scoreForEvents[i][k] = 0.0;
			}
		}
	}
	
	/**
	 * Read the first i columns from Event Detection Result file
	 * @param i
	 * @throws Exception
	 */
	private static void ReadEventRst(int i)throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(EVENT_RST_PATH));
		String line = null;
		int clipNum = -1;
		String currentClip = "";
	/*	String event1 = "",event2 = "";
		double score1 = 0.0, score2 = 0.0; */
		int innerCnter = 0;
		while((line = br.readLine())!=null)
		{
			String[] splitted = line.split("\t");
			if(!currentClip.equals(splitted[0].trim()))
			{
				currentClip = splitted[0];
				System.out.println("current clips changed to:" + currentClip);
				clipNum++;
				innerCnter = 0;
				eventDetected[clipNum] = new String[dim];
				scoreForEvents[clipNum] = new double[dim];
				ClipName[clipNum] = currentClip;
			}
			
			for(int k = 1; k <= i; ++k)
			{
				eventDetected[clipNum][innerCnter] = splitted[k].split("\\s+")[0];
				scoreForEvents[clipNum][innerCnter] = Double.parseDouble(splitted[k].split("\\s+")[1]);
				innerCnter++;
			}
	/*		event1 = splitted[1].split("\\s+")[0];
			score1 = Double.parseDouble(splitted[1].split("\\s+")[1]);
			
			event2 = splitted[2].split("\\s+")[0];
			score2 = Double.parseDouble(splitted[2].split("\\s+")[1]);
			
			eventDetected[clipNum][innerCnter] = event1;
			scoreForEvents[clipNum][innerCnter] = score1;
			innerCnter++;
			eventDetected[clipNum][innerCnter] = event2;
			scoreForEvents[clipNum][innerCnter] = score2;
			innerCnter++;*/
		}
		
		
		br.close();
	}
	
	private static void ReadEventRst()throws Exception
	{	
		BufferedReader br = new BufferedReader(new FileReader(EVENT_RST_PATH));
		String line = null;
		int clipNum = -1;
		String currentClip="",event = "";
		String currentEvent = "";
		double currentScore=0.0,score = 0.0;
		int innerCnter = 0;
		boolean initial = true;
		int last = 0;
		while((line = br.readLine())!=null)
		{
			String[] splitted = line.split("\t");
			if(!currentClip.equals(splitted[0].trim()))
			{
				currentClip = splitted[0];
				System.out.println("current clips changed to:" + currentClip);
				clipNum++;
				innerCnter = 0;
				initial = true;
				eventDetected[clipNum] = new String[dim];
				scoreForEvents[clipNum] = new double[dim];
				ClipName[clipNum] = currentClip;
			}
			event = splitted[1].split("\\s+")[0];
			score = Double.parseDouble(splitted[1].split("\\s+")[1]);
			
			if(initial)
			{
				currentEvent = event;
				currentScore = score;	
				initial = false;
			}
			
			
			if(!event.equals(currentEvent))
			{
				eventDetected[clipNum][innerCnter] = currentEvent;
				scoreForEvents[clipNum][innerCnter] = currentScore;
				innerCnter++;
				last = innerCnter;
				currentEvent = event;
				currentScore = score;
			}
			else
			{
				if(currentScore < score)
				{
					currentScore = score;
				}
			}	
		}
		eventDetected[clipNum][last] = currentEvent;
		scoreForEvents[clipNum][last] = currentScore;
		
		br.close();
	}
	
	private static void Vote() throws Exception
	{
		System.out.println("=================================");
		System.out.println("Start voting for each clips...");
		for(int i = 0; i < clipNum; ++i)
		{
			System.out.println("========" + ClipName[i] + "========");
			int idx = VoteWithConfusion(i);
			if(idx == -1)
				Result[i] = "Unknown";
			else
				Result[i] = SceneList[idx];
			System.out.println("");
					
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(SCENE_RST_PATH));
		for(int i = 0; i < clipNum; ++i)
		{
			bw.append(ClipName[i]);
			bw.append("\t");
			
			bw.append(Result[i]);
			bw.newLine();
		}
		bw.close();
	}
	
	/**
	 * For clip i, using voting algorithm that considers the possibilites of confusion,
	 * to decide the most likely scene
	 * 
	 * @param i
	 * @return
	 * @throws Exception 
	 */
	private static int VoteWithConfusion(int i) throws Exception
	{
		eventConfusionDealer ecd = new eventConfusionDealer();
		int ecdK = ecd.getK();
		double max = 0.0;
		int idx = -1;
		String[] eventList = eventDetected[i];
		double[] eventScoreList = scoreForEvents[i];

		String[] eventDetectedConsiderConfused = new String[dim *ecdK];
		double[] eventDetectedConsiderConfusedValue = new double[dim * ecdK];
		
		for(int m = 0; m < dim; ++m)
		{
			if( eventList[m] == null || eventList[m].equals(""))
			{
				break;
			}
			
			String[] topK = ecd.getTopKConfused(eventList[m]);
			double[] topKValue = ecd.getTopKConfusedValue(eventList[m]);
			for(int mm = 0; mm < ecdK; mm++)
			{
				eventDetectedConsiderConfused[m*ecdK + mm] = topK[mm];
				eventDetectedConsiderConfusedValue[m*ecdK + mm] = topKValue[mm];
			}
		}
		
		
		// Compute final scores for each event
		for(int j = 0; j < sceneNum; ++j)
		{
			System.out.println(SceneList[j]);
			
			double score = 0.0;
			String[] gtEventList = gtMapEvent[j];
			double[] gtTFIDFList = gtMapTFIDF[j];
			
			
			for(int k = 0; k < dim * ecdK ; ++k)
			{
				String tmp = eventDetectedConsiderConfused[k];
				if(tmp == null || tmp.equals(""))
				{
					if(max < score)
					{
						max = score;
						idx = j;
					}
					break;
				}
				else if(Arrays.asList(gtEventList).contains(tmp))
				{
					if(useTFIDF)
					{
						score += eventDetectedConsiderConfusedValue[k] * gtTFIDFList[Arrays.asList(gtEventList).indexOf(tmp)];
						System.out.println(tmp + "\t" + eventDetectedConsiderConfusedValue[k] * gtTFIDFList[Arrays.asList(gtEventList).indexOf(tmp)]);
					}
					else
					{
						score += eventDetectedConsiderConfusedValue[k] * 1.0;
						System.out.println(tmp + "\t" + eventDetectedConsiderConfusedValue[k] * 1.0);
					}
				}
			}
			System.out.println(score);
			System.out.println("------------------------------------------");
			
		}
		if(idx == -1)
		{
			System.out.println(ClipName[i]);
		}
		//System.out.println("------" + SceneList[idx]);
		return idx;
	}
	
	/**
	 * For clip i, using voting algorithm to decide the most likely scene
	 * 
	 * @param i
	 * @return	index of the most likely scene
	 */
	private static int Vote(int i)
	{
		double max = 0.0;
		int idx = -1;
		String[] eventList = eventDetected[i];
		double[] eventScoreList = scoreForEvents[i];

		// Compute final scores for each event
		for(int j = 0; j < sceneNum; ++j)
		{
			System.out.println(SceneList[j]);
			
			double score = 0.0;
			String[] gtEventList = gtMapEvent[j];
			double[] gtTFIDFList = gtMapTFIDF[j];
			
			//System.out.println(Arrays.asList(gtEventList).contains("applause"));
			
			for(int k = 0; k < dim; ++k)
			{
				String tmp = eventList[k];
				if(tmp == null || tmp.equals(""))
				{
					if(max < score)
					{
						max = score;
						idx = j;
					}
					break;
				}
				else if(Arrays.asList(gtEventList).contains(tmp))
				{
					if(useTFIDF)
					{
						score += eventScoreList[k] * gtTFIDFList[Arrays.asList(gtEventList).indexOf(tmp)];
						System.out.println(tmp + "\t" + eventScoreList[k] * gtTFIDFList[Arrays.asList(gtEventList).indexOf(tmp)]);
					}
					else
					{
						score += eventScoreList[k] * 1.0;
						System.out.println(tmp + "\t" + eventScoreList[k] * 1.0);
					}
				}
			}
			System.out.println(score);
			System.out.println("------------------------------------------");
			
		}
		if(idx == -1)
		{
			System.out.println(ClipName[i]);
		}
		//System.out.println("------" + SceneList[idx]);
		return idx;
	}
	
}