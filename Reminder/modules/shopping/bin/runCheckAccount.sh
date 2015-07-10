#/bin/bash
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
echo $CL


java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.sites.test.TestBase client1-v2-cluster.properties linkedin-company.xml checkAccount
