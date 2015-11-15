import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
public class Preprocessing {
	
	Map<String, String> uMap = new HashMap<String, String>();
	Map<String, String> pMap = new HashMap<String, String>();
	
	public Preprocessing() throws FileNotFoundException
	{
		BufferedReader reader1 = new BufferedReader(new FileReader(new File ("/Users/Yahui/Desktop/uOutput.txt")));
		BufferedReader reader2 = new BufferedReader(new FileReader(new File ("/Users/Yahui/Desktop/pOutput.txt")));
		
		String currentLine1 = "";
		while((currentLine1 = reader1.readLine())!=null) {
			//int, string
			uMap.put(currentLine1.substring(currentLine1.indexOf("\t") + 1).trim(), currentLine1.substring(0,currentLine1.indexOf("\t")).trim());
		}
		
		tring currentLine2 = "";
		while((currentLine2 = reader2.readLine())!=null) {
			//int, string
			pMap.put(currentLine2.substring(currentLine2.indexOf("\t") + 1).trim(), currentLine2.substring(0,currentLine2.indexOf("\t")).trim());
		}
	
	}

	public void convertitemid(String s)
	{
		pMap.
	}
	
}
