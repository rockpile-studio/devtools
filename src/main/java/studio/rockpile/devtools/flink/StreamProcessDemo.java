package studio.rockpile.devtools.flink;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import studio.rockpile.devtools.flink.mapper.WordCountFlatMapper;

// flink流处理演示 word count
public class StreamProcessDemo {
    public static void main(String[] args) {
        try {
            // 创建执行环境
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8); // 设置执行的并行度
            // 从文件中读取数据
            String path = "D:/source/java/learning-flink/src/main/resources/hello.txt";
            // DataStream<String> dataStream = env.readTextFile(path);

            // 用parameter tool工具从程序启动参数中提起配置项
            // Program arguments：--host 192.168.4.119 --port 23333
            ParameterTool paramTool = ParameterTool.fromArgs(args);
            String host = paramTool.get("host");
            int port = paramTool.getInt("port");

            // 模拟流式数据源，从netcat模拟数据源
            // # yum install -y nc
            // # nc -lk 23333 /*在linux主机上开一个socket端口，端口号23333*/
            DataStream<String> dataStream = env.socketTextStream("192.168.4.119", 23333);

            // 对数据流进行处理
            DataStream<Tuple2<String, Integer>> resultStream = dataStream.flatMap(new WordCountFlatMapper())
                    .keyBy(0) /*按一个位置的Key对数据做重分区的操作*/
                    .sum(1);

            resultStream.print();
            // 执行流处理任务
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
