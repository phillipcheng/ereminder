call mvn -f ../pom.xml install -Dmaven.test.skip=true 
call mvn -f ../stockpersist/pom.xml install -Dmaven.test.skip=true 
call mvn -f pom.xml install -Dmaven.test.skip=true
call ant -f build.xml dist