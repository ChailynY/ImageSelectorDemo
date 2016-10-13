package com.example.com.imageselectordemo.interfaces;

import com.example.com.imageselectordemo.bean.Folder;

/**
 * Created by yuanqingying on 2016/10/10.
 * 文件切换的接口
 */
public interface OnFolderChangeListener {
    void onChange(int position, Folder folder);
}
