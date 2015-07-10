#/bin/bash
#stock test
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
echo $CL


#all
java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.sites.test.TestSinaStock client1-v2-cluster.properties $1 $2 $3
