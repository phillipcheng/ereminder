echo off
setlocal enableDelayedExpansion

set JAVA_HOME=C:\Java\jdk1.7.0_67\bin
set CL=
for %%X in (..\target\lib\*.jar) do set CL=!CL!;%%X

echo on
%JAVA_HOME%/java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=C:\ddout -cp ../cfg/server;../target/test-classes;%CL% org.cld.taskmgr.Main server.properties

endlocal