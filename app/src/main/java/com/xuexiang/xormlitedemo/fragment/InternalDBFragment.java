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

package com.xuexiang.xormlitedemo.fragment;

import android.view.View;
import android.widget.ListView;

import com.xuexiang.xormlite.InternalDataBaseRepository;
import com.xuexiang.xormlite.db.DBService;
import com.xuexiang.xormlitedemo.R;
import com.xuexiang.xormlitedemo.adapter.StudentAdapter;
import com.xuexiang.xormlitedemo.db.entity.Student;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.BaseFragment;
import com.xuexiang.xutil.tip.ToastUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * <pre>
 *     desc   : 内部存储数据库
 *     author : xuexiang
 *     time   : 2018/5/10 上午12:49
 * </pre>
 */
@Page(name = "内部存储数据库")
public class InternalDBFragment extends BaseFragment {

    private DBService<Student> mDBService;

    @BindView(R.id.lv_data)
    ListView mLvData;

    private StudentAdapter mStudentAdapter;

    private List<Student> mTempList;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_db;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        mDBService = InternalDataBaseRepository.getInstance().getDataBase(Student.class);

        mStudentAdapter = new StudentAdapter(getContext(), null, mDBService);
        mLvData.setAdapter(mStudentAdapter);
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListeners() {
        mTempList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Student student = new Student();
            student.setUserName("xuexiang");
            student.setSex("男");
            student.setAge((int) (Math.random() * 100));
            student.setId(i);
            mTempList.add(student);
        }
    }

    @OnClick({R.id.btn_add, R.id.btn_query, R.id.btn_update, R.id.btn_delete, R.id.btn_add_by_transaction, R.id.btn_delete_by_transaction})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                Student student = new Student();
                student.setUserName("xuexiang");
                student.setSex("男");
                student.setAge((int) (Math.random() * 100));

                try {
                    mDBService.insert(student);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_query:
                try {
                    mStudentAdapter.updateList(mDBService.queryAll());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_update:
                try {
                    mDBService.updateDataByColumn("username", "xxxx", "username", "xuexiang");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                break;
            case R.id.btn_delete:
                try {
                    mDBService.deleteAll();
                    mStudentAdapter.updateList(mDBService.queryAll());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_add_by_transaction:
                try {
                    mDBService.doInTransaction(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            for (int i = 0; i < mTempList.size(); i++) {
                                mDBService.insert(mTempList.get(i));
                            }
                            return true;
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    ToastUtils.toast("事务执行失败！");
                }
                break;
            case R.id.btn_delete_by_transaction:
                try {
                    mDBService.doInTransaction(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            for (int i = 0; i < mTempList.size(); i++) {
                                mDBService.deleteData(mTempList.get(i));
                            }
                            return true;
                        }
                    });
                    mStudentAdapter.updateList(mDBService.queryAll());
                } catch (SQLException e) {
                    e.printStackTrace();
                    ToastUtils.toast("事务执行失败！");
                }
                break;
        }
    }
}
