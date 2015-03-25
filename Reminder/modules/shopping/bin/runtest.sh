#/bin/bash
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
echo $CL
java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL" org.junit.runner.JUnitCore org.cld.booksites.test.TestHadoopTask 

