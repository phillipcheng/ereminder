#/bin/bash
#stock test
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
echo $CL


#all
java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.util.SingleJUnitTestRunner org.cld.sites.test.TestSinaStock#$1
