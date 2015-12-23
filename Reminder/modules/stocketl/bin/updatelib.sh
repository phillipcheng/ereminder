export HADOOP_HOME=/data/hadoop-2.5.2
rm -f /reminder/lib/hadoop*
rm -f /reminder/lib/jdk*
rm -f /reminder/lib/jasper*
$HADOOP_HOME/bin/hadoop fs -mkdir -p /reminder/lib
$HADOOP_HOME/bin/hadoop fs -rm /reminder/lib/*
$HADOOP_HOME/bin/hadoop fs -put /data/reminder/lib/* /reminder/lib
