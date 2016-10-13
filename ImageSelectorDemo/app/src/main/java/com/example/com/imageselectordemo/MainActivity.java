package com.example.com.imageselectordemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.imageselectordemo.adapter.DividerGridItemDecoration;
import com.example.com.imageselectordemo.adapter.FolderListAdapter;
import com.example.com.imageselectordemo.adapter.ImageListAdapter;
import com.example.com.imageselectordemo.adapter.ViewPagerAdapter;
import com.example.com.imageselectordemo.bean.Folder;
import com.example.com.imageselectordemo.bean.Image;
import com.example.com.imageselectordemo.interfaces.OnFolderChangeListener;
import com.example.com.imageselectordemo.interfaces.OnItemClickListener;
import com.example.com.imageselectordemo.utils.FileUtils;
import com.example.com.imageselectordemo.utils.LogUtils;
import com.example.com.imageselectordemo.utils.StatusBarCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener,Callback{

    public static final String INTENT_RESULT = "result";
    private static final int IMAGE_CROP_CODE = 1;//裁剪
    private static final int STORAGE_REQUEST_CODE = 1;

    private List<Folder> folderList = new ArrayList<>();//文件夹包含图片
    private List<Image> imageList = new ArrayList<>();//图片

    private RecyclerView imgRecycleView;
    private ViewPager mViewPager;

    private ImageListAdapter imageListAdapter;//图片列表
    private FolderListAdapter folderListAdapter;//文件夹列表

    private ViewPagerAdapter mViewPagerAdapter;


    private Button btnAlbumSelected;//所有图片筛选
    private View rlBottom;

    private RelativeLayout rlTitleBar;//头部
    private TextView tvTitle;
    private Button btnConfirm;
    private ImageView ivBack;

    private static ImgSelConfig config;

    private ProgressBar loadProgress;

    private ListPopupWindow listPopupWindow;// 底部的popupwindow


    //点击回调事件
    private  Callback callback;

    //返回的最终选中结果
    private ArrayList<String> result = new ArrayList<>();

    //左下角的文件夹切换
    private  boolean isFolderChange=false;



    //当前页选中的，为甚有一个全局的还要这的原因是为了在已经选了几个点击确定后又返回继续选中这个时候使用
    private  List<String> curentStringPath = new ArrayList<String>();
    private String cropImagePath;//裁剪图片的路径

    private File tempFile;//拍照的保存路径


    public static void startActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivityForResult(intent, requestCode);

    }

    public static void startActivity(Activity activity, ImgSelConfig mConfig, int requestCode) {
        config = mConfig;
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivityForResult(intent, requestCode);

    }

    //开放给fragment
    public static void startActivity(Fragment fragment, ImgSelConfig mConfig, int requestCode) {
        config = mConfig;
        Intent intent = new Intent(fragment.getActivity(), MainActivity.class);
        fragment.getActivity().startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callback =this;

        getSupportActionBar().hide();


        initView();


        initViewData();


        //图片列表显示
        imageListAdapter = new ImageListAdapter(MainActivity.this, imageList, config);
        imgRecycleView.setAdapter(imageListAdapter);
        folderListAdapter = new FolderListAdapter(MainActivity.this, folderList, config);


        //加载本地的所有图片
//        getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback).startLoading();
        getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);

        initEvent();


    }

    //点击事件处理
    private void initEvent() {
        imageListAdapter.setListener(new OnItemClickListener() {
            //选中事件
            @Override
            public int onCheckedClick(int position, Image image) {
                return chekImaged(position, image);
            }


            //图片点击事件
            @Override
            public void onImageClick(int position, Image image) {

                if(config.needCamera&&position==0){
                    //去拍照
                    Log.i("yqy","跳转到拍照~");
                    showCameraAction();
                }else{
                    if(config.multiSelect){//大图的这个暂时不写

                        Toast.makeText(MainActivity.this, "要放大的图是" + position, Toast.LENGTH_LONG).show();

                    }else{
                        if(callback!=null){
                            callback.onSingleImageSelected(image.path);
                        }

                    }
                }


            }
        });
    }

    //图片选中
    private int chekImaged(int position, Image image) {

        if (image != null) {


            if (curentStringPath.contains(image.path)) {
                //选中了取消
                curentStringPath.remove(image.path);//移除掉

            } else {
                curentStringPath.add(image.path);
            }

            if (Constant.imageList.contains(image.path)) {
                //选中了取消
                Constant.imageList.remove(image.path);//移除掉
                if(callback !=null){
                    callback.onImageUnselected(image.path);
                }
            } else {

                if (Constant.imageList.size() >= config.maxNum) {//大于最大可选的数目，比如说是做多可以选中9张，这里就是9

                    Toast.makeText(MainActivity.this, String.format(getString(R.string.maxnum), config.maxNum), Toast.LENGTH_LONG).show();//提示不能超过的最大值

                    return 0;//不能再添加图片了

                }
                Constant.imageList.add(image.path);
                if(callback !=null){
                    callback.onImageSelected(image.path);
                }
            }
            return 1;
        }

        return 0;

    }


    private void initViewData() {
        if (config != null) {
            if (config.backResId != -1) {
                ivBack.setImageResource(config.backResId);
            }

            //状态栏透明
            if (config.statusBarColor != -1) {
                StatusBarCompat.compat(this, config.statusBarColor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
            rlTitleBar.setBackgroundColor(config.titleBgColor);
            tvTitle.setTextColor(config.titleColor);
            tvTitle.setText(config.title);
            btnConfirm.setBackgroundColor(config.btnBgColor);
            btnConfirm.setTextColor(config.btnTextColor);
            if (config.multiSelect) {
                btnConfirm.setText(String.format(getString(R.string.confirm), Constant.imageList.size(), config.maxNum));
            } else {
                btnConfirm.setText(getString(R.string.confirm_single));
            }
        }
    }

    private void initView() {
        loadProgress = (ProgressBar) findViewById(R.id.loadProgress);
        imgRecycleView = (RecyclerView) findViewById(R.id.imgRecycleView);
        imgRecycleView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        //添加分割线
        imgRecycleView.addItemDecoration(new DividerGridItemDecoration(MainActivity.this));
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        btnAlbumSelected = (Button) findViewById(R.id.btnAlbumSelected);
        btnAlbumSelected.setOnClickListener(this);
        rlBottom = findViewById(R.id.rlBottom);


        rlTitleBar = (RelativeLayout) findViewById(R.id.rlTitleBar);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);

        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
    }


    //创建底部的文件夹的popupwindow
    private void createPopupFOlderList(int width) {
        listPopupWindow = new ListPopupWindow(MainActivity.this);
        listPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        listPopupWindow.setAdapter(folderListAdapter);
        listPopupWindow.setContentWidth(width);
        listPopupWindow.setWidth(width);
        listPopupWindow.setAnchorView(rlBottom);
        listPopupWindow.setModal(true);
        folderListAdapter.setOnFloderChangeListener(new OnFolderChangeListener() {
            @Override
            public void onChange(int position, Folder folder) {
                listPopupWindow.dismiss();
                isFolderChange =true;
                if (position == 0) {
                    //加载所有的图片
                    getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                    btnAlbumSelected.setText(getString(R.string.all_images));
                } else {
                    imageList.clear();
                    if (config.needCamera)
                        imageList.add(new Image());
                    imageList.addAll(folder.images);
                    btnAlbumSelected.setText(folder.name);
                }

                imageListAdapter.notifyDataSetChanged();

            }
        });

    }


    /**
     * ********************************获取本地图片**********************************
     */

    private boolean hasFolderGened = false;//添加文件夹中的文件
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private static final int REQUEST_CAMERA = 5;


    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            loadProgress.setVisibility(View.VISIBLE);
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            loadProgress.setVisibility(View.GONE);
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    List<Image> tempImageList = new ArrayList<>();
                    data.moveToFirst();
                    do {

                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);

                        tempImageList.add(image);
                        if (!hasFolderGened) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.cover = image;
                            if (!folderList.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                folderList.add(folder);
                            } else {
                                Folder f = folderList.get(folderList.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    } while (data.moveToNext());

                    imageList.clear();
                    if (config.needCamera)
                        imageList.add(new Image());
                    imageList.addAll(tempImageList);

                    //设置adapter显示
                    imageListAdapter.notifyDataSetChanged();
                    folderListAdapter.notifyDataSetChanged();
                    hasFolderGened = true;


                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAlbumSelected.getId()) {
            if (listPopupWindow == null) {

                WindowManager wm = getWindowManager();
                createPopupFOlderList(wm.getDefaultDisplay().getWidth() / 3 * 2);
            }

            if (listPopupWindow.isShowing()) {
                listPopupWindow.dismiss();
            } else {

                listPopupWindow.show();
                int index = folderListAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                listPopupWindow.getListView().setSelection(index);
            }

        } else if (v.getId() == ivBack.getId()) {
            finish();
        }else if(v.getId()==btnConfirm.getId()){
            exit();
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    //单选
    @Override
    public void onSingleImageSelected(String path) {
        Log.i("yqy","是否需要裁剪======"+config.needCrop);
        if(config.needCrop){//裁剪
            Toast.makeText(MainActivity.this,"需要裁剪",Toast.LENGTH_LONG).show();
            cropPicture(path);

        }else{
            Constant.imageList.add(path);
            exit();
        }


    }

    private void cropPicture(String path) {
        File file = new File(FileUtils.createRootPath(this) + "/" + System.currentTimeMillis() + ".jpg");

        cropImagePath = file.getAbsolutePath();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", config.aspectX);
        intent.putExtra("aspectY", config.aspectY);
        intent.putExtra("outputX", config.outputX);
        intent.putExtra("outputY", config.outputY);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, IMAGE_CROP_CODE);
    }


    //拍照
    private void showCameraAction() {

        Log.i("yqy","开始进行拍照======"+config.maxNum+"====="+Constant.imageList.size());
        if (config.maxNum <= Constant.imageList.size()) {
            Toast.makeText(MainActivity.this, String.format(getString(R.string.maxnum), config.maxNum), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            Log.i("yqy","开始进行拍照！！！");
            tempFile = new File(FileUtils.createRootPath(MainActivity.this) + "/" + System.currentTimeMillis() + ".jpg");
            LogUtils.e(tempFile.getAbsolutePath());
            FileUtils.createFile(tempFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.open_camera_failure), Toast.LENGTH_SHORT).show();
        }
    }


    //多选的选中
    @Override
    public void onImageSelected(String path) {
        btnConfirm.setText(String.format(getString(R.string.confirm),Constant.imageList.size(),config.maxNum));

    }
    //多选的取消选中
    @Override
    public void onImageUnselected(String path) {
        btnConfirm.setText(String.format(getString(R.string.confirm), Constant.imageList.size(), config.maxNum));

    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            if (config.needCrop) {

                cropPicture(imageFile.getAbsolutePath());
            } else {
                Constant.imageList.add(imageFile.getAbsolutePath());
                exit();
            }
        }

    }

    @Override
    public void onPreviewChanged(int select, int sum, boolean visible) {

    }

    //选中退出

    boolean isBtnConfirmClick = false;
    public void exit(){

        isBtnConfirmClick =true;

        Intent intent = new Intent();
        result.clear();
        curentStringPath.addAll(Constant.imageList);
        result.addAll(Constant.imageList);
        intent.putStringArrayListExtra(INTENT_RESULT, result);
        setResult(RESULT_OK, intent);

        finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isBtnConfirmClick){
            //这里清除数据
//        Constant.imageList.clear();
        }
    }


    //裁剪图片的activityResut
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {

            Constant.imageList.add(cropImagePath);
            exit();
        }else    if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (tempFile != null) {
                    if (callback != null) {
                        callback.onCameraShot(tempFile);
                    }
                }
            } else {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
