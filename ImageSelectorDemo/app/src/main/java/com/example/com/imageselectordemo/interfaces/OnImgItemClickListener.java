package com.example.com.imageselectordemo.interfaces;

import com.example.com.imageselectordemo.bean.Image;

/**
 * Created by yuanqingying on 2016/10/8.
 * 选中的图片删除事件
 *
 */
public interface OnImgItemClickListener {

    //checkbox的点击事件
    void onCheckedClick(String path);

}
