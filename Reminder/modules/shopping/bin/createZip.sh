HADOOP_BIN=/data/hadoop-2.5.2/bin

mkdir ../zip
rm ../zip/reminder.zip
tar -cvf ../zip/reminder.zip -C ../lib .
$HADOOP_BIN/hdfs dfs -mkdir /reminder/zip
$HADOOP_BIN/hdfs dfs -rm /reminder/zip/reminder.zip
$HADOOP_BIN/hdfs dfs -copyFromLocal ../zip/reminder.zip /reminder/zip/
$HADOOP_BIN/hdfs dfs -rm /reminder/lib/*
$HADOOP_BIN/hdfs dfs -mkdir /reminder/lib
$HADOOP_BIN/hdfs dfs -copyFromLocal ../lib/* /reminder/lib/
