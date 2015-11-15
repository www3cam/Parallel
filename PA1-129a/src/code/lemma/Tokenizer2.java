
/*
 * Consider other ways of removing stop words - zipf's law
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Tokenizer2 {
	// build a hash set of stop words

	public static void main(String[] args) throws IOException
	{
		Tokenizer2 token = new Tokenizer2("Englishstop.txt");
		Scanner input = new Scanner(new File("Made_To_Stick-libre.txt"));
		BufferedWriter write = new BufferedWriter(new FileWriter("output.txt"));
		while (input.hasNext())
		{
			List<String> Words = token.tokenize(input.nextLine());
			while (!Words.isEmpty())
			{
				String str = Words.remove(0);
				write.write(str + " ");
			}
			
		}
		
	}
	public final Set<String> stopWords = new HashSet<String>();

	public Tokenizer2(String stopwordFilePath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				stopwordFilePath)));
		String currentLine = null;
		while ((currentLine = reader.readLine()) != null) {
			stopWords.add(currentLine);
		}
		reader.close();
	}
/**
 * Tokenizing sentence into a list of words
 * @param sentence
 * @return result
 * @throws FileNotFoundException
 */
	public List<String> tokenize(String sentence) throws FileNotFoundException {
		List<String> result = new ArrayList<String>();

		// separate the words into tokens and take out unnecessary symbols
		StringTokenizer st = new StringTokenizer(sentence," ,.:;?!+=_`\n\t\r-|<>@#$%^&*()[]{}\"\\/1234567890");
		// ...I removed it below using the lemmatizer.
		while (st.hasMoreTokens()) {
			String token = st.nextToken().toLowerCase();
			// take out the stop words
			if (!stopWords.contains(token)) {
				// lemmatizing input token
				String word = lemmatizing(token);
				if (word.length()>1) {
					if (!word.substring(0,1).matches("[a-z]+"))
						word = word.substring(1);
					if (!word.substring(word.length()-1, word.length()).matches("[a-z]+"))
						word = word.substring(0, word.length()-1);
				}
				result.add(word);
			}
		}
		return result;
	}

/**
 * This function determines if the input character is a vowel letter
 * @param c
 * @return
 */
	private boolean isVowel(char c) {
		return (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
	}

	/**
	 * This function determines if the input character is a stop or nasal
	 * consonant
	 */
	private boolean isConsonant(char c) {
		return (c == 'p' || c == 't' || c == 'k' || c == 'b' || c == 'd'
				|| c == 'g' || c == 'm' || c == 'n');
	}

	/**
	 * This function lemmatizes the input token
	 */
	private String lemmatizing(String token) {
		int len = token.length();
		//remove apostrophe('s)
		if (len > 2 && token.substring(len - 2, len).equals("'s"))
			token = token.substring(0, len - 2);
		
		//remove apostrophe(')
		else if (token.substring(len - 1).equals("'"))
			token = token.substring(0, len - 1);

		// plurality and present tense removal
		else if (len > 2 && token.charAt(len - 1) == 's') {
			// eg. cherries -> cherry
			if (len > 3 && token.substring(len - 3, len - 1).equals("ie"))
			{
				token = token.substring(0, len - 3) + "y";
			} else if (len > 3
					&& token.substring(len - 3, len - 1).equals("ve")) {
				// eg. leaves -> leaf
				if (len > 5 && isVowel(token.charAt(len - 4))
						&& isVowel(token.charAt(len - 5)))
				{
					token = token.substring(0, len - 3) + "f";
				}
				// eg. lives -> life
				else if (len > 3) {
					token = token.substring(0, len - 3) + "fe";
				}
			}
			// eg. boxes -> box, passes -> pass
			else if (len > 4
					&& token.charAt(len - 2) == 'e'
					&& token.charAt(len - 1) == 's'
					&& !(isVowel(token.charAt(len - 3)) && (token
							.charAt(len - 3) == 'x'
							|| token.charAt(len - 3) == 'z'
							|| token.charAt(len - 3) == 'o'
							|| token.substring(len - 4, len - 2).equals("sh")
							|| token.substring(len - 4, len - 2).equals("ss") || token
							.substring(len - 4, len - 2).equals("ch")))) 
			{
				token = token.substring(0, len - 2);
			}
			// eg. dogs -> dog, gets -> get
			else if (len > 2
					&& !(token.charAt(len - 2) == 's' || (token.charAt(len - 2) == 'i'))) {
				token = token.substring(0, len - 1);
			}

			// capturing small words
			else if (len < 4)
			{
				token = token.substring(0, len - 1);
			}

		}
		// past participle removal
		else if (len > 5 && token.charAt(len - 1) == 'd'
				&& token.charAt(len - 2) == 'e') {
			// eg. tanned -> tan
			if (token.charAt(len - 3) == token.charAt(len - 4)
					&& isConsonant(token.charAt(len - 3))
					&& isConsonant(token.charAt(len - 4)))
				token = token.substring(0, len - 3);
			// eg. carried -> carry
			else if (token.charAt(len - 3) == 'i')
				token = token.substring(0, len - 3) + "y";
			// eg. fired--> fire
			else if (isConsonant(token.charAt(len - 3))
					&& isVowel(token.charAt(len - 4))
					&& !(isVowel(token.charAt(len - 5)) || (len > 5 && token
							.substring(len - 5, len - 2).equals("ang")))
					|| (len > 5 && token.substring(len - 5, len - 2).equals(
							"ais")))
				token = token.substring(0, len - 1);
			// eg. showed -> show
			else
				token = token.substring(0, len - 2);
		}
		// gerund form removal
		else if (len > 5 && token.charAt(len - 3) == 'i'
				&& token.charAt(len - 2) == 'n' && token.charAt(len - 1) == 'g') {
			// eg. getting -> get
			if (len > 6 && token.charAt(len - 4) == token.charAt(len - 5)
					&& isConsonant(token.charAt(len - 4))
					&& isConsonant(token.charAt(len - 5)))
				token = token.substring(0, len - 4);
			// eg. leaving -> leave
			else if ((!(isVowel(token.charAt(len - 4)))
					&& isVowel(token.charAt(len - 5)) && !isVowel(token
						.charAt(len - 6)))
					|| token.equals("leaving")
					|| token.equals("housing")
					|| (len > 6 && token.substring(len - 6, len - 3).equals(
							"ang"))
					|| (len > 6 && token.substring(len - 6, len - 3).equals(
							"ais")))// ng is for words like changing --> change
				token = token.substring(0, len - 3) + "e";
			// eg. going -> go
			else
				token = token.substring(0, len - 3);
		}
		/* Because we felt like we didn't do enough work on the lemmatization section this following bit includes
		 * stemming that we would add if stemming is required.  Lemmatization changes only a few words and so we think if 
		 * stemming was required the below would be used\
		 //ly removal
		else if (len > 3 && token.substring(len - 2).equals("ly")) {
			// eg. happily --> happy
			if (token.charAt(len - 3) == 'i')
				token = token.substring(0, len - 3) + "y";
			// eg. quickly --> quick
			else
				token = token.substring(0, len - 2);
		}
		//ness removal
		else if (len > 5 && token.substring(len-4).equals("ness"))
		{
			//happiness --> happy
			if (token.charAt(len-5) == 'i')
			{
				token = token.substring(0,len-5) + "y";
			}
			
			lateness --> late
			else
			{
				token = token.substring(0,len-4);
			}
		}
		//ive removal
		else if (len > 6 && token.substring(len-3).equals("ive"))
		{
			additive --> add
			if (token.charAt(len-5) == 'i' && token.charAt(len-4) =='t')
			{
				token = token.substring(0,len-5);
			}
			
			addictive --> addict
			else
			{
				token = token.substring(0,len-3);
			}
		}
		
		//al removal
		else if (len > 5 && token.substring(len-2).equals("al"))
		{	
			removal --> remove
			if (!isVowel(token.charAt(len-3)) && isVowel(token.charAt(len-4)) && !isVowel(token.charAt(len -5)))
			{
				token = token.substring(0,len-2) + "e";
			}
			
		}
		//ful removal
		else if (len > 4 && token.substring(len-3).equals("ful"))
		{
		
			//pitiful --> pity
			if (token.charAt(len-4) == 'i')
			{
				token = token.substring(0,len-4) + "y";
			}
			
			//joyful --> joy
			else
			{
				token = token.substring(0,len-3);
			}
		}
		
		//able removal
		else if (len > 6 && token.substring(len-4).equals("able"))
		{
			
			//tokenizable --> tokenize
			if (!isVowel(token.charAt(len-5)) && isVowel(token.charAt(len-6)) && !isVowel(token.charAt(len -7)))
			{
				token = token.substring(0,len-4) + "e";
			}
			
			//habitable --> habit
			else
			{
				token = token.substring(0,len-4);
			}
		}
		//er removal
		else if (len > 4 && token.substring(len-2).equals("er"))
		{
			//tokenizer --> tokenize
			if (len > 5 && !isVowel(token.charAt(len-3)) && isVowel(token.charAt(len-4)) && !isVowel(token.charAt(len -5)))
			{
				token = token.substring(len-1);
			}
			//happier --> happy
			else if (token.charAt(len-3) == 'i')
			{
				token = token.substring(len -3) + "y";
			}
			
			//runner --> run
			else if (!isVowel(token.charAt(len-3)) && token.charAt(len-3) == token.charAt(len-4))
			{
				token = token.substring(len -3); 
			}
			
			//revealer --> reveal
			else 
			{
				token = token.substring(len-2);
			}
		
		}
		//ize removal
		else if (len > 5 && token.substring(len-3).equals("ize"))
		{
			
			if (!isVowel(token.charAt(len-4)) && token.charAt(len-4) == token.charAt(len-5))
			{
				token = token.substring(len-4);
			}
			
			else if ((len > 6 && !isVowel(token.charAt(len-4)) && isVowel(token.charAt(len-5)) && !isVowel(token.charAt(len -6))))
			{
				token = token.substring(len-3) + "e";
			}
			
			else
			{
				token = token.substring(len-3);
			}
		}*/
		
		return token;
		
		
	}
}