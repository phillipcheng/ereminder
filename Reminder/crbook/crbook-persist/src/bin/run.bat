echo off
setlocal enableDelayedExpansion

set JAVA_HOME=C:\Java\jdk1.7.0_60
set CL=
for %%X in (..\lib\*.jar) do set CL=!CL!;%%X

echo on

%JAVA_HOME%\bin\java -Xmx2048m -cp "..\crbook-persist-1.0.0.jar;..\test-classes;%CL%" org.junit.runner.JUnitCore cy.crbook.persist.test.TestRun
