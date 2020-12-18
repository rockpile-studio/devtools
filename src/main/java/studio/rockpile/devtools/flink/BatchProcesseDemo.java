package studio.rockpile.devtools.flink;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import studio.rockpile.devtools.flink.mapper.WordCountFlatMapper;

// flink批处理演示 word count
public class BatchProcesseDemo {
    public static void main(String[] args) {
        try {
            // 创建执行环境
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            // 从文件中读取数据
            String path = "D:/source/java/learning-flink/src/main/resources/hello.txt";
            DataSet<String> dataSet = env.readTextFile(path);
            // 对数据集进行处理
            // 按空格分词展开，按(word, 1)进行分词统计
            DataSet<Tuple2<String, Integer>> resultSet = dataSet.flatMap(new WordCountFlatMapper())
                    .groupBy(0) /*按第一个位置的word，进行分组*/
                    .sum(1); /*将第二个位置的数据进行求和*/
            resultSet.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
