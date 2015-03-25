#/bin/bash
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
echo $CL
java -Dlog4j.debug -Xmx3072m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "../cfg/client:../cld-shopping-1.0.0.jar:$CL" org.cld.taskmgr.Main client1.properties
