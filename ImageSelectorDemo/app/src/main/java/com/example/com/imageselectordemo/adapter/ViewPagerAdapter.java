package com.example.com.imageselectordemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.com.imageselectordemo.R;
import com.example.com.imageselectordemo.bean.Image;
import com.example.com.imageselectordemo.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by yuanqingying on 2016/10/8.
 * 小图点击显示大图的viewpager的adpter
 */
public class ViewPagerAdapter extends PagerAdapter  {

    private Activity activity;
    private List<Image> images;
    private Context context;

    private OnItemClickListener listener;

    public ViewPagerAdapter(Activity activity, List<Image> images, Context context) {
        this.activity = activity;
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View root = View.inflate(activity, R.layout.viewpager_item, null);
        final ImageView photoView = (ImageView) root.findViewById(R.id.ivImage);

        Glide.with(context).load(images.get(position).path).into(photoView);

        container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return root;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }


    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
