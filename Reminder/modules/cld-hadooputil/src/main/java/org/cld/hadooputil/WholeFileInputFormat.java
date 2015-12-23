package org.cld.hadooputil;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

public class WholeFileInputFormat extends CombineFileInputFormat<Text, Text>{
	
	public WholeFileInputFormat(){
		super();
		setMaxSplitSize(64*1024*1024);//64M
	}

    @Override
    protected boolean isSplitable(JobContext context, Path file) {
        return false;
    }

	/**
	* Creates a CombineFileRecordReader to read each file assigned to this InputSplit.
	* Note, that unlike ordinary InputSplits, split must be a CombineFileSplit, and therefore
	* is expected to specify multiple files.
	*
	* @param split The InputSplit to read.  Throws an IllegalArgumentException if this is
	*        not a CombineFileSplit.
	* @param context The context for this task.
	* @return a CombineFileRecordReader to process each file in split.
	*         It will read each file with a WholeFileRecordReader.
	* @throws IOException if there is an error.
	*/

	@Override
	public RecordReader<Text, Text> createRecordReader(
	        InputSplit split, TaskAttemptContext context) throws IOException {
	    if (!(split instanceof CombineFileSplit)) {
          throw new IllegalArgumentException("split must be a CombineFileSplit");
        }
        return new CombineFileRecordReader<Text, Text>((CombineFileSplit) split, context, WholeFileRecordReader.class);
	}

}
