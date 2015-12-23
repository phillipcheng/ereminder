mvn -f ../pom.xml install -Dmaven.test.skip=true 
mvn -f pom.xml install
ant -f build.xml dist
