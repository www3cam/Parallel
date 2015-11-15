import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;
//code for converting letters and numbers into a long

public class convertertest 
{
	public static void main(String[] args) throws FileNotFoundException
	{
		//main is a test code for the convert and backwards convert
		Scanner input = new Scanner(new File("GroupNum/amazon_user.txt"));
		
		while(input.hasNext())
		{
			String a = input.next();
			System.out.print(a);
			System.out.print(", " + converta(a));
			System.out.print(", " + backconverta(converta(a)));
			System.out.println();
			
		}
		
		
		
		
	}



	public static BigInteger converta(String s)
	{
		//converts from string to a long
		BigInteger value = BigInteger.valueOf(0);
		
		for (int i = 0; i < s.length(); i++)
		{
			long c = (long) (s.charAt(i)) - 47;
			//System.out.println(c);
			value = value.add(BigInteger.valueOf((long) ((c)*Math.pow(42, i))));
			
		}
		
		return value;
	}
	
	public static String backconverta(BigInteger l)
	{
		//converts backwards from a long into a String
		String s = "";
		
		while (!l.equals(new BigInteger("0")))
		{
			char value = (char) ((l.mod(new BigInteger("42"))).add(new BigInteger("47")).intValue());
			s = s + value;
			l = l.divide(new BigInteger("42"));
			
		}
		
		return s;
	}
	
}