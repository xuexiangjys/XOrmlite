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
import android.text.TextUtils;

import com.j256.ormlite.support.ConnectionSource;
import com.xuexiang.xormlite.db.IExternalDataBase;
import com.xuexiang.xormlite.logs.DBLog;
import com.xuexiang.xutil.file.FileUtils;

import java.io.File;

/**
 * <pre>
 *     desc   : 外部存储的数据库 实现接口
 *     author : xuexiang
 *     time   : 2018/5/10 上午12:08
 * </pre>
 */
public class ExternalDataBase extends InternalDataBase implements IExternalDataBase {

    private int mDatabaseVersion;
    private String mDBPath;
    private String mDBName;

    public ExternalDataBase(String dbPath, String dbName, int databaseVersion) {
        mDBPath = dbPath;
        mDBName = dbName;
        mDatabaseVersion = databaseVersion;
    }

    /**
     * 创建或者打开数据库
     *
     * @param connectionSource
     */
    @Override
    public void createOrOpenDB(ConnectionSource connectionSource) {
        String dbFilePath = FileUtils.getFilePath(mDBPath, mDBName);
        if (FileUtils.createOrExistsFile(dbFilePath)) {
            SQLiteDatabase db = null;
            try {
                db = SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
            } catch (Exception e) {
                DBLog.e(e);
            }
            if (db != null) {
                int oldVersionCode = db.getVersion();
                if (oldVersionCode != mDatabaseVersion) { //版本不一致需要操作
                    if (oldVersionCode == 0) {
                        onCreate(db, connectionSource);
                    } else {
                        if (oldVersionCode < mDatabaseVersion) {
                            onUpgrade(db, connectionSource, oldVersionCode, mDatabaseVersion);
                        }
                    }
                }
                db.setVersion(mDatabaseVersion);
            }
        }
    }

}
