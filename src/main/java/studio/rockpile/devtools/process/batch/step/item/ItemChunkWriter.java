package studio.rockpile.devtools.process.batch.step.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import studio.rockpile.devtools.process.batch.FlowDataCacheHolder;
import studio.rockpile.devtools.process.batch.GenericDataEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component(value = "itemChunkWriter")
public class ItemChunkWriter implements ItemWriter<Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(ItemChunkWriter.class);
    @Autowired
    private FlowDataCacheHolder rawsCache;

    @Override
    public void write(List<? extends Map<String, Object>> items) throws Exception {
        Map<String, GenericDataEntity> rawsHash = rawsCache.getRawsHash();
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            String rawKey = item.get(FlowDataCacheHolder.RAWS_INDEX_KEY_NAME).toString();
            if (rawsHash.containsKey(rawKey)) {
                GenericDataEntity raws = rawsHash.get(rawKey);
                List<Map<String, Object>> dataSet = raws.getDataSet();
                if (dataSet == null) {
                    dataSet = new ArrayList<>();
                    dataSet.add(item);
                    raws.setDataSet(dataSet);
                } else {
                    dataSet.add(item);
                }
            } else {
                GenericDataEntity raws = new GenericDataEntity();
                List<Map<String, Object>> dataSet = new ArrayList<>();
                dataSet.add(item);
                raws.setDataSet(dataSet);
                rawsHash.put(rawKey, raws);
            }
        }
        // 输出 RAWS CACHE结果
        Iterator<Map.Entry<String, GenericDataEntity>> iterator = rawsHash.entrySet().iterator();
        while( iterator.hasNext() ){
            Map.Entry<String, GenericDataEntity> next = iterator.next();
            logger.debug("item step writer rawKey: {}", next.getKey());
            List<Map<String, Object>> dataSet = next.getValue().getDataSet();
            for( Map<String, Object> data : dataSet ){
                logger.debug("item step writer value: {}", data);
            }
        }
    }
}
