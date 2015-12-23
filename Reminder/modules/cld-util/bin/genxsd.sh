export project_root=/Users/chengyi/Documents/ereminder/Reminder/modules

xjc -d $project_root/datastore/src/main/java -p org.xml.taskdef -b $project_root/shopping/src/main/resources/taskbinding_2_0.xml $project_root/shopping/src/main/resources/Task_2_0.xsd
