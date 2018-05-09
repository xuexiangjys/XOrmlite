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

package com.xuexiang.xormlitedemo.db;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xuexiang.xormlite.db.IDatabase;
import com.xuexiang.xormlite.logs.DBLog;
import com.xuexiang.xormlitedemo.db.entity.Student;

import java.sql.SQLException;

/**
 * <pre>
 *     desc   : 应用内部数据库 实现接口
 *     author : xuexiang
 *     time   : 2018/5/9 下午11:52
 * </pre>
 */
public class InternalDataBase implements IDatabase {
    /**
     * 数据库创建
     *
     * @param database         SQLite数据库
     * @param connectionSource 数据库连接
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Student.class);// 创建检查信息表
        } catch (SQLException e) {
            DBLog.e(e);
        }
    }

    /**
     * 数据库升级和降级操作
     *
     * @param database         SQLite数据库
     * @param connectionSource 数据库连接
     * @param oldVersion       旧版本
     * @param newVersion       新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        DBLog.i("数据库旧版本:" + oldVersion);
        DBLog.i("数据库新版本:" + newVersion);
    }
}
