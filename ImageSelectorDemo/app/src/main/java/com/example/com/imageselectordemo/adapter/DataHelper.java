package com.example.com.imageselectordemo.adapter;

import java.util.List;

/**
 * Created by yuanqingying on 2016/10/9.
 * 相关的数据处理
 */
public interface DataHelper<T> {

    boolean addAll(List<T> list);

    boolean addAll(int position, List<T> list);

    void add(T data);

    void add(int position, T data);

    void clear();

    boolean contains(T data);

    T getData(int index);

    void modify(T oldData, T newData);

    void modify(int index, T newData);

    boolean remove(T data);

    void remove(int index);
}
