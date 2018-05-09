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

package com.xuexiang.xormlite.enums;

/**
 * <pre>
 *     desc   : 数据库的类型（内部还是外部)
 *     author : xuexiang
 *     time   : 2018/5/9 上午12:28
 * </pre>
 */
public enum DataBaseType {
    /**
     * 内部存储的数据库(数据库根目录路径固定为:/data/data/package/databases/)
     */
    INTERNAL,
    /**
     * 外部存储的数据库(数据库根目录路径默认为:/storage/emulated/0/Android/databases/xormlite/)
     */
    EXTERNAL

}
