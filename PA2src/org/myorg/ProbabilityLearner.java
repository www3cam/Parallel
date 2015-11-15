package org.myorg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map; 
import java.util.Scanner;
import java.util.StringTokenizer;

import util.StringIntegerList;
import util.StringIntegerList.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;

         

public class ProbabilityLearner {
	public int numpeople = 0;
	
	//input key is offset string in document.  Output is map of professions and the corresponding probabilities the second map is a Map of professions and associated lemma probabilities
	public class Mapping extends Mapper<LongWritable,Text, Map<String,Double>, Map<String, Map<String,Double>>>{//had to change class name to mapping bc it was conflicting with map collection
		BufferedReader input1;
		//BufferedReader input2;
		Map<String,Map<String,Integer>> articles = new HashMap<String,Map<String, Integer>>();//map contains the number of lemmas in articles
		Map<String,List<String>> Profmap = new HashMap<String,List<String>>();
		Map<String, Map<String,Integer>> prob = new HashMap<String, Map<String,Integer>>();//Map that contains the number of each lemma for each profession
		Map<String,Double> profprobs = new HashMap<String,Double>();//Map which contains the probability of each profession eg. P(A)
		
		@Override
		public void setup(Mapper<LongWritable, Text, Map<String,Double>, Map<String,Map<String, Double>>>.Context context) throws IOException, InterruptedException
		{
			super.setup(context);
			//input2 = new BufferedReader(new FileReader("profession_train.txt"));
			input1 = new BufferedReader(new FileReader("part-00000"));
			String s;
			
			//Put all the profession list into a map
			/*while((s = input2.readLine())!= null)
			{
				Scanner input = new Scanner(s);
				String key = "";
				String word = "";
				
				word = input.next();
				while(word.length() == 0)
				{
					word = input.next();
				}
				//takes the place of a delimiter if : is in the word it knows to move on to prefessions and not names
				while(!(word.contains(":")) || !(input.hasNext()))
				{
					while(word.length() == 0)
					{
						word = input.next();
					}
					
					key = key + word;
					word = input.next();
				}
				
				//gets the value as the remaining terms  (professions) into a list
				String value = "";
				List <String> professions = new LinkedList<String>();
				if (input.hasNext())
				{
					
					StringTokenizer st = new StringTokenizer(input.nextLine(),",");
					while(st.hasMoreTokens())
					{
						professions.add(st.nextToken());
					}
				}
				
				Profmap.put(key, professions);
					
				
							
				input.close();
				
			}*/
			//iterator used to test the profession_train.txt
//			Iterator it = Profmap.entrySet().iterator();
//			while (it.hasNext())
//			{
//				Map.Entry<String,String> Pairs = (Map.Entry<String, String>)it.next();
//				System.out.println(Pairs.getKey() + ", " + Pairs.getValue());
//			}
			
			int count = 0;//count is used to test so it doesn't run through the entire data set this should be removed for the cluster run
			
			String s1 = "";
			
			//Brings the lemma index into a map
			while (((s1 = input1.readLine()) != null) && count < 20)//count < 20 should be removed when testing is done
			{
				String key = "";
				@SuppressWarnings("resource")
				//use of this Delimiter is the get the entire title
				Scanner st = new Scanner(s1).useDelimiter("[<]");
				if (st.hasNext())
				{
					key = st.next();
				}
				
				//use this delimiter to get each individual lemma frequency pair
				st.useDelimiter("[,<>]");
				
				
				int number = 0; //frequency
				String word = "";//Lemma
				
					
				Map<String,Integer> list = new HashMap<String,Integer>();//list used to store lemma frequency pairs
				
				//Note the code has some problem storing numbers but because I felt like our data scrubs numbers anyway it shouldn't matter
				while(st.hasNext())
				{
					
					word = st.next();
					
					while(!(st.hasNextInt())&& st.hasNext())
					{
						word = word + st.next();
					}
					
					if (st.hasNextInt())
					{
						number = st.nextInt();
					}
					if (word != null)
					{
						list.put(word,number);
					}
					
				}
				
				count++;
				articles.put(key, list);
				System.out.println(key);
				
				st.close();
				}
				
				//code used to test the inverted index lemma
				/*Iterator it = articles.entrySet().iterator();
				while (it.hasNext())
				{
					Map.Entry Pairs = (Map.Entry)it.next();
					String names = (String) Pairs.getKey();
					Iterator it2 = ((Map<String,Integer>) Pairs.getValue()).entrySet().iterator();
					System.out.print(names.toUpperCase()+ ": ");
					while(it2.hasNext())
					{
						Map.Entry<String, Integer> Pair = (Map.Entry<String, Integer>) it2.next();
						System.out.print(" (" + Pair.getKey() + "," + Pair.getValue() + ") ");
					}
					
					System.out.println();
				}*/
		}
		//The map program takes a String written in the people_train file and generates a probability
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			String line = value.toString();//line of text in this format First name Last name : Profession 1,[Profession 2],[Profession 3]
			Scanner input = new Scanner(line);//scanner scans the line of text
			input.useDelimiter(":"); //Delimiter reads to the left of the colon
			String person = input.next();
			input.useDelimiter(",");//After the : is reached reads the professions
			String [] profession = new String [3];
			profession[0] = input.next();//puts professions (up to 3) in an profession array
			
			if(input.hasNext())
			{
				profession[1] = input.next();
			}
			
			if (input.hasNext())
			{
				profession[2] = input.next();
			}
			
			input.close();
			
			for (int i = 0; i < 3 && profession[i] != null; i++)
			{
				Map<String,Integer> probabilities = new HashMap<String,Integer>();//intermediate map that contains lemma and counts of each lemma in an article
				int sum = 0;
				//unsure if extra space before the colon and after the name effects recall
				if (articles.containsKey(person.substring(0,person.length()-2)))//articles is the map that is set up in the setup that contains all the pages and lemmas
				{
					probabilities = articles.get(person.substring(0,person.length()-2));
				}
				
				else if (articles.containsKey(person))
				{
					probabilities = articles.get(person);
				}
				
				for (Integer values : probabilities.values()) //sums integers of all the lemmas
				{
					sum = sum + values;
				}
				
				if (prob.containsKey(profession[i]))//puts the new wikipedia page data into a map (prob) with all the lemmas for each profession and the counts of words
				{
					prob.get(profession[i]).putAll(probabilities);
					int currentcount = prob.get(profession[i]).get("*count");
					currentcount = currentcount + sum;
					prob.get(profession[i]).put("*count", currentcount);
					int profcount = prob.get(profession[i]).get("*profcount");
					profcount++;
					prob.get(profession[i]).put("*profcount",profcount);
				}
				
				else if (!(prob.containsKey(profession[i])))//if prob does not contain the profession puts a new entry for that profession
				{
					probabilities.put("*count", sum);
					probabilities.put("*profcount",1);
					prob.put(profession[i], probabilities);
				}
			
				
				if(profprobs.containsKey(profession[i]))//profprobs is information on the frequency of each profession and numpeople is the total number of professions
				{
					for (String profkeys : profprobs.keySet())//has to iterate through all the professions and redo the average
					{
						if (profkeys.equals(profession[i]))
						{
							double profsum = profprobs.get(profession[i])*numpeople;//since I don't know how to update the average at the end the average updates after each round 
							profsum = (profsum + 1)/((double) (numpeople + 1));
							profprobs.put(profession[i], profsum);
						}
						
						else
						{
							double profsum = profprobs.get(profession[i])*numpeople; 
							profsum = profsum/((double) (numpeople + 1));
							profprobs.put(profession[i], profsum);
						}
						
					}
					
				}
				
				else if(!(profprobs.containsKey(profession[i])))//same as the above if statement but creates a profession for one that is not included in the map
				{
					for (String profkeys : profprobs.keySet())
					{
						double profsum = profprobs.get(profession[i])*numpeople;
						profsum = profsum/((double) (numpeople + 1));
						profprobs.put(profession[i], profsum);
						
					}
					profprobs.put(profession[i], (double)(1)/((double) (numpeople + 1)));
				}
				
				numpeople++;
			}
			
			
			//This section begins the probability calculations section for the map ie. changing frequencies of lemmas to probabilities
			
			Map<String,Map<String,Double>> prob1 = new HashMap<String,Map<String,Double>>();
			
			
			for (String keys : prob.keySet())
			{
				int totalcount = prob.get(keys).get("*count");//gets the total word count for all articles in one profession
				
				Map<String,Double> lemmaprobs = new HashMap<String, Double>(); //map for including lemma and corresponding probabilities
				for (String lemma : prob.get(keys).keySet())
				{
					double logProbability = Math.log((double)(prob.get(keys).get(lemma))/((double)(totalcount)));//gets the log probability of getting any given word.  
					lemmaprobs.put(lemma, logProbability);
				}
				prob1.put(keys, lemmaprobs);//puts the lemma probabitity map into a map with the corresponding keys
				
			}
			
			
			context.write(profprobs,prob1);//writes the output which includes profprobs (probability of each profession happening) and lemma probas (probablitily of a given lemma given a profession)
			
			
		}

	}
}