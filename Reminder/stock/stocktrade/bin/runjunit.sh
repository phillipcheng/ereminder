#/bin/bash
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
echo $CL


#all
java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.junit.runner.JUnitCore $1 
