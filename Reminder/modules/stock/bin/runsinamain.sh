#/bin/bash
#stock test
CL=

for file in ../target/lib/*
do
	CL=$CL:$file
done
echo $CL


#all
java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.stock.test.TestSinaStock client1-v2.properties $1 $2 $3
