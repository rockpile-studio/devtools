package studio.rockpile.devtools.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HBaseDdlApiDemo {
    private Connection connection = null;
    private Admin admin = null;

    @Before
    public void setup() {
        try {
            // HBaseConfiguration config = new HBaseConfiguration(); 旧API接口写法
            Configuration configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", "192.168.4.80:2181");
            // admin = new HBaseAdmin(configuration); 旧API接口写法
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 判断表是否存在
    @Test
    public void tableOperateExist() {
        try {
            String tableName = "student";
            boolean exists = admin.tableExists(TableName.valueOf(tableName));
            System.out.println("是否存在表" + tableName + ":" + exists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tableTest() {
        try {
            createTable("user", "info", "role");
            System.out.println("表user创建成功");
            dropTable("user");
            System.out.println("表user删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dropTable(String tableName) throws Exception {
        TableName table = TableName.valueOf(tableName);
        if (!admin.tableExists(table)) {
            System.out.println(tableName + "表不存在");
            return;
        }
        admin.disableTable(table);
        admin.deleteTable(table);
    }

    private void createTable(String tableName, String... columnFamilies) throws Exception {
        if (columnFamilies.length <= 0) {
            throw new Exception("未指定表的列族信息");
        }
        TableName table = TableName.valueOf(tableName);
        if (admin.tableExists(table)) {
            System.out.println(tableName + "表已经存在");
            return;
        }
        // 创建表描述器，添加列族信息
        HTableDescriptor hTableDescriptor = new HTableDescriptor(table);
        for (String columnFamily : columnFamilies) {
            HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(columnFamily);
            hTableDescriptor.addFamily(hColumnDescriptor);
        }
        admin.createTable(hTableDescriptor);
    }

    @Test
    public void createNamespace() {
        String namespace = "myspace";
        try {
            NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespace).build();
            admin.createNamespace(namespaceDescriptor);
            System.out.println("命名空间创建成功");
        } catch (NamespaceExistException e) {
            System.out.println(namespace + "命名空间已经存在");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void shutdown() {
        try {
            if (admin != null) {
                admin.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
