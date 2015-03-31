HADOOP_DIR=/data/hadoop-2.5.2

mkdir ./hadooplibback

cp $HADOOP_DIR/share/hadoop/common/lib/httpclient-4.2.5.jar ./hadooplibback/
rm $HADOOP_DIR/share/hadoop/common/lib/httpclient-4.2.5.jar
cp ../lib/httpclient-4.3.jar $HADOOP_DIR/share/hadoop/common/lib/

cp $HADOOP_DIR/share/hadoop/common/lib/httpcore-4.2.5.jar ./hadooplibback/
rm $HADOOP_DIR/share/hadoop/common/lib/httpcore-4.2.5.jar
cp ../lib/httpcore-4.3.jar $HADOOP_DIR/share/hadoop/common/lib/

