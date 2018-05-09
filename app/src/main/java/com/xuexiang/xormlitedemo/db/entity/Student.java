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

package com.xuexiang.xormlitedemo.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "student")
public class Student {

    @DatabaseField(generatedId = true)
    private long Id;
    @DatabaseField(columnName = "username")
    private String UserName;
    @DatabaseField(columnName = "age")
    private int Age;
    @DatabaseField(columnName = "sex")
    private String Sex;

    public long getId() {
        return Id;
    }

    public Student setId(long id) {
        Id = id;
        return this;
    }

    public String getUserName() {
        return UserName;
    }

    public Student setUserName(String userName) {
        UserName = userName;
        return this;
    }

    public int getAge() {
        return Age;
    }

    public Student setAge(int age) {
        Age = age;
        return this;
    }

    public String getSex() {
        return Sex;
    }

    public Student setSex(String sex) {
        Sex = sex;
        return this;
    }

    public String toString() {
        return "id:" + Id + ", username:" + UserName + ", age:" + Age + ", sex:" + Sex;
    }

}
