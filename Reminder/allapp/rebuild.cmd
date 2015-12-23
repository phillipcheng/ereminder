call mvn -f ../build/env/pom.xml install
call mvn -f ../modules/pom.xml install -Dmaven.test.skip=true 
call mvn -f ../crbook/pom.xml install -Dmaven.test.skip=true  
call mvn -f ../stock/pom.xml install -Dmaven.test.skip=true  
call mvn install -Dmaven.test.skip=true