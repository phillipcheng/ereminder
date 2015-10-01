call mvn -f ../pom.xml install -Dmaven.test.skip=true 
call mvn -f ../stock/pom.xml install -Dmaven.test.skip=true  
call mvn -f ../book/pom.xml install -Dmaven.test.skip=true  
call mvn -f ../../../crbook.modules/pom.xml install -Dmaven.test.skip=true 
call mvn install -Dmaven.test.skip=true