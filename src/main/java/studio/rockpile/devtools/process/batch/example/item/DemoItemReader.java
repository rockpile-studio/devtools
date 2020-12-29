package studio.rockpile.devtools.process.batch.example.item;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;
import java.util.List;

public class DemoItemReader implements ItemReader<String> {
    private List<String> buffer = null;
    private Iterator<String> iterator = null;

    public DemoItemReader(List<String> buffer) {
        this.buffer = buffer;
        this.iterator = this.buffer.iterator();
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // 一次读取一个数据
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }
}
