echo off
setlocal enableDelayedExpansion

set JAVA_HOME=c:\java\jre7\bin
set CL=
for %%X in (target\lib\*.jar) do set CL=!CL!;%%X

echo on
%JAVA_HOME%/java -Xmx512m -cp target/cld-datacrawl-1.0.0.jar;target/conf;%CL% org.cld.datacrawl.test.TestRedeploy

endlocal