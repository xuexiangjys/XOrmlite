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

import com.j256.ormlite.support.ConnectionSource;

/**
 * <pre>
 *     desc   : 应用外部数据库 实现接口
 *     author : xuexiang
 *     time   : 2018/5/7 下午10:29
 * </pre>
 */
public interface IExternalDataBase extends IDatabase {

    /**
     * 创建或者打开数据库
     *
     * @param connectionSource
     */
    void createOrOpenDB(ConnectionSource connectionSource);
}
