package studio.rockpile.devtools.hbase;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

public class HBaseDmlApiDemo {
    public final static Integer FETCH_NUM = 100;
    private Connection connection = null;

    @Before
    public void setup() {
        try {
            // HBaseConfiguration config = new HBaseConfiguration(); 旧API接口写法
            Configuration configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", "192.168.4.80:2181");
            // admin = new HBaseAdmin(configuration); 旧API接口写法
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dataOperateTest() {
        long ts = Calendar.getInstance().getTimeInMillis();
        String rowKey = String.valueOf(ts);
        String tableName = "student";
        String columnFamily = "info";
        try {
            query(tableName, "1607997151084", columnFamily, null);
            scan(tableName, "503002", "503004");

            save(tableName, rowKey, columnFamily, "name", "rockpile");
            save(tableName, rowKey, columnFamily, "age", "18");
            save(tableName, rowKey, columnFamily, "sex", "male");
            System.out.println("表数据记录Put成功，rowKey=" + rowKey);

            delete(tableName, rowKey, columnFamily, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delete4MultiVer(String tableName, String rowKey, String columnFamily, String columnQualifier) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        if (StringUtils.isNotEmpty(columnFamily)) {
            if (StringUtils.isEmpty(columnQualifier)) {
                delete.addFamily(Bytes.toBytes(columnFamily));
            } else {
                // Delete the latest version of the specified column.
                // find the latest versions timestamp.
                // Then it adds a delete using the fetched cells timestamp.
                delete.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier));
            }
        }
        table.delete(delete);
        System.out.println("表数据记录删除成功，rowKey=" + rowKey);
    }

    private void delete(String tableName, String rowKey, String columnFamily, String columnQualifier) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        if (StringUtils.isNotEmpty(columnFamily)) {
            if (StringUtils.isEmpty(columnQualifier)) {
                delete.addFamily(Bytes.toBytes(columnFamily));
            } else {
                // 使用delete.addColumns() 方法删除所有时间戳版本的数据
                // Delete all versions of the specified column.
                delete.addColumns(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier));
            }
        }
        table.delete(delete);
        System.out.println("表数据记录删除成功，rowKey=" + rowKey);
    }

    private void query(String tableName, String rowKey, String columnFamily, String columnQualifier) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        if (StringUtils.isNotEmpty(columnFamily)) {
            if (StringUtils.isEmpty(columnQualifier)) {
                get.addFamily(Bytes.toBytes(columnFamily));
            } else {
                get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier));
            }
        }
        // 设置获取数据的版本数
        get.setMaxVersions();

        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        for (int i = 0; i < cells.length; i++) {
            printCell(cells[i]);
        }
    }

    private void scan(String tableName, String startRow, String stopRow) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = null;
        if (StringUtils.isNotEmpty(startRow)) {
            if (StringUtils.isNotEmpty(stopRow)) {
                scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
            } else {
                scan = new Scan(Bytes.toBytes(startRow));
            }
        } else {
            scan = new Scan();
        }
        ResultScanner scanner = table.getScanner(scan);
        Result[] results = scanner.next(FETCH_NUM);
        for (int i = 0; i < results.length; i++) {
            Result result = results[i];
            Cell[] cells = result.rawCells();
            for (int j = 0; j < cells.length; j++) {
                printCell(cells[j]);
            }
        }
    }

    private void save(String tableName, String rowKey, String columnFamily, String columnQualifier, String value) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier), Bytes.toBytes(value));
        table.put(put);
    }

    private void printCell(Cell cell) {
        String row = Bytes.toString(CellUtil.cloneRow(cell));
        String family = Bytes.toString(CellUtil.cloneFamily(cell));
        String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
        String value = Bytes.toString(CellUtil.cloneValue(cell));
        System.out.print("row=" + row);
        System.out.println("   family=" + family + ", qualifier=" + qualifier + ", value=" + value);
    }

    @After
    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
