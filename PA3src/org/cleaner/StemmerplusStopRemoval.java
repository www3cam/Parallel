package org.cleaner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import Stemmers.tartarus.snowball.SnowballProgram;
import Stemmers.tartarus.snowball.SnowballStemmer;

public class StemmerplusStopRemoval {

	public static final Set<String> stopWords = new HashSet<String>();

	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		BufferedReader reader = new BufferedReader(new FileReader("Dependencies/stopword (1).txt"));
		String currentLine = null;
		while ((currentLine = reader.readLine()) != null) {
			stopWords.add(currentLine);
		}
		reader.close();
		
		BufferedReader br = new BufferedReader(new FileReader("Data/input_pa3_random.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("Data/output_pa3_radom.txt"));
		
		String line = "";
		Class stemClass = Class.forName("Stemmers.tartarus.snowball.ext.englishStemmer");
		SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
		
		
		while((line = br.readLine())!= null)
		{
			StringTokenizer st = new StringTokenizer(line," ,.:;?!+=_`\n\t\r-|<>@#$%^&*()[]{}\"\\/1234567890");
			while (st.hasMoreTokens()) {
				String token = st.nextToken().toLowerCase();
				// take out the stop words
				if (!stopWords.contains(token)) {
					stemmer.setCurrent(token);
					stemmer.stem();
					bw.write(stemmer.getCurrent()+ " ");
					
				}
			}

		}
		
		br.close();
		bw.close();

	}

}
