/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xormlite.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * <pre>
 *     desc   : 数据库操作服务
 *     author : xuexiang
 *     time   : 2018/5/8 上午12:07
 * </pre>
 */
public class DBService<T> {

    private Context mContext;
    /**
     * 数据库操作Dao
     */
    private Dao<T, Integer> mDao;
    /**
     * 数据库打开助手
     */
    private OrmLiteSqliteOpenHelper mSqliteOpenHelper;
    /**
     * 数据库连接
     */
    private DatabaseConnection mConnection;
    private Savepoint mSavePoint;

    /**
     * 构造应用内部的数据库
     *
     * @param context
     * @param clazz           数据库表实体类型
     * @param dbName          数据库名
     * @param databaseVersion 数据库版本号
     * @param iDatabase       默认数据库操作接口
     */
    public DBService(Context context, Class<T> clazz, String dbName, int databaseVersion, IDatabase iDatabase) throws SQLException {
        mContext = context.getApplicationContext();
        mSqliteOpenHelper = new DefaultDBHelper(mContext, dbName, databaseVersion, iDatabase);
        mDao = mSqliteOpenHelper.getDao(clazz);
    }

    /**
     * 构造应用外部的数据库
     *
     * @param context
     * @param clazz             数据库表实体类型
     * @param dbDirPath         数据库文件存放的根目录
     * @param dbName            数据库文件的名称
     * @param databaseVersion   数据库版本号
     * @param iExternalDataBase 应用外部的数据库接口
     */
    public DBService(Context context, Class<T> clazz, String dbDirPath, String dbName, int databaseVersion, IExternalDataBase iExternalDataBase) throws SQLException {
        mContext = context.getApplicationContext();
        mSqliteOpenHelper = new ExternalDBHelper(mContext, dbDirPath, dbName, databaseVersion, iExternalDataBase);
        mDao = mSqliteOpenHelper.getDao(clazz);
    }

    /************************************************* 插入 **********************************************/
    /**
     * 插入单条数据
     *
     * @param object
     * @return 1
     * @throws SQLException
     */
    public int insert(T object) throws SQLException {
        return mDao.create(object);
    }

    /**
     * 插入单条数据(返回被插入的对象）
     *
     * @param object
     * @return
     * @throws SQLException
     */
    public T insertData(T object) throws SQLException {
        return mDao.createIfNotExists(object);
    }

    /**
     * 批量添加，返回插入数量的集合
     *
     * @param collection
     * @return
     * @throws SQLException
     */
    public int insertDatas(Collection<T> collection) throws SQLException {
        return mDao.create(collection);
    }

    /************************************************* 查询 **********************************************/
    /**
     * 使用迭代器查询所有
     */
    public List<T> queryAllData() throws IOException {
        List<T> dataList = new ArrayList<T>();
        CloseableIterator<T> iterator = mDao.closeableIterator();
        try {
            while (iterator.hasNext()) {
                T data = iterator.next();
                dataList.add(data);
            }
        } finally {
            iterator.close();
        }
        return dataList;
    }

    /**
     * 查询所有的数据
     */
    public List<T> queryAll() throws SQLException {
        return mDao.queryForAll();
    }

    /**
     * 查询所有的数据并根据列名（1个）进行排序，返回一个对象集合
     *
     * @param columnName 排序的列名
     * @param ascending  true：升序，false：降序
     * @return
     * @throws SQLException
     */
    public List<T> queryAllOrderBy(String columnName, boolean ascending) throws SQLException {
        return mDao.queryBuilder().orderBy(columnName, ascending).query();
    }

    /**
     * 查询所有的数据并根据列名（2个）进行排序，返回一个对象集合
     *
     * @param columnName1 排序的列名1
     * @param ascending1  true：升序，false：降序
     * @param columnName2 排序的列名2
     * @param ascending2  true：升序，false：降序
     * @return
     * @throws SQLException
     */
    public List<T> queryAllOrderBy(String columnName1, boolean ascending1, String columnName2, boolean ascending2) throws SQLException {
        return mDao.queryBuilder().orderBy(columnName1, ascending1).orderBy(columnName2, ascending2).query();
    }

    /**
     * 有条件的排序查询，返回一个对象集合
     *
     * @param fieldName  查询条件列名
     * @param value      查询条件列名的值
     * @param columnName 排序的列名
     * @param ascending  true：升序，false：降序
     * @return
     * @throws SQLException
     */
    public List<T> queryAndOrderBy(String fieldName, Object value, String columnName, boolean ascending) throws SQLException {
        QueryBuilder<T, Integer> query = mDao.queryBuilder();
        query.where().eq(fieldName, value);
        query.orderBy(columnName, ascending);
        return query.query();
    }

    /**
     * 根据id查询出一条数据
     *
     * @param id 查询的id
     */
    public T queryById(Integer id) throws SQLException {
        return mDao.queryForId(id);
    }

    /**
     * 根据条件查询(一个条件) 返回一个对象集合
     *
     * @param fieldValues 查询条件的集合
     */
    public List<T> queryByField(Map<String, Object> fieldValues) throws SQLException {
        return mDao.queryForFieldValuesArgs(fieldValues);
    }

    /**
     * 根据条件查询(一个条件) 返回一个对象集合，和方法{@link DBService#queryByColumn}一样的效果
     *
     * @param fieldName 查询条件列名
     * @param value     查询条件列名的值
     */
    public List<T> queryByField(String fieldName, Object value) throws SQLException {
        return mDao.queryForEq(fieldName, value);
    }

    /**
     * 根据条件查询(一个条件) 返回一个对象集合，和方法{@link DBService#queryByField}一样的效果
     *
     * @param columnName 查询条件列名
     * @param value      查询条件列名的值
     */
    public List<T> queryByColumn(String columnName, Object value) throws SQLException {
        return mDao.queryBuilder().where().eq(columnName, value).query();
    }

    /**
     * 根据条件查询(一个条件) 返回第一个符合条件的对象
     *
     * @param columnName 查询条件列名
     * @param value      查询条件列名的值
     */
    public T queryForColumnFirst(String columnName, Object value) throws SQLException {
        return mDao.queryBuilder().where().eq(columnName, value).queryForFirst();
    }

    /**
     * 根据条件查询(二个条件)，返回一个对象集合
     *
     * @param columnName1 查询条件列名1
     * @param value1      查询条件列名1的值
     * @param columnName2 查询条件列名2
     * @param value2      查询条件列名2的值
     */
    public List<T> queryByColumn(String columnName1, Object value1, String columnName2, Object value2) throws SQLException {
        return mDao.queryBuilder().where().eq(columnName1, value1).and().eq(columnName2, value2).query();
    }

    /**
     * 根据条件查询(二个条件)，返回第一个符合条件对象
     *
     * @param columnName1 查询条件列名1
     * @param value1      查询条件列名1的值
     * @param columnName2 查询条件列名2
     * @param value2      查询条件列名2的值
     */
    public T queryForColumnFirst(String columnName1, Object value1, String columnName2, Object value2) throws SQLException {
        return mDao.queryBuilder().where().eq(columnName1, value1).and().eq(columnName2, value2).queryForFirst();
    }

    /**
     * 根据条件模糊查询，返回一个对象集合
     *
     * @param columnName 模糊查询的条件列名
     * @param value      模糊查询的列名值
     */
    public List<T> indistinctQueryForColumn(String columnName, Object value) throws SQLException {
        return mDao.queryBuilder().where().like(columnName, "%" + value + "%").query();
    }

    /**
     * 精准查询+模糊查询，返回一个对象集合
     *
     * @param columnName1 精准查询的条件列名
     * @param value1      精准查询的列名值
     * @param columnName2 模糊查询的条件列名
     * @param value2      模糊查询的列名值
     */
    public List<T> indistinctQueryForColumn(String columnName1, Object value1, String columnName2, Object value2) throws SQLException {
        return mDao.queryBuilder().where().eq(columnName1, value1).and().like(columnName2, "%" + value2 + "%").query();
    }

    /**
     * 根据sql语句查询，返回String[]的集合
     *
     * @param sql 查询的sql语句
     */
    public List<String[]> queryDataBySql(String sql) throws SQLException {
        GenericRawResults<String[]> rawResults = mDao.queryRaw(sql);
        return rawResults.getResults();
    }

    /**
     * 根据sql语句查询，返回对象的集合
     *
     * @param sql          查询的sql语句
     * @param rawRowMapper 将查询的结果映射为对象的接口
     */
    public List<T> queryDataBySql(String sql, RawRowMapper<T> rawRowMapper) throws SQLException {
        GenericRawResults<T> rawResults = mDao.queryRaw(sql, rawRowMapper);
        return rawResults.getResults();
    }

    /************************************************* 更新 **********************************************/
    /**
     * 使用对象更新一条记录（注意：对象必须带唯一标识ID,且该方法不能更新ID字段)
     *
     * @param object 更新的对象
     * @return 更新记录的数量
     */
    public int updateData(T object) throws SQLException {
        return mDao.update(object);
    }

    /**
     * 根据某一条件更新对象
     *
     * @param updateColumnName 更新列名
     * @param updateValue      更新值
     * @param columnName       更新条件列名
     * @param value            更新条件值
     * @return 更新记录的数量
     */
    public int updateDataByColumn(String updateColumnName, Object updateValue, String columnName, Object value) throws SQLException {
        String sql = mDao.updateBuilder().updateColumnValue(updateColumnName, updateValue).where().eq(columnName, value).prepare().getStatement();
        return updateDataBySQL(sql);
    }

    /**
     * 根据某一条件更新对象的多列属性
     *
     * @param fieldValues 更新列名和更新值的集合
     * @param columnName  更新条件列名
     * @param value       更新条件值
     * @return 更新记录的数量
     */
    public int updateDataByColumn(Map<String, Object> fieldValues, String columnName, Object value) throws SQLException {
        UpdateBuilder<T, Integer> updates = mDao.updateBuilder();
        for (String key : fieldValues.keySet()) {
            updates.updateColumnValue(key, fieldValues.get(key));
        }
        String sql = updates.where().eq(columnName, value).prepare().getStatement();
        return updateDataBySQL(sql);
    }

    /**
     * 根据条件做update时直接使用sql语句进行更新
     *
     * @param sql 更新的SQL语句【必须包含关键字INSERT，DELETE，UPDATE】
     */
    public int updateDataBySQL(String sql) throws SQLException {
        return mDao.updateRaw(sql);
    }

    /************************************************* 删除 **********************************************/
    /**
     * 根据对象删除一条记录（注意：对象必须带唯一标识ID,否则方法不起作用)
     *
     * @param object 删除的对象
     */
    public int deleteData(T object) throws SQLException {
        return mDao.delete(object);
    }

    /**
     * 批量删除（注意：对象必须带唯一标识ID,否则方法不起作用)
     * <p>大数据量的删除不起左右，会报too many SQL variables错误</p>
     *
     * @param datas
     */
    public int deleteDatas(Collection<T> datas) throws SQLException {
        return mDao.delete(datas);
    }

    /**
     * 删除所有数据
     */
    public int deleteAll() throws SQLException {
        return executeSql("DELETE from " + mDao.getTableName());
    }

    /**
     * 根据id删除一条记录
     *
     * @param id
     * @throws SQLException
     */
    public int deleteById(Integer id) throws SQLException {
        return mDao.deleteById(id);
    }

    /**
     * 根据id删除一条记录
     *
     * @param id
     * @throws SQLException
     */
    public int deleteById(int id) throws SQLException {
        return mDao.deleteById(id);
    }

    /**
     * 执行sql语句
     *
     * @param sql 执行的sql语句
     */
    public int executeSql(String sql) throws SQLException {
        return mDao.executeRaw(sql);
    }

    // ========================Builder===============================//

    /**
     * 返回QueryBuilder
     */
    public QueryBuilder<T, Integer> getQueryBuilder() {
        return mDao.queryBuilder();
    }

    /**
     * 返回UpdateBuilder
     */
    public UpdateBuilder<T, Integer> getUpdateBuilder() {
        return mDao.updateBuilder();
    }

    /**
     * 返回DeleteBuilder
     */
    public DeleteBuilder<T, Integer> getDeleteBuilder() {
        return mDao.deleteBuilder();
    }

    // ========================其他操作===============================//

    /**
     * 序列化数据库对象的信息
     *
     * @param data
     * @return
     */
    public String objectToString(T data) {
        return mDao.objectToString(data);
    }

    public Dao<T, Integer> getDao() {
        return mDao;
    }

    /************************************************* 事务操作 **********************************************/

    /**
     * 执行事务操作
     */
    public <T> T doInTransaction(final Callable<T> callable) throws SQLException {
        return TransactionManager.callInTransaction(mSqliteOpenHelper.getConnectionSource(), callable);
    }

    /**
     * 开启数据库事务操作
     */
    public Savepoint beginTransaction(String savepoint) throws SQLException {
        mConnection = mDao.startThreadConnection();
        return mSavePoint = mConnection.setSavePoint(savepoint);
    }

    /**
     * 提交事务
     */
    public void commit(Savepoint savepoint) throws SQLException {
        if (mConnection != null) {
            mConnection.commit(savepoint);
            mDao.endThreadConnection(mConnection);
        }
    }

    /**
     * 提交事务
     */
    public void commit() throws SQLException {
        if (mConnection != null) {
            mConnection.commit(mSavePoint);
            mDao.endThreadConnection(mConnection);
        }
    }

    /**
     * 事务回滚
     */
    public void rollBack(Savepoint savepoint) throws SQLException {
        if (mConnection != null) {
            mConnection.rollback(savepoint);
            mDao.endThreadConnection(mConnection);
        }
    }

    /**
     * 事务回滚
     */
    public void rollBack() throws SQLException {
        if (mConnection != null) {
            mConnection.rollback(mSavePoint);
            mDao.endThreadConnection(mConnection);
        }
    }

}

