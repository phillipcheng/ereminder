HADOOP_DIR=/data/hadoop-2.5.2

$HADOOP_DIR/bin/hdfs dfs -rm /reminder/task/*
$HADOOP_DIR/mybin/sync-restart-yarn.sh
