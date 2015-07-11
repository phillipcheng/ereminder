export HADOOP_HOME=/Applications/hadoop-2.6.0
$HADOOP_HOME/bin/hadoop fs -rm /reminder/lib/*
$HADOOP_HOME/bin/hadoop fs -put ./target/lib/* /reminder/lib
