call mvn -f ../../modules/pom.xml install -Dmaven.test.skip=true
call mvn -f ../stockstrategy/pom.xml install -Dmaven.test.skip=true
call mvn -f ../stockpersist/pom.xml install -Dmaven.test.skip=true
call mvn -f ../stocketl/pom.xml install -Dmaven.test.skip=true
call mvn -f ../stockanalyze/pom.xml install -Dmaven.test.skip=true
call ant -f build.xml dist