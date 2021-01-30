package com.xuexiang.xormlite.db;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据库工具类
 *
 * @author xuexiang
 * @since 2019/3/14 下午11:22
 */
public final class DataBaseUtils {

    private DataBaseUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 通过数据库表类名集合创建表
     *
     * @param connectionSource 连接池
     * @param tableClassNames  数据库表的类名集合
     * @throws SQLException
     */
    public static void createTablesByClassNames(ConnectionSource connectionSource, List<String> tableClassNames) throws SQLException {
        if (tableClassNames != null && tableClassNames.size() > 0) {
            for (String tableClassName : tableClassNames) {
                try {
                    TableUtils.createTableIfNotExists(connectionSource, Class.forName(tableClassName));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过数据库表类集合创建表
     *
     * @param connectionSource 连接池
     * @param tableClasses     数据库表的类集合
     * @throws SQLException
     */
    public static void createTablesByClasses(ConnectionSource connectionSource, List<Class<?>> tableClasses) throws SQLException {
        if (tableClasses != null && tableClasses.size() > 0) {
            for (Class<?> tableClass : tableClasses) {
                TableUtils.createTableIfNotExists(connectionSource, tableClass);
            }
        }
    }

    /**
     * 通过数据库表类集合创建表
     *
     * @param connectionSource 连接池
     * @param tableClasses     数据库表的类集合
     * @throws SQLException
     */
    public static void createTablesByClasses(ConnectionSource connectionSource, Class[] tableClasses) throws SQLException {
        if (tableClasses != null && tableClasses.length > 0) {
            for (Class<?> tableClass : tableClasses) {
                TableUtils.createTableIfNotExists(connectionSource, tableClass);
            }
        }
    }

}
