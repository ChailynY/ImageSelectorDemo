package com.example.com.imageselectordemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    public static Fragment[] mFragments;
    private FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;
    public static RadioGroup bottomRg;
    private RadioButton rbOne, rbTwo, rbThree,rbfour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        checkPermisiion();
        mFragments = new Fragment[4];
        fragmentManager = getSupportFragmentManager();
        mFragments[0] = fragmentManager.findFragmentById(R.id.fragment1);
        mFragments[1]  = fragmentManager.findFragmentById(R.id.fragment2);
        mFragments[2]  = fragmentManager.findFragmentById(R.id.fragment3);
        mFragments[3]  = fragmentManager.findFragmentById(R.id.fragment4);
        fragmentTransaction = fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]);
        fragmentTransaction.show(mFragments[0]).commit();
        bottomRg = (RadioGroup) findViewById(R.id.radioGroup1);
        rbOne = (RadioButton) findViewById(R.id.radio0);
        rbTwo = (RadioButton) findViewById(R.id.radio1);
        rbThree = (RadioButton) findViewById(R.id.radio2);
        rbfour = (RadioButton) findViewById(R.id.radio3);
//		dian = (LinearLayout) findViewById(R.id.dian);
//		cart = (TextView) findViewById(R.id.cart);
        bottomRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            int j = 1;//记录选择哪个按钮.

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                fragmentTransaction = fragmentManager.beginTransaction()
                        .hide(mFragments[0]).hide(mFragments[1])
                        .hide(mFragments[2]).hide(mFragments[3]);
                switch (checkedId) {
                    case R.id.radio0:
                        j = 1;//首页
                        fragmentTransaction.show(mFragments[0]).commit();
                        break;
                    case R.id.radio1:
                        j = 2;//商家
                        fragmentTransaction.show(mFragments[1]).commit();
                        break;

                    case R.id.radio2:
                        Log.i("yqy","消息消息消息=====2");
                        j = 3;//乐友
                        fragmentTransaction.show(mFragments[2]).commit();
                        break;
                    case R.id.radio3:
                        j = 4;//我的
                        fragmentTransaction.show(mFragments[3]).commit();
                        if(j==1){//首页
                            rbOne.setChecked(true);
                        }else if(j==2){//商家
                            rbTwo.setChecked(true);
                        }else if(j==3){//乐友
                            rbThree.setChecked(true);
                        }else if(j==4){//我的
                            rbfour.setChecked(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void checkPermisiion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//未被授予该权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}

