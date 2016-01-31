#/bin/bash
#stock test
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
CL=../conf:$CL:../lib

echo $CL


#all
java -Xmx4g -Dhttps.proxyHost=16.85.88.10 -Dhttps.proxyPort=8080 -Dcom.sun.management.jmxremote.port=9595 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=192.85.246.17 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.trade.AutoTrader
