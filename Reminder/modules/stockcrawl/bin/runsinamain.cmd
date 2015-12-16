Setlocal EnableDelayedExpansion

set HBASE_HOME=C:\dev\hbase-1.1.1
set CL=

for /r %%i in (../target/lib/*) do set CL=!CL!;%%i

echo %CL%

java -Xmx1024m -cp "%CL%;%HBASE_HOME%/conf" org.cld.stock.sina.RunSinaStock client1-v2.properties test true %1 %2 %3
