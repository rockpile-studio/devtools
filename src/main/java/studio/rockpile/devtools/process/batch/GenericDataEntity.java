package studio.rockpile.devtools.process.batch;

import java.util.List;
import java.util.Map;

public class GenericDataEntity {
    private List<Map<String, Object>> dataSet;

    @Override
    public String toString() {
        return "GenericDataEntity{" +
                "dataSet=" + dataSet +
                '}';
    }

    public List<Map<String, Object>> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<Map<String, Object>> dataSet) {
        this.dataSet = dataSet;
    }
}
