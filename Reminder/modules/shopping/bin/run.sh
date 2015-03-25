#/bin/bash
CL=

for file in ./lib/*
do
	CL=$CL:$file
done
echo $CL
java -Xmx2048m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/cy/shopping -cp "target/classes:$CL" org.cld.book.Main
