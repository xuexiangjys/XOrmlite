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

package com.xuexiang.xormlitedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 基类适配器
 *
 * @param <T>
 * @author xx
 */
public abstract class BaseContentAdapter<T> extends BaseAdapter {

    protected Context mContext;
    private List<T> mDataList;
    protected LayoutInflater mInflater;

    public BaseContentAdapter(Context context, List<T> list) {
        mContext = context.getApplicationContext();
        mInflater = LayoutInflater.from(mContext);
        mDataList = list;
    }

    public void updateList(List<T> dataList) {
        if (dataList != null) {
            if (mDataList == null) {
                mDataList = new ArrayList<>();
            } else {
                mDataList.clear();
            }
            mDataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList == null ? null : mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean existsData() {
        return mDataList != null && mDataList.size() > 0;
    }

    public void clear() {
        if (existsData()) {
            mDataList.clear();
        }
    }

    public void add(int position, T item) {
        if (item != null) {
            mDataList.add(position, item);
        }
    }

    public void add(T item) {
        if (item != null) {
            mDataList.add(item);
        }
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getConvertView(position, convertView, parent);
    }

    protected abstract View getConvertView(int position, View convertView, ViewGroup parent);

}
