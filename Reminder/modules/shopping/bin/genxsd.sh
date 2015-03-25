export project_root=/Users/chengyi/Documents/workspace/maven.1401627294964/Reminder/modules

xjc -d $project_root/taskmgr/src/main/java -p org.xml.taskdef -b $project_root/shopping/src/main/resources/taskbinding_2_0.xml $project_root/shopping/src/main/resources/Task_2_0.xsd