package studio.rockpile.devtools.registry;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DemoService {
    private String message;
    private DataSource dataSource;
    private List<Map<String, Object>> dataSet = new ArrayList<>();
    private List<Map<String, Object>> dataShared;

    @Override
    public String toString() {
        return "DemoService{" +
                "message='" + message + '\'' +
                ", dataSource=" + dataSource +
                ", dataSet=" + dataSet +
                ", dataShared=" + dataShared +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Map<String, Object>> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<Map<String, Object>> dataSet) {
        this.dataSet = dataSet;
    }

    public List<Map<String, Object>> getDataShared() {
        return dataShared;
    }

    public void setDataShared(List<Map<String, Object>> dataShared) {
        this.dataShared = dataShared;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("调用DemoService::finalize()");
        super.finalize();
    }
}
