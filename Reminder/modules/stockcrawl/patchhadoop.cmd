set HADOOP_HOME=C:/Java/hadoop-2.5.2
mkdir %HADOOP_HOME%/share/hadoop/common/backup
mv %HADOOP_HOME%/share/hadoop/common/lib/httpclient-4.3.3.jar %HADOOP_HOME%/share/hadoop/common/backup/
mv %HADOOP_HOME%/share/hadoop/common/lib/httpcore-4.3.3.jar %HADOOP_HOME%/share/hadoop/common/backup/
cp target/lib/httpclient-4.5.jar %HADOOP_HOME%/share/hadoop/common/lib/
cp target/lib/httpcore-4.4.1.jar %HADOOP_HOME%/share/hadoop/common/lib/
