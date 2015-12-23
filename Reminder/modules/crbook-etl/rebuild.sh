mvn -f ../pom.xml install -Dmaven.test.skip=true
mvn -f pom.xml test
ant -f build.xml dist
