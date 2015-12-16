call mvn -f ../pom.xml install -Dmaven.test.skip=true
call mvn -f ../stockstrategy/pom.xml install -Dmaven.test.skip=true
call mvn -f ../signpost-1.2.1.2/signpost-core/pom.xml install -Dmaven.test.skip=true
call mvn -f ../signpost-1.2.1.2/signpost-jetty9/pom.xml install -Dmaven.test.skip=true
call mvn -f pom.xml install -Dmaven.test.skip=true
call ant -f build.xml dist