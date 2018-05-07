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
 *     desc   : 应用外部(如SD卡等)数据库打开助手
 *     author : xuexiang
 *     time   : 2018/5/8 上午12:18
 * </pre>
 */
public class ExternalDBHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 数据库路径
     */
    private String mDatabasePath;
    /**
     * 应用外部数据库 实现接口
     */
    private IExternalDataBase mIExternalDataBase;

    /**
     * @param context
     * @param databasePath      数据库的完整路径
     * @param databaseVersion   数据库版本号
     * @param iExternalDataBase 应用外部数据库 实现接口
     */
    public ExternalDBHelper(Context context, String databasePath, int databaseVersion, IExternalDataBase iExternalDataBase) {
        super(context, null, null, databaseVersion);
        mDatabasePath = databasePath;
        mIExternalDataBase = iExternalDataBase;

        mIExternalDataBase.createOrOpenDB(connectionSource);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        if (mIExternalDataBase != null) {
            mIExternalDataBase.onCreate(database, connectionSource);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (mIExternalDataBase != null) {
            mIExternalDataBase.onUpgrade(database, connectionSource, oldVersion, newVersion);
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(mDatabasePath, null,
                SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(mDatabasePath, null,
                SQLiteDatabase.OPEN_READWRITE);

    }

}
