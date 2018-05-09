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

import java.io.File;

/**
 * <pre>
 *     desc   : 应用外部(如SD卡等)数据库打开助手
 *     author : xuexiang
 *     time   : 2018/5/8 上午12:18
 * </pre>
 */
public class ExternalDBHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 数据库存放的根目录
     */
    private String mDBDirPath;

    /**
     * 数据库文件的名称
     */
    private String mDBName;

    /**
     * 应用外部数据库 实现接口
     */
    private IExternalDataBase mIExternalDataBase;

    /**
     * @param context
     * @param dbDirPath         数据库存放的根目录
     * @param dbName            数据库文件的名称
     * @param databaseVersion   数据库版本号
     * @param iExternalDataBase 应用外部数据库 实现接口
     */
    public ExternalDBHelper(Context context, String dbDirPath, String dbName, int databaseVersion, IExternalDataBase iExternalDataBase) {
        super(context, null, null, databaseVersion);
        mDBDirPath = dbDirPath;
        mDBName = dbName;
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
        File dbFile = new File(mDBDirPath, mDBName);
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
        return SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null,
                SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        File dbFile = new File(mDBDirPath, mDBName);
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
        return SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null,
                SQLiteDatabase.OPEN_READWRITE);

    }

}
