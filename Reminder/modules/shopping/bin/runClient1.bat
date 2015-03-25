echo off
setlocal enableDelayedExpansion

set JAVA_HOME=C:\Java\jdk1.7.0_67\bin
set CL=
for %%X in (..\target\lib\*.jar) do set CL=!CL!;%%X

echo on
%JAVA_HOME%/java -Xmx2048m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=C:\ddout -cp ../cfg/client;../target/cld-shopping-1.0.0.jar;%CL% org.cld.taskmgr.Main client1.properties

endlocal