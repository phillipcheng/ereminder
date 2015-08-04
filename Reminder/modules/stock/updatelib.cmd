set HADOOP_HOME=C:/java/hadoop-2.5.2
call %HADOOP_HOME%/bin/hadoop fs -mkdir -p /reminder/lib
call %HADOOP_HOME%/bin/hadoop fs -rm /reminder/lib/*
call %HADOOP_HOME%/bin/hadoop fs -put C:\mydoc\myprojects\ereminder\Reminder\modules\stock\target\lib\* /reminder/lib
