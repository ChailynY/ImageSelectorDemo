package com.example.com.imageselectordemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.com.imageselectordemo.adapter.ImgSelectAdapter;
import com.example.com.imageselectordemo.interfaces.OnImgItemClickListener;

import java.util.List;

public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    private  int DEFAULT_COLOR=Color.parseColor("#FF4081");//标题栏浸染的颜色

    private  TextView tvResult;
    private GridView imgGridview;


    private  ImgSelectAdapter mImgSelectAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //检验6.0的权限   PERMISSION_GRANTED  PERMISSION_DENIED 授予权限以及拒绝授予
        // Android 6.0 checkSelfPermission
        checkPermisiion();
        tvResult= (TextView) findViewById(R.id.tvResult);
        imgGridview = (GridView) findViewById(R.id.imgGridview);



    }

    private void initEvent(final List<String> pathList) {

        if(mImgSelectAdapter!=null){
            mImgSelectAdapter.setListener(new OnImgItemClickListener() {
                @Override
                public void onCheckedClick(String path) {
                    if(pathList.contains(path)){
                        pathList.remove(path);
                        Constant.imageList.remove(path);
                    }
                    mImgSelectAdapter.notifyDataSetChanged();


                }
            });
        }else{
            Log.i("yqy","此时的mImgSelectAdapter也是空的");
        }
    }


    private void checkPermisiion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//未被授予该权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    //多选
    public  void Multiselect(View view){
        if(Constant.imageList!=null &&Constant.imageList.size()<9){
            ImgSelConfig config = new ImgSelConfig.Builder(loader)
                    // 是否多选
                    .multiSelect(true)
                    // 确定按钮背景色
                    .btnBgColor(Color.WHITE)
                    // 确定按钮文字颜色
                    .btnTextColor(DEFAULT_COLOR)
                    // 使用沉浸式状态栏
                    .statusBarColor(DEFAULT_COLOR)
                    // 返回图标ResId
                    .backResId(R.mipmap.ic_back)
                    //标题
                    .title("图片")
                    .titleColor(Color.WHITE)
                    .titleBgColor(DEFAULT_COLOR)
                    //主动裁剪大小
                    .cropSize(1, 1, 200, 200)
                    //需要裁剪,选中单张的时候使用
                    .needCrop(false)
                    // 第一个是否显示相机,显示则表示可以拍照选中
                    .needCamera(true)
                    // 最大选择图片数量
                    .maxNum(9)
                    .build();


            MainActivity.startActivity(StartActivity.this,config,REQUEST_CODE);
        }else{
            Toast.makeText(StartActivity.this,"最多只能9张哦",Toast.LENGTH_LONG).show();
        }


    }
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    //单选
    public  void Single(View view){

        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // 确定按钮背景色
                .btnBgColor(Color.GRAY)
                // 确定按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(DEFAULT_COLOR)
                // 返回图标ResId
                .backResId(R.mipmap.ic_back)
                //标题
                .title("图片")
                .titleColor(Color.WHITE)
                .titleBgColor(DEFAULT_COLOR)
                //主动裁剪大小
                .cropSize(1, 1, 200, 200)
                //需要裁剪,选中单张的时候使用
                .needCrop(true)
                // 第一个是否显示相机,显示则表示可以拍照选中
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9)
                .build();

        //跳转到基本的图片选择界面

        MainActivity.startActivity(StartActivity.this,config,REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(MainActivity.INTENT_RESULT);

            // 测试Fresco。可不理会
//             draweeView.setImageURI(Uri.parse("file://"+pathList.get(0)));
            mImgSelectAdapter =new ImgSelectAdapter(StartActivity.this,pathList);

            imgGridview.setAdapter(mImgSelectAdapter);

            //点击事件
            initEvent(pathList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.imageList.clear();
    }
}
