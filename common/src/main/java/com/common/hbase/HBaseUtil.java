package com.common.hbase;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseUtil.class);

    private static Configuration conf = null;
    private static HConnection conn = null;

    /**
     * 初始化配置
     */
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum",
                PropertiesUtil.getPropertyString("hbase.zookeeper.quorum"));
        conf.set("hbase.zookeeper.property.clientPort",
                PropertiesUtil.getPropertyString("hbase.zookeeper.property.clientPort"));
        conf.set("hbase.zookeeper.master", "192.168.1.12:60000");
        try {
            conn = HConnectionManager.createConnection(conf);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

    }

    /**
     * 创建 HBase nameSpace
     *
     * @param namespace xx:yy
     */
    public static void createNamespace(String namespace) {
        try {
            HBaseAdmin admin = new HBaseAdmin(conf);
            admin.createNamespace(NamespaceDescriptor.create(namespace).build());
            admin.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 根据table name 返回HBase HTableInterface对象，可以执行CRUD操作
     * 使用完毕之后，需要调用HTableInterface.close()
     *
     * @param tableName
     * @return
     */
    public static HTableInterface getTable(String tableName) {
        HTableInterface table = null;
        try {
            table = conn.getTable(tableName);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return table;
    }

    public static void save(List<Put> puts, String tableName) {
        LOGGER.info("start  save.....");
        // 获取表名
        HTableInterface table = null;
        try {
            table = getTable(tableName);
            // table.put(puts);
            Object[] batch = table.batch(puts);

            System.out.println(batch.length);
            table.put(puts);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭htable
            closeTable(table);
        }
        LOGGER.info("end save......");
    }

    /**
     * 单个put保存方法，返回失败的rowkey
     *
     * @param put
     * @param tableName
     * @return
     */
    public static String saveSingle(Put put, String tableName) {
        // 获取表名
        HTableInterface table = null;
        try {
            table = getTable(tableName);
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
            return Bytes.toString(put.getRow());
        } finally {
            // 关闭htable
            closeTable(table);
        }
        return "";
    }

    public static void save(Put put, String tableName) {
        List<Put> ps = new ArrayList<Put>();
        ps.add(put);
        save(ps, tableName);
    }

    /**
     * 通过rawKey查询??
     *
     * @param tableName 表名
     * @param rawKey 行健
     * @return
     */
    public static Map<String, String> queryRow(String tableName, String rowKey) {
        HTableInterface table = null;
        Map<String, String> result = null;
        System.out.println("queryRow :" + rowKey);
        byte[] b = toBytes(rowKey);
        for (int i = 0; i < b.length; i++)
            System.out.println(b[i]);
        try {
            table = getTable(tableName);
            Get get = new Get(toBytes(rowKey));
            Result rs = table.get(get);
            result = resultHandle(rs);
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            closeTable(table);
        }
        return result;
    }

    /**
     * 将结果封装成map
     *
     * @param rs
     * @return
     */
    private static Map<String, String> resultHandle(Result rs) {
        Map<String, String> result = new HashMap<String, String>();
        Cell[] cells = rs.rawCells();
        result = new HashMap<String, String>();
        boolean isFirst = true;
        for (Cell cell : cells) {
            if (isFirst) {
                result.put("rowKey", toString(CellUtil.cloneRow(cell)));
                result.put("timeStamp", cell.getTimestamp() + "");
                isFirst = false;
            }
            // System.out.println(new
            // Date(cell.getTimestamp()).toLocaleString());;
            result.put(toString(CellUtil.cloneQualifier(cell)),

                    toString(CellUtil.cloneValue(cell)));
        }
        return result;
    }

    /**
     * 扫描整张?
     *
     * @param tableName
     * @return
     */
    public static List<Map<String, String>> scan(String tableName) {
        HTableInterface table = null;
        List<Map<String, String>> rsList = null;
        try {
            table = getTable(tableName);
            Scan scan = getScan(null, null);
            ResultScanner rss = table.getScanner(scan);
            Map<String, String> map = null;
            rsList = new LinkedList<Map<String, String>>();
            for (Result rs : rss) {
                map = resultHandle(rs);
                rsList.add(map);
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            closeTable(table);
        }
        return rsList;
    }

    /**
     * Ren.ych 2014.12.25 通过RowKey前缀查询数据,并删除重复数据
     *
     * @param tableName 表名
     * @param rowPrifix 前缀
     */

    public static int deleteFileByScanPrefixFilter(String tablename, String rowPrifix) {
        HTableInterface table = null;

        int count = 0;
        try {
            table = getTable(tablename);

            Scan s = new Scan();
            s.setFilter(new PrefixFilter(rowPrifix.getBytes()));
            ResultScanner rss = table.getScanner(s);

            for (Result rs : rss) {

                Cell[] cells = rs.rawCells();

                for (Cell cell : cells) {

                    if (toString(CellUtil.cloneQualifier(cell)).equals("repeatFileRowKey")) {

                        String value = toString(CellUtil.cloneValue(cell));

                        if (value.contains(",")) {

                            String rowKeys[] = value.split(",");
                            // 根据文件标题删除重复的文件，留最后一个
                            if (rowKeys != null && rowKeys.length > 0) {

                                for (int i = 0; i < rowKeys.length - 1; i++) {

                                    table.delete(new Delete(toBytes(rowKeys[i])));

                                    count = count + 1;

                                }
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }
        return count;

    }

    /**
     * @Title: closeTable
     * @Description: 关闭HTableInterface或�把HTableInterface放回连接
     * @param @param table 设定文件
     * @return void 返回类型
     * @throws
     */
    private static void closeTable(HTableInterface table) {
        if (table == null)
            return;
        try {
            table.close();
        } catch (Exception e) {
            System.out.println("关闭" + table.getName() + "失败!!!!!!");
            e.printStackTrace();
        }
    }

    /**
     * @Title: closeAdmin
     * @Description: 关闭HBaseAdmin
     * @param @param admin
     * @return void 返回类型
     * @throws
     */
    private static void closeAdmin(HBaseAdmin admin) {
        if (admin == null)
            return;
        try {
            admin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title: createTable
     * @Description: 创建张表
     * @param @param tableName 表名
     * @param @param families 列族
     * @return void 返回类型
     * @throws
     */
    public static void createTable(String tableName, String... families) {

        System.out.println("始创建表" + tableName);
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(conf);
            if (tableExists(tableName, admin)) {
                System.out.println("表：" + tableName + "已经存在");
                return;
            }
            TableName name = TableName.valueOf(tableName);
            HTableDescriptor desc = new HTableDescriptor(name);
            desc.setDurability(Durability.SYNC_WAL);
            if (families != null && families.length > 0) {

                for (int i = 0; i < families.length; i++) {
                    desc.addFamily(new HColumnDescriptor(families[i]));
                }
            }

            admin.createTable(desc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAdmin(admin);
        }

        System.out.println("创建表：" + tableName + " 成功！！");
    }

    /**
     * 判断表是否存在
     *
     * @param tableName
     * @param admin
     * @return
     * @throws IOException
     */
    private static boolean tableExists(String tableName, HBaseAdmin admin) throws IOException {

        boolean exists = false;
        if (admin.tableExists(tableName))
            exists = true;
        return exists;
    }

    /**
     * 判断表是否存在
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static boolean tableExists(String tableName) throws IOException {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(conf);
            if (!tableExists(tableName, admin)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAdmin(admin);
        }
        return true;
    }

    /**
     * @Title: delete
     * @Description: 根据行健删除表中的某行或多行
     * @param @param tableName 表名
     * @param @param rowKeys 行健
     * @return void 返回类型
     * @throws
     */
    public static void deleteRow(String tableName, String... rowKeys) {

        HTableInterface table = null;
        try {
            table = getTable(tableName);
            if (rowKeys != null && rowKeys.length > 0) {

                for (String rowKey : rowKeys) {
                    table.delete(new Delete(toBytes(rowKey)));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }
    }

    /**
     * @Title: toBytes
     * @Description: 字符串转字节数组 编码格式为UTF-8
     * @param @param str 要转换的字符
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     */
    public static byte[] toBytes(String str) {

        byte[] bytes = null;
        if (str == null)
            str = "";
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * @Title: toString
     * @Description: 字节数组转字符串
     * @param @param arr
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     */
    private static String toString(byte[] arr) {

        String charset = "UTF-8";
        String returnVal = null;
        try {
            returnVal = new String(arr, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnVal;
    }

    /**
     * @Title: getTable
     * @Description: 通过表名获取
     * @param @param tableName
     * @param @return
     * @param @throws IOException 设定文件
     * @return HTableInterface 返回类型
     * @throws
     */
    // private static HTableInterface getTable(String tableName) throws
    // IOException{
    // return conn.getTable( tableName );
    //
    // }

    public static void clearTable(String tableName) {

        System.out.println("始清除表" + tableName);
        HTableInterface table = null;
        List<Delete> deletes = new LinkedList<Delete>();
        try {
            table = getTable(tableName);
            Scan scan = getScan(null, null);
            ResultScanner scanner = table.getScanner(scan);
            for (Result rs : scanner) {
                Cell[] cells = rs.rawCells();
                for (Cell cell : cells) {
                    byte[] rowKey = CellUtil.cloneRow(cell);
                    deletes.add(new Delete(rowKey));
                }

            }
            table.delete(deletes);
            System.out.println("清除表：" + tableName + "成功！！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }
    }

    /**
     * @Title: getScan
     * @Description: 通过始rowkey和结束rowKey获取扫描
     * @param @param StartRow 始rowkey
     * @param @param endRow 结束rowkey
     * @param @return 设定文件
     * @return Scan 返回类型
     * @throws
     */
    public static Scan getScan(String StartRow, String endRow) {

        Scan scan = new Scan();
        if (StartRow != null)
            scan.setStartRow(toBytes(StartRow));
        if (endRow != null)
            scan.setStopRow(toBytes(endRow));
        // 设置缓存数量
        scan.setCaching(1000);
        // 启缓
        scan.setCacheBlocks(true);
        return scan;
    }

    /**
     * @Title: getGetList
     * @Description: 通过行健集合获取get集合
     * @param @param rowKeys 行健集合
     * @param @return 设定文件
     * @return List<Get> 返回类型
     * @throws
     */
    private static List<Get> getGetList(List<byte[]> rowKeys) {

        Get get = null;
        List<Get> gets = new LinkedList<Get>();
        for (byte[] arr : rowKeys) {
            // System.out.println(toString(arr));
            get = new Get(arr);
            gets.add(get);
        }
        return gets;

    }

    /**
     * scanByPage,分页查询
     *
     * @param tableName 表名
     * @param StartRow 始行
     * @param endRow 结束行健
     * @param currentPae 当前 默认1
     * @param pageSize 分页大小 默认100
     * @param filters 过滤器，可以设置多个
     * @return List封装的map对象
     */
    public static List<Map<String, String>> scanByPage(String tableName, String StartRow,
                                                       String endRow, Integer currentPae,
                                                       Integer pageSize, Filter... filters) {

        HTableInterface table = null;
        List<byte[]> rowKeyList = new LinkedList<byte[]>();
        List<Map<String, String>> result = new LinkedList<Map<String, String>>();
        try {
            // 格式化输入信
            if (currentPae == null || currentPae == 0)
                currentPae = 1;
            if (pageSize == null || pageSize == 0)
                pageSize = 100;
            // 计算分页的数据范
            int firstCount = (currentPae - 1) * pageSize;
            int maxCount = firstCount + pageSize - 1;
            table = getTable(tableName);
            Scan scan = getScan(StartRow, endRow);
            scan.setMaxVersions();
            if (filters != null) {
                for (int i = 0; i < filters.length; i++) {
                    scan.setFilter(filters[i]);
                }
            }
            // TODO 只取rowkey
            ResultScanner scanner = table.getScanner(scan);
            int i = 0;
            // 获取分页数据的rowKey
            for (Result rs : scanner) {
                byte[] rowKey = null;
                if (i >= firstCount && i <= maxCount) {
                    rowKey = rs.getRow();
                    rowKeyList.add(rowKey);
                }
                i++;
            }
            // 通过行健集合构建查询集合
            List<Get> getList = getGetList(rowKeyList);
            Result[] rss = table.get(getList);
            Map<String, String> rsMap = null;
            for (Result rs : rss) {
                rsMap = resultHandle(rs);
                result.add(rsMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }
        return result;

    }

    public static int getAllCount(String tableName, String StartRow, String endRow) {
        int count = 0;
        HTableInterface table = null;
        try {
            table = getTable(tableName);
            Scan scan = getScan(StartRow, endRow);
            ResultScanner scanner = table.getScanner(scan);
            for (@SuppressWarnings("unused")
            Result rs : scanner) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void main(String[] args) {
        // System.out.println("234|1f3dfca9199042b39bfc7de13232cd22|1|20141218135423".length());
        String name = "dataSource";
        List<Map<String, String>> ls = scan(name);
        for (Map<String, String> map : ls) {
            // System.out.println(map.get("rowKey"));
            System.out.println(map.get("title"));
            System.out.println(map.get("message"));
        }
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     */
    public static void deleteTable(String tableName) {

        LOGGER.info("开始删除表" + tableName);
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(conf);
            if (!tableExists(tableName, admin)) {
                LOGGER.info("表：" + tableName + "不存在");
                return;
            }
            TableName name = TableName.valueOf(tableName);
            admin.disableTable(name);
            admin.deleteTable(name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAdmin(admin);
        }
        LOGGER.info("删除表：" + tableName + " 成功！！");
    }

    /**
     * 保存list封装的map对象。map中包括rowKey，各个字段等
     *
     * @param tableName
     * @param lists
     * @return map对象，包括count：数据总量，failedCount失败总量
     */
    public static Map<String, Integer> saveListMap(String tableName, List<Map<String, String>> lists) {
        int failedCount = 0;
        for (Map<String, String> map : lists) {

            Put put = new Put(map.get("rowKey").toString().getBytes());
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                if (key.equals("rowKey") || key.equals("timeStamp")) {
                    continue;
                }
                put.add("t".getBytes(), key.getBytes(), Bytes.toBytes(map.get(key)));
            }

            // 调用单个 put保存方法，返回失败的rowkey
            String rowKey = saveSingle(put, tableName);

            if (StringUtils.isNotBlank(rowKey)) {
                failedCount++;
            }
        }
        System.out.println("出错数量:" + failedCount);

        Map<String, Integer> results = new HashMap<String, Integer>();
        results.put("count", lists.size());
        results.put("failedCount", failedCount);
        return results;
    }



}
