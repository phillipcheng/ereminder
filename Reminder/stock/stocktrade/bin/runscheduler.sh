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
java -Xmx4g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.stock.StockCrawlScheduler 
