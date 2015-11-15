package code.articles;

import GetarticlesMapreduce;
import Mainclass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

/**
 * This class is used for Section A of assignment 1. You are supposed to
 * implement a main method that has first argument to be the dump wikipedia
 * input filename , and second argument being an output filename that only
 * contains articles of people as mentioned in the people auxiliary file.
 */
public class GetArticlesMapred {

	//@formatter:off
	/**
	 * Input:
	 * 		Page offset 	WikipediaPage
	 * Output
	 * 		Page offset 	WikipediaPage
	 * @author Tuan
	 *
	 */
	//@formatter:on
	public static class GetArticlesMapper extends Mapper<LongWritable, WikipediaPage, Text, Text> {
		public static Set<String> peopleArticlesTitles = new HashSet<String>();

		@Override
		protected void setup(Mapper<LongWritable, WikipediaPage, Text, Text>.Context context)
				throws IOException, InterruptedException {
			BufferedReader input = new BufferedReader(new FileReader("peoplefilelocation.txt"));//need to get location of file once connected to server
			String Line = "";
			while((Line = input.readLine())!=null)
			{
				peopleArticlesTitles.add(Line);
			}
			input.close();
			super.setup(context);
		}

		public void map(LongWritable offset, WikipediaPage inputPage, OutputCollector<Text, Text> output, Reporter arg3)
				throws IOException, InterruptedException {
			if (peopleArticlesTitles.contains(inputPage.getTitle()))
			{
				
				Text text = new Text(inputPage.getWikiMarkup());
				Text text2 = new Text(inputPage.getDocid());
				output.collect(text2, text);//don't know what to do without output
			
			}
		}
		
	}

	public static void main(String[] args) {
		// TODO: you should implement the Job Configuration and Job call
		// hereJobConf conf = new JobConf(Mainclass.class);
		JobConf conf = new JobConf(GetArticlesMapred.class);
		conf.setJobName("Text file");
		conf.setMapperClass(GetArticlesMapper.class);//why does this not work?
		// conf.setCombinerClass(WordCountReducer.class)  - optional
//		conf.setReducerClass(WordCountReducer.class);
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		//conf.setMapOutputValueClass(Text.class);to separate output and reducers

		conf.setInputFormat (TextInputFormat.class);
		conf.setOutputFormat (TextOutputFormat.class);

		FileInputFormat.addInputPath(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		JobClient.runJob(conf);
	}
}
