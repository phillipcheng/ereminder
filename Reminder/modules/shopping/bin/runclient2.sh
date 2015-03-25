#/bin/bash
CL=

for file in ./lib/*
do
	CL=$CL:$file
done
echo $CL
java -Xmx3072m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/chengyi/shopping -cp "target/classes/client:target/classes:$CL" org.cld.shopping.Main client1.properties
