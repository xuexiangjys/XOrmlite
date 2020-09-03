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

package com.xuexiang.xormlitedemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xormlite.InternalDataBaseRepository;
import com.xuexiang.xormlite.db.DBService;
import com.xuexiang.xormlitedemo.R;
import com.xuexiang.xormlitedemo.adapter.PageQueryAdapter;
import com.xuexiang.xormlitedemo.db.entity.Student;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;

import java.sql.SQLException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]<BR>
 *
 * @author xWx916614
 * @version [V9.1.0.1, 2020/9/3]
 * @since V9.1.0.1
 */
@Page(name = "Sqlite分页查询")
public class PageQueryFragment extends XPageFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private DBService<Student> mDBService;
    private PageQueryAdapter mAdapter;

    private int mPageIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_page_query;
    }

    @Override
    protected void initViews() {
        mDBService = InternalDataBaseRepository.getInstance().getDataBase(Student.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter = new PageQueryAdapter(mDBService));
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final @NonNull RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.refresh(pageQuery(mPageIndex = 0));
                        refreshLayout.finishRefresh();
                    }
                }, 1000);
            }
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final @NonNull RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPageIndex++;
                        mAdapter.loadMore(pageQuery(mPageIndex));
                        refreshLayout.finishLoadMore();
                    }
                }, 1000);
            }
        });
        refreshLayout.autoRefresh();//第一次进
    }

    private List<Student> pageQuery(int pageIndex) {
        try {
            return mDBService.pageQuery("Id", true, pageIndex, 50);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
