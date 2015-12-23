#/bin/bash
#stock test
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
CL=$CL:../lib

echo $CL


#all
java -Xmx4g -cp "$CL" org.cld.hadooputil.DumpHdfsFile $1 $2 $3 $4 $5 $6
