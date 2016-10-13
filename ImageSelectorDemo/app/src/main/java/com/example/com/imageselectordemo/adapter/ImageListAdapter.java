package com.example.com.imageselectordemo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.com.imageselectordemo.Constant;
import com.example.com.imageselectordemo.ImgSelConfig;
import com.example.com.imageselectordemo.R;
import com.example.com.imageselectordemo.adapter.EasyRVAdapter;
import com.example.com.imageselectordemo.adapter.EasyRVHolder;
import com.example.com.imageselectordemo.bean.Image;
import com.example.com.imageselectordemo.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by yuanqingying on 2016/10/9.
 * //默认显示拍照
 */
public class ImageListAdapter extends EasyRVAdapter<Image> {
    private  Context mContext;
    private OnItemClickListener listener;
    private ImgSelConfig config;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImageListAdapter(Context mContext, List<Image> mList, ImgSelConfig config) {
        super(mContext, mList, R.layout.item_home,R.layout.item_camera);
        this.config =config;
        this.mContext=mContext;
    }

    @Override
    protected void onBindData(final EasyRVHolder viewHolder, final int position, final Image item) {
        if(position==0){
            ImageView iv=viewHolder.getView(R.id.ivTakePhoto);
            iv.setImageResource(R.mipmap.ic_take_photo);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onImageClick(position,mList.get(position));
                    }
                }
            });
            return;
        }

        if(config.multiSelect){
                viewHolder.setVisible(R.id.ivPhotoCheaked, true);
                if (mList.contains(item.path)) {
                    viewHolder.setImageResource(R.id.ivPhotoCheaked, R.mipmap.ic_checked);
                } else {
                    viewHolder.setImageResource(R.id.ivPhotoCheaked, R.mipmap.ic_uncheck);
                }
            } else {
                viewHolder.setVisible(R.id.ivPhotoCheaked, false);
            }


        //大图点击事件
        viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!=null){
                    listener.onImageClick(position,item);
                }
            }
        });


        final ImageView iv =viewHolder.getView(R.id.imgview);//图片显示
        Glide.with(mContext).load(mList.get(position).path).into(iv);

        //选中的点击事件

        if(config.multiSelect) {
            viewHolder.getView(R.id.ivPhotoCheaked).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if (listener != null) {
                            int ret = listener.onCheckedClick(position, item);
                            //  0   1 返回0表示的是不添加的状态， 返回是1 表示的就是添加成功的状态
                            if (ret == 1) { //选中成功 局部刷新,
                                if (Constant.imageList.contains(item.path)) {//遍历相应的图片路径，对应上后选中的图片进行相应的标记
                                    viewHolder.setVisible(R.id.imgslect, true);
                                    viewHolder.setImageResource(R.id.ivPhotoCheaked, R.mipmap.ic_checked);
                                } else {
                                    viewHolder.setImageResource(R.id.ivPhotoCheaked, R.mipmap.ic_uncheck);
                                    viewHolder.setVisible(R.id.imgslect, false);
                                }
                            }
                        }
                    }
                }
            });

        }
            //这里再次写一下为的是切换左下角的文件夹的时候选中的还是选中的状态

            if (config.multiSelect) {
                viewHolder.setVisible(R.id.ivPhotoCheaked, true);
                if (Constant.imageList.contains(item.path)) {//遍历相应的图片路径，对应上后选中的图片进行相应的标记
                    viewHolder.setVisible(R.id.imgslect, true);
                    viewHolder.setImageResource(R.id.ivPhotoCheaked, R.mipmap.ic_checked);
                } else {
                    viewHolder.setImageResource(R.id.ivPhotoCheaked, R.mipmap.ic_uncheck);
                    viewHolder.setVisible(R.id.imgslect, false);
                }
            } else {
                viewHolder.setVisible(R.id.ivPhotoCheaked, false);
            }


    }

    @Override
    public int getItemViewType(int position) {

        if(position==0){
            return 1;
        }else{
            return 0;
        }

    }
}
