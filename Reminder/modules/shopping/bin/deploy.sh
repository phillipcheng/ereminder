dist_dir=/cygdrive/c/mydoc/learn/CS/reminder/trunk/Reminder-v1/dist/v1
from_dir=/cygdrive/c/mydoc/learn/CS/reminder/trunk/Reminder-v1/modules

cp -f $from_dir/datacrawl/target/cld-datacrawl-1.0.0.jar $dist_dir/lib/cld-datacrawl-1.0.0.jar

cp -f $from_dir/datacrawlimpl/target/cld-datacrawl.impl-1.0.0.jar $dist_dir/cfg/cld-datacrawl.impl-1.0.0.jar

cp -f $from_dir/datastore/target/cld-datastore-1.0.0-engine.jar $dist_dir/lib/cld-datastore-1.0.0-engine.jar
cp -f $from_dir/datastore/target/cld-datastore-1.0.0-ext.jar $dist_dir/cfg/cld-datastore-1.0.0-ext.jar

cp -f $from_dir/shopping/target/lib/cld-common-1.0.0.jar $dist_dir/lib/cld-common-1.0.0.jar
cp -f $from_dir/shopping/target/lib/cld-taskmgr-1.0.0.jar $dist_dir/lib/cld-taskmgr-1.0.0.jar
cp -f $from_dir/shopping/target/lib/cld-util-1.0.0.jar $dist_dir/lib/cld-util-1.0.0.jar

cp -f $from_dir/shopping/target/cld-shopping-1.0.0.jar $dist_dir/cfg/cld-shopping-1.0.0.jar