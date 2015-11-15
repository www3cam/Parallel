package org.myorg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;

public class Professionmapper {
	static BufferedReader input1;
	static BufferedReader input2;
	static Map<String,Map<String,Integer>> articles = new HashMap<String,Map<String, Integer>>();
	static Map<String,List<String>> Profmap = new HashMap<String,List<String>>();
	
	public static void main(String[] args) throws IOException
	{
		input2 = new BufferedReader(new FileReader("profession_train.txt"));
		input1 = new BufferedReader(new FileReader("part-00000"));
		String s;
		
		//Put all the profession list into a map
		while((s = input2.readLine())!= null)
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
			
		}
		//iterator used to test the profession_train.txt
//		Iterator it = Profmap.entrySet().iterator();
//		while (it.hasNext())
//		{
//			Map.Entry<String,String> Pairs = (Map.Entry<String, String>)it.next();
//			System.out.println(Pairs.getKey() + ", " + Pairs.getValue());
//		}
		
		int count = 0;//count is used to test so it doesn't run through the entire data set
		
		String s1 = "";
		
		//Brings the lemma index into a map
		while (((s1 = input1.readLine()) != null) && count < 20)
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

}
