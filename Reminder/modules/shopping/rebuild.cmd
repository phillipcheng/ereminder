call mvn install -Dmaven.test.skip=true 
call mvn -f shopping/pom.xml install -Dmaven.test.skip=true  
call mvn -f shopping/pom.xml test
call ant -f shopping/build.xml dist