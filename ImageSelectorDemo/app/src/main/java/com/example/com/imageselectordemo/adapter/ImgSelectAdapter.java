package com.example.com.imageselectordemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.com.imageselectordemo.R;
import com.example.com.imageselectordemo.interfaces.OnImgItemClickListener;

import java.util.List;

/**
 * Created by yuanqingying on 2016/10/11.
 */
public class ImgSelectAdapter extends BaseAdapter {


    private Context context;
    private List<String> mLists;
    private ImgSelectViewHolder imgSelectViewHolder;

    private OnImgItemClickListener listener;


    public ImgSelectAdapter(Context context, List<String> mLists) {
        this.context = context;
        this.mLists = mLists;
    }

    @Override
    public int getCount() {
        return mLists.size() == 0 ? 0 : mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_img_selsect, parent, false);
            imgSelectViewHolder = new ImgSelectViewHolder();
            convertView.setTag(imgSelectViewHolder);
        } else {
            imgSelectViewHolder = (ImgSelectViewHolder) convertView.getTag();
        }

        imgSelectViewHolder.imageView = (ImageView) convertView.findViewById(R.id.imgviewselsect);
        imgSelectViewHolder.ivPhotoDelete= (ImageView) convertView.findViewById(R.id.ivPhotoDelete);
        Glide.with(context).load(mLists.get(position)).override(200,200).into(imgSelectViewHolder.imageView);


        imgSelectViewHolder.ivPhotoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCheckedClick(mLists.get(position));
                }else{
                    Log.i("yqy","点击删除此时的listener为空---null----");
                }
            }
        });

        return convertView;
    }


    class ImgSelectViewHolder {
        ImageView imageView,ivPhotoDelete;
    }

    public void setListener(OnImgItemClickListener listener) {
        this.listener = listener;
    }
}
