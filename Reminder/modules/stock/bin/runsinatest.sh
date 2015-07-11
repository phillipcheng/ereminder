#/bin/bash
#stock test
CL=

for file in ../target/lib/*
do
	CL=$CL:$file
done
echo $CL


#all
java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.util.SingleJUnitTestRunner org.cld.stock.test.TestSinaStock#$1
