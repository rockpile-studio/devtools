package studio.rockpile.devtools.process.batch;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FlowDataCacheHolder {
    public static final String RAWS_INDEX_KEY_NAME = "rawsKey";
    private Map<String, GenericDataEntity> rawsHash = new HashMap<>();

    @Override
    public String toString() {
        return "FlowDataCacheHolder{" +
                "rawsHash=" + rawsHash +
                '}';
    }

    public Map<String, GenericDataEntity> getRawsHash() {
        return rawsHash;
    }

    public void setRawsHash(Map<String, GenericDataEntity> rawsHash) {
        this.rawsHash = rawsHash;
    }
}
