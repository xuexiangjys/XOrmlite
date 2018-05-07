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
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

/**
 * <pre>
 *     desc   : 应用内部数据库打开助手【路径固定为:/data/data/package/databases/databaseName】
 *     author : xuexiang
 *     time   : 2018/5/8 上午12:13
 * </pre>
 */
public class DefaultDBHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 应用程序内部数据库的实现接口
     */
    private IDatabase mIDataBase;

    /**
     * @param context
     * @param databaseName    数据库名
     * @param databaseVersion 数据库版本号
     * @param iDatabase       db操作接口
     */
    public DefaultDBHelper(Context context, String databaseName, int databaseVersion, IDatabase iDatabase) {
        super(context, databaseName, null, databaseVersion);
        mIDataBase = iDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        if (mIDataBase != null) {
            mIDataBase.onCreate(db, connectionSource);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (mIDataBase != null) {
            mIDataBase.onUpgrade(db, connectionSource, oldVersion, newVersion);
        }
    }

    // 释放资源
    @Override
    public void close() {
        super.close();
    }

    public SQLiteDatabase getDatabase() {
        return getWritableDatabase();
    }

}
