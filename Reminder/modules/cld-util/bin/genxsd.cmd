set project_root=C:/mydoc/myprojects/ereminder/Reminder/modules

xjc -d %project_root%/cld-util/src/main/java -p org.xml.taskdef -b %project_root%/shopping/src/main/resources/taskbinding_2_0.xml %project_root%/shopping/src/main/resources/Task_2_0.xsd
