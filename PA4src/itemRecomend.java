

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
//The dependencies are all in the Library folder

public class itemRecomend {
	//created from this website: https://www.youtube.com/watch?v=yD40rVKUwPI


	public static void main(String[] args) {
		
		
		try {
				DataModel dm = new FileDataModel(new File("data/output_int.txt"));//this works for csv files data has to be in this format column 1 item, column 2 user who rated, column three rating,
				
				ItemSimilarity sim = new LogLikelihoodSimilarity(dm);//change this to get different similarity measures
			
				GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dm,sim);
				
				for (int i = 15; i < 16; i++)
				{
				
					Scanner input = new Scanner(new File("GroupNum/items_convert_" + i + ".txt")); 
					BufferedWriter fw = new BufferedWriter(new FileWriter(new File("output/items_output" + i + ".txt")));
					String s = "";
					while (input.hasNext())//iterate through the list of items
					{
						Long itemID = input.nextLong();
						List<RecommendedItem> recommend = recommender.recommend(itemID,10);//list 5 recomentded items
						fw.write(String.valueOf(itemID));
						
						for (RecommendedItem recommendations : recommend) //prints recommendations can add more
						{
							fw.write("," + String.valueOf(recommendations.getItemID()));
						}
						
						fw.write("\n");
						
					}
					
					input.close();
			}
			
		} catch (IOException | TasteException e) {
			System.err.println("There was an error");
			e.printStackTrace();
		}
		
		
		
	}
	
	public static long convert(String s)
	{
		long value = 0;
		
		for (int i = 0; i < 10; i++)
		{
			long c = (long) (s.charAt(i)) - 48;
			//System.out.println(c);
			value = (long) (value + (c)*Math.pow(42, i));
			
		}
		
		return value;
	}
	
	public static String backconvert(long l)
	{
		String s = "";
		
		while (l > 0)
		{
			char value = (char) ((l % 42) + 48);
			s = s + value;
			l = l/42;
			
		}
		
		return s;
	}

}
