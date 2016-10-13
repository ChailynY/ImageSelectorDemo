package com.example.com.imageselectordemo.interfaces;

import com.example.com.imageselectordemo.bean.Image;

/**
 * Created by yuanqingying on 2016/10/8.
 */
public interface OnItemClickListener {

    //checkbox的点击事件
    int onCheckedClick(int position, Image image);

    //图片的点击事件
    void onImageClick(int position, Image image);
}
