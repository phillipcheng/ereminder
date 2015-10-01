export HADOOP_HOME=/data/hadoop-2.5.2
$HADOOP_HOME/bin/hadoop fs -rm /reminder/lib/*
$HADOOP_HOME/bin/hadoop fs -put /data/reminder/lib/* /reminder/lib
