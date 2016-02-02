call %HADOOP_HOME%/bin/hadoop fs -mkdir -p /reminder/lib
call %HADOOP_HOME%/bin/hadoop fs -rm /reminder/lib/*
call %HADOOP_HOME%/bin/hadoop fs -put C:\mydoc\myprojects\ereminder\Reminder\modules\shopping\target\lib\* /reminder/lib
call %HADOOP_HOME%/bin/hadoop fs -put C:\mydoc\myprojects\ereminder\Reminder\modules\cld-webconf\target\cldwebconf.jar /reminder/lib

