package hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class WordCount {

    public static class WordCountMap
            extends Mapper<Object, Text, Text, IntWritable> {

        public void map(Object key,Text value, Context context) throws IOException, InterruptedException {
            //在此处写map代码
            String[] lines = value.toString().split(" ");
            for (String word : lines) {
                context.write(new Text(word), new IntWritable(1));
            }
        }
    }

    public static class WordCountReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //在此处写reduce代码
            int count=0;
            for (IntWritable cn : values) {
                count=count+cn.get();
            }
            context.write(key, new IntWritable(count));
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        //设置输入路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        //设置输出路径
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //设置实现map函数的类
        job.setMapperClass(WordCountMap.class);
        //设置实现reduce函数的类
        job.setReducerClass(WordCountReducer.class);

        //设置map阶段产生的key和value的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //设置reduce阶段产生的key和value的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //提交job
        job.waitForCompletion(true);

        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job,new Path(otherArgs[otherArgs.length - 1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
