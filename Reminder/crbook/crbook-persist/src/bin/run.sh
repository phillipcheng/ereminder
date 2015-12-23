#/bin/bash
CL=

for file in ./lib/*
do
	CL=$CL:$file
done

echo $CL

java -Xmx2048m -cp "../crbook-persist-1.0.0.jar:../test-classes:$CL" org.junit.runner.JUnitCore cy.crbook.persist.test.TestRun
