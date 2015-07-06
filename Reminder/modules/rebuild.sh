mvn install -Dmaven.test.skip=true 
mvn -f shopping/pom.xml install -Dmaven.test.skip=true  
ant -f shopping/build.xml dist
$HADOOP_HOME/bin/hadoop fs -rm /reminder/lib/*
$HADOOP_HOME/bin/hadoop fs -put ./shopping/target/lib/* /reminder/lib/
