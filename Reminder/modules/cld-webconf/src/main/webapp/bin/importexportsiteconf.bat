echo off
setlocal enableDelayedExpansion

set JAVA_HOME=C:\Java\jdk1.7.0_67\bin
set CL=
for %%X in (..\WEB-INF\lib\*.jar) do set CL=!CL!;%%X

echo on
%JAVA_HOME%/java -cp %CL% org.cld.crbook.util.ImportExportSiteConf %1 %2 %3 %4

endlocal