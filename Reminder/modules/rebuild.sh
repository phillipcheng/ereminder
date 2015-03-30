mvn install -Dmaven.test.skip=true 
mvn -f shopping/pom.xml install -Dmaven.test.skip=true  
ant -f shopping/build.xml dist