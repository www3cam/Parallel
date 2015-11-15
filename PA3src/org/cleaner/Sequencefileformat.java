package org.cleaner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile.Writer;
import org.apache.hadoop.io.SequenceFile;




/*
 *  most of the code gotten from this source: http://stackoverflow.com/questions/16070587/reading-and-writing-sequencefile-using-hadoop-2-0-apis
 *
 */

public class Sequencefileformat {

	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader("Data/output_pa3_radom.txt"));
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.getLocal(conf);
		Path seqFilePath = new Path("Data/output_sparse.txt");
		SequenceFile.Writer writer = SequenceFile.createWriter(conf, SequenceFile.Writer.file(seqFilePath), Writer.keyClass(IntWritable.class),Writer.valueClass(Map.class));
		int linenumber = 1;
		
		String line = "";
		
		while((line = bf.readLine()) != null )
		{
			Scanner input = new Scanner(line);
			Map<String,Integer> map = new HashMap<String,Integer>();
			
			while(input.hasNext())
			{
				String s = input.next();
				if (map.containsKey(s))
				{
					int value = map.get(s);
					value++;
					map.put(s, value);
				}
				else
				{
					map.put(s, 1);
				}
			}
		writer.append(new IntWritable(linenumber), map);
		linenumber++;
		input.close();
		}
		
		writer.close();
		bf.close();
	}

}


