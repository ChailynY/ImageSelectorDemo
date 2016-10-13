package com.example.com.imageselectordemo;

import java.io.File;

/**
 * Created by yuanqingying on 2016/10/10.

 相关点击事件的回调接口

 */
public interface Callback {

    //图片单选
    void onSingleImageSelected(String path);

    //多选的图片选中
    void onImageSelected(String path);

    //多选的图片取消选中
    void onImageUnselected(String path);

    //相机拍照的裁剪
    void onCameraShot(File imageFile);

    //？？？？图片相应的改变
    void onPreviewChanged(int select, int sum, boolean visible);

}
