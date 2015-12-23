#/bin/bash
#stock test
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
CL=$CL:../lib

echo $CL


#all
java -Xmx6g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.stock.RunStock cld-stock-cluster.properties $1 $2 $3 $4 $5 $6 $7
