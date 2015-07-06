HADOOP_DIR=/Applications/hadoop-2.6.0

mkdir ./hadooplibback

cp $HADOOP_DIR/share/hadoop/common/lib/httpclient-4.2.5.jar ./hadooplibback/
rm $HADOOP_DIR/share/hadoop/common/lib/httpclient-4.2.5.jar
cp ../target/lib/httpclient-4.3.3.jar $HADOOP_DIR/share/hadoop/common/lib/

cp $HADOOP_DIR/share/hadoop/common/lib/httpcore-4.2.5.jar ./hadooplibback/
rm $HADOOP_DIR/share/hadoop/common/lib/httpcore-4.2.5.jar
cp ../target/lib/httpcore-4.3.3.jar $HADOOP_DIR/share/hadoop/common/lib/

