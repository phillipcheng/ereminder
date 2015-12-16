export HADOOP_HOME=/Applications/hadoop-2.6.0
cp $HADOOP_HOME/share/hadoop/common/backup/*.jar $HADOOP_HOME/share/hadoop/common/lib/
rm $HADOOP_HOME/share/hadoop/common/lib/httpclient-4.5.jar
rm $HADOOP_HOME/share/hadoop/common/lib/httpcore-4.4.1.jar
