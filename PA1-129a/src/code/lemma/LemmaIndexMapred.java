package code.lemma;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;

import util.StringIntegerList;
import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

/**
 * 
 *
 */
public class LemmaIndexMapred {
	public static class LemmaIndexMapper extends Mapper<LongWritable, WikipediaPage, Text, StringIntegerList> {

		public void map(LongWritable offset, WikipediaPage page, OutputCollector<Text, Text> output, Reporter arg3) throws IOException,
				InterruptedException {
			
			BufferedReader input = new BufferedReader(new FileReader(page.getContent()));
			String word = "";
			String article = "";
			while ((word = input.readLine()) != null)
			{
				Scanner input2 = new Scanner(word);
				while (input2.hasNext())
				{
					String lemma = lemmatizer(input2.next());
					article = article + lemma;
				}
				
			}
			Text text = new Text(offset.toString()); 
			output.collect(text, new Text(article));
			
			
		}
		
		public String lemmatizer(String word)
		{
			String lemma = "";
			if (word.length()> 1)
			{
				if (word.charAt(word.length()-1) == 's' && word.charAt(word.length()-2)!= 'a' && word.charAt(word.length()-2)!= 'i' && word.charAt(word.length()-2)!= 'o' && word.charAt(word.length()-2)!= 'u')
				{
					lemma = word.substring(0,word.length()-2);
				}
			}
			
			if (word.length()> 5)
			{
				if (word.charAt(word.length()-1)=='d'&& word.charAt(word.length()-2)=='e' && word.charAt(word.length()-3)== word.charAt(word.length()-4))
				{
					lemma = word.substring(0,word.length()-4);
				}
				
				else if (word.charAt(word.length()-1)=='d'&& word.charAt(word.length()-2)=='e' && word.charAt(word.length()-3)=='i')
				{
					lemma = word.substring(0,word.length()-4) + "y";
				}
				
				else if (word.charAt(word.length()-1)=='d' && word.charAt(word.length()-2)=='e' && word.charAt(word.length()-3)!= 'a' && word.charAt(word.length()-3)!= 'i' && word.charAt(word.length()-3)!= 'o' && word.charAt(word.length()-3)!= 'u' && word.charAt(word.length()-4) =='a'&& word.charAt(word.length()-4) =='e'&& word.charAt(word.length()-4) =='i'&& word.charAt(word.length()-4) =='o'&& word.charAt(word.length()-4) =='u')
				{
					lemma = word.substring(0,word.length()-2);
				}
				
				else if (word.charAt(word.length()-1)=='d' && word.charAt(word.length()-2)=='e' )
				{
					lemma = word.substring(0,word.length()-3);
				}
			}
			
			if (word.length() > 6)
			{
				if (word.charAt(word.length()-1)=='g' && word.charAt(word.length()-2)=='n' && word.charAt(word.length()-3)=='i' && word.charAt(word.length()-4)== word.charAt(word.length()-5))
				{
					lemma = word.substring(0,word.length()-5);
				}
				
				else if (word.substring(word.length()-4,word.length()-1).equals("ing")&& !isVowel(word.charAt(word.length()-4))&& isVowel(word.charAt(word.length()-5)))
				{
					lemma = word.substring(0,word.length()-4)+"e";
				}
				
				else if (word.substring(word.length()-4,word.length()-1).equals("ing"))
				{
					lemma = word.substring(0,word.length()-4);
				}
			}
			
			else
			{
				lemma = word;
			}
			
			return lemma;
			
	
		}
		
		public boolean isVowel(char c)
		{
			if (c == 'a' || c == 'i' || c == 'o' || c == 'u')
			{
				return true;
			}
			
			return false;
		}
		
	}
	
	
	
}
