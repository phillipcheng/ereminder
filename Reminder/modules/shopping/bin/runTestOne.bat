echo off
setlocal enableDelayedExpansion

set JAVA_HOME=C:\Java\jdk1.7.0_67\bin
set CL=
for %%X in (..\target\lib\*.jar) do set CL=!CL!;%%X

echo on
%JAVA_HOME%/java -cp ../cfg/test;../target/cld-shopping-1.0.0.jar;../target/test-classes;%CL% org.junit.runner.JUnitCore org.cld.booksites.test.TestOne
endlocal