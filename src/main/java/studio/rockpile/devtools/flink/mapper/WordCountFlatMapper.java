package studio.rockpile.devtools.flink.mapper;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

// 自定义FlatMapFunction接口，FlatMapFunction<T, O>
// <T> Type of input elements，<O> Type of returned elements.
// 推荐使用flink定义的二元组类型 Tuple2
public class WordCountFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>> {
    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
        // 按空格分词
        String[] words = value.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            Tuple2<String, Integer> record = new Tuple2<>(word, 1);
            out.collect(record);
        }
    }
}
