#/bin/bash
CL=

for file in ../lib/*
do
	CL=$CL:$file
done
echo $CL

HBASE_HOME=/data/hbase-0.98.9-hadoop2


java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/reminder -cp "$CL:$HBASE_HOME/conf" org.cld.sites.test.TestBase client1-v2-cluster.properties linkedin-company.xml crawl bct https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=D,E&page_num=1::https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=F,G,H,I&page_num=1::https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=C&f_NFR=NFR2,NFR3,NFR5,NFR4&page_num=1::https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=C&f_NFR=NFR1&page_num=1::https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=B&page_num=1

