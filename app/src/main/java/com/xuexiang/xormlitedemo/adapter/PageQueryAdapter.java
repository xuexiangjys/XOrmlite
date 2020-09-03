/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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
 *
 */

package com.xuexiang.xormlitedemo.adapter;

import android.view.View;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xormlite.db.DBService;
import com.xuexiang.xormlitedemo.R;
import com.xuexiang.xormlitedemo.db.entity.Student;

import java.sql.SQLException;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]<BR>
 *
 * @author xWx916614
 * @version [V9.1.0.1, 2020/9/3]
 * @since V9.1.0.1
 */
public class PageQueryAdapter extends SmartRecyclerAdapter<Student> {

    private DBService<Student> mDBService;

    public PageQueryAdapter(DBService<Student> dbService) {
        super(R.layout.adapter_list_student_item);
        mDBService = dbService;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Student model, final int position) {
        holder.text(R.id.tvId, String.valueOf(model.getId()));
        holder.text(R.id.tvName, model.getUserName());
        holder.text(R.id.tvAge, String.valueOf(model.getAge()));
        holder.text(R.id.tvSex, model.getSex());
        holder.click(R.id.tvUpdate, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(position);
            }
        });
        holder.click(R.id.tvDelete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });
    }

    public void update(int position) {
        try {
            Student student = getItem(position);
            student.setUserName("xxxx");
            student.setAge(19);
            student.setSex("女");
            mDBService.updateData(student);
            replaceNotNotify(position, student);
            notifyItemChanged(position);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int position) {
        try {
            Student student = getItem(position);
            mDBService.deleteData(student);
            getListData().remove(position);
            notifyItemRemoved(position);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
