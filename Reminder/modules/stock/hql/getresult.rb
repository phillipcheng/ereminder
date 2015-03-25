import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Get;
import java.util.ArrayList;

def get_result(rowkey)
  htable = HTable.new(HBaseConfiguration.new, "crawledItem")
  puts "#{rowkey}\n"
  get = Get.new(rowkey.to_java_bytes)
  rs = htable.get(get)
  output = ArrayList.new
  if rs.isEmpty()
  	puts "isempty"
  else
    cell = rs.getColumnLatestCell("cf".to_java_bytes, "data".to_java_bytes) 
    ts = cell.getTimestamp()
    val = Bytes.toString(cell.getValue)
    puts " timestamp=#{ts}, value=#{val} \n"
	end
end
