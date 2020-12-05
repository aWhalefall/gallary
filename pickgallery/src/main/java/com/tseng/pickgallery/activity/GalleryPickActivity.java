package com.tseng.pickgallery.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tseng.pickgallery.R;
import com.tseng.pickgallery.adapter.FolderAdapter;
import com.tseng.pickgallery.adapter.PhotoAdapter;
import com.tseng.pickgallery.bean.FolderInfo;
import com.tseng.pickgallery.bean.PhotoInfo;
import com.tseng.pickgallery.config.GalleryConfig;
import com.tseng.pickgallery.config.GalleryPick;
import com.tseng.pickgallery.inter.IHandlerCallBack;
import com.tseng.pickgallery.permission.PermissionCallback;
import com.tseng.pickgallery.permission.PermissionUtils;
import com.tseng.pickgallery.utils.FileUtils;
import com.tseng.pickgallery.utils.UCropUtils;
import com.tseng.pickgallery.widget.FolderListPopupWindow;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: yangweichao
 * Date:   2020/12/5 7:56 PM
 * Description:图片选择页面
 */


public class GalleryPickActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "GalleryPickActivity";

    private Context mContext;
    private Activity mActivity;
    private TextView tvSelectedComfirm;                  // 完成按钮
    private TextView tvGalleryFolder;           // 文件夹按钮
    private LinearLayout btnClose;    // 返回按钮
    private RecyclerView galleryRecycle;        // 图片列表
    private PhotoAdapter photoAdapter;              // 图片适配器
    private FolderAdapter folderAdapter;            // 文件夹适配器
    private FolderListPopupWindow folderListPopupWindow;   // 文件夹选择弹出框
    private GridLayoutManager gridLayoutManager;

    private boolean debug = false;
    private boolean hasFolderScan = false;           // 是否扫描过
    private ArrayList<String> resultPhoto = new ArrayList<>();
    private List<FolderInfo> folderInfoList = new ArrayList<>();    // 本地文件夹信息List
    private List<PhotoInfo> photoInfoList = new ArrayList<>();      // 本地图片信息List
    private List<PhotoInfo> sourceInfoList = new ArrayList<>();      // 本地图片详细包含宽高

    private static final int LOADER_ALL = 0;         // 获取所有图片
    private static final int LOADER_CATEGORY = 1;    // 获取某个文件夹中的所有图片
    private static final int REQUEST_CAMERA = 100;   // 设置拍摄照片的 REQUEST_CODE
    private int defaultColumns = 3;    //列数

    private File cameraTempFile;
    private File cropTempFile;

    private GalleryConfig galleryConfig;   // GalleryPick 配置器
    private IHandlerCallBack mHandlerCallBack;   // GalleryPick 生命周期接口
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback;

    private String[] permissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_main);
        galleryConfig = GalleryPick.getInstance().getGalleryConfig();
        if (galleryConfig == null) {
            activityBack();
            return;
        }
        defaultColumns=galleryConfig.getDefaultColumns();
        mHandlerCallBack = galleryConfig.getIHandlerCallBack();
        if (mHandlerCallBack != null)
            mHandlerCallBack.onStart();
        mContext = this;
        mActivity = this;
        initView();
        initListener();
        checkPermission();
    }


    /**
     * 初始化视图
     */
    private void initView() {
        tvSelectedComfirm = findViewById(R.id.txt_back);
        tvGalleryFolder = findViewById(R.id.txt_other_folder);
        btnClose = findViewById(R.id.btn_selected_confirm);
        galleryRecycle = findViewById(R.id.galleryRecycleView);
    }

    private void initListener() {
        btnClose.setOnClickListener(this);
        tvSelectedComfirm.setOnClickListener(this);
        tvGalleryFolder.setOnClickListener(this);
    }


    private void checkPermission() {
        PermissionUtils.getInstance().onRequestPermission(this, permissionList, new PermissionCallback() {

            @Override
            public void onGranted() {
                initValue();
                initPhoto();
            }

            @Override
            public void permissionDenied() {
                //不授予权限
                finish();
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    private void initValue() {
        resultPhoto = galleryConfig.getPathList();
        tvSelectedComfirm.setText(getString(R.string.gallery_finish, resultPhoto.size(), galleryConfig.getMaxSize()));

        gridLayoutManager = new GridLayoutManager(mContext, defaultColumns);
        galleryRecycle.setItemAnimator(null);
        galleryRecycle.setLayoutManager(gridLayoutManager);
        photoAdapter = new PhotoAdapter(mActivity, mContext, photoInfoList, galleryConfig);
        photoAdapter.setOnCallBack(onItemClickListener);
        galleryRecycle.setAdapter(photoAdapter);
        if (!galleryConfig.isMultiSelect()) {
            tvSelectedComfirm.setVisibility(View.GONE);
        }
        folderAdapter = new FolderAdapter(mActivity, mContext, folderInfoList);
        folderAdapter.setOnClickListener(folderItemClickListener);
    }


    /**
     * 初始化配置
     */
    private void initPhoto() {
        mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            private final String[] IMAGE_PROJECTION = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT,
            };

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == LOADER_ALL) {
                    return new CursorLoader(mActivity,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                            null, null, IMAGE_PROJECTION[2] + " DESC");
                } else if (id == LOADER_CATEGORY) {
                    return new CursorLoader(mActivity,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                            IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'",
                            null, IMAGE_PROJECTION[2] + " DESC");
                }
                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data != null) {
                    int count = data.getCount();
                    if (count > 0) {
                        List<PhotoInfo> tempPhotoList = new ArrayList<>();
                        data.moveToFirst();
                        do {
                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                            long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                            int size = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                            boolean showFlag = size > 1024 * 1;                           //是否大于5K
                            PhotoInfo photoInfo = null;
                            //optimization ywc 2020/4/26 15:42 兼容 andorid 10
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inJustDecodeBounds = true;//这个参数设置为true才有效，
                            BitmapFactory.decodeFile(path, opts);
                            int with = opts.outWidth;
                            int height = opts.outHeight;
                            Log.d("path= ", path + "  with =" + with + " height= " + height);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                photoInfo = new PhotoInfo(name, path, uri, dateTime, with, height);
                            } else {
                                photoInfo = new PhotoInfo(path, name, dateTime, with, height);
                            }
                            if (showFlag) {
                                tempPhotoList.add(photoInfo);
                            }
                            if (!hasFolderScan && showFlag) {
                                File photoFile = new File(path);                  // 获取图片文件
                                File folderFile = photoFile.getParentFile();      // 获取图片上一级文件夹

                                FolderInfo folderInfo = new FolderInfo();
                                folderInfo.name = folderFile.getName();
                                folderInfo.path = folderFile.getAbsolutePath();
                                folderInfo.photoInfo = photoInfo;
                                if (!folderInfoList.contains(folderInfo)) {      // 判断是否是已经扫描到的图片文件夹
                                    List<PhotoInfo> photoInfoList = new ArrayList<>();
                                    photoInfoList.add(photoInfo);
                                    folderInfo.photoInfoList = photoInfoList;
                                    folderInfoList.add(folderInfo);
                                } else {
                                    FolderInfo f = folderInfoList.get(folderInfoList.indexOf(folderInfo));
                                    f.photoInfoList.add(photoInfo);
                                }
                            }

                        } while (data.moveToNext());

                        photoInfoList.clear();
                        photoInfoList.addAll(tempPhotoList);

//                        List<String> tempPhotoPathList = new ArrayList<>();
//                        for (PhotoInfo photoInfo : photoInfoList) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                tempPhotoPathList.add(photoInfo.uri.toString());
//                            }else {
//                                tempPhotoPathList.add(photoInfo.path);
//                            }
//                        }
//                        //判断是否包含已经选择的path
//                        for (String mPhotoPath : galleryConfig.getPathList()) {
//                            if (!tempPhotoPathList.contains(mPhotoPath)) {
//                                PhotoInfo photoInfo = new PhotoInfo(mPhotoPath, null, 0L);
//                                photoInfoList.add(0, photoInfo);
//                            }
//                        }
                        //回显
                        photoAdapter.setSelectPhoto(resultPhoto);
                        photoAdapter.notifyDataSetChanged();
                        hasFolderScan = true;
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
        getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);   // 扫描手机中的图片
    }


    private PhotoAdapter.OnCallBack onItemClickListener = new PhotoAdapter.OnCallBack() {
        @Override
        public void OnClickCamera(List<String> selectPhotoList) {
            resultPhoto.clear();
            resultPhoto.addAll(selectPhotoList);
            showCameraAction();
        }

        @Override
        public void OnClickPhoto(List<String> selectPhotoList) {
            tvSelectedComfirm.setText(getString(R.string.gallery_finish, selectPhotoList.size(),
                    galleryConfig.getMaxSize()));
            resultPhoto.clear();
            resultPhoto.addAll(selectPhotoList);
            if (!galleryConfig.isMultiSelect() && resultPhoto.size() > 0) {
                if (galleryConfig.isCrop()) {
                    cameraTempFile = new File(resultPhoto.get(0));
                    cropTempFile = FileUtils.getCorpFile(galleryConfig.getFilePath(), mActivity);
                    UCropUtils.start(mActivity,
                            cameraTempFile, cropTempFile,
                            galleryConfig.getAspectRatioX(), galleryConfig.getAspectRatioY(),
                            galleryConfig.getMaxWidth(), galleryConfig.getMaxHeight());
                    return;
                }
                mHandlerCallBack.onSuccess(resultPhoto);
                activityBack();
            }
        }

        @Override
        public void OnClickPhotoAndoridQplus(List<PhotoInfo> selectPhoto) {
            if (0 <= sourceInfoList.size()) {
                sourceInfoList.clear();
            }
            sourceInfoList.addAll(selectPhoto);
            // mHandlerCallBack.onBoSuccess(sourceInfoList);
            //每次会将所选择的数据回调，一个图片回调一个bo，增加一个图片，会在原来的基础上回调两个bo
            for (int i = 0; debug && i < selectPhoto.size(); i++) {
                System.out.println(TAG + "Listbos =" + selectPhoto.get(i).toString());
            }

        }

    };

    FolderAdapter.OnClickListener folderItemClickListener = new FolderAdapter.OnClickListener() {
        @Override
        public void onClick(FolderInfo folderInfo) {
            if (folderInfo == null) {
                getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                tvGalleryFolder.setText(R.string.gallery_all_folder);
            } else {
                photoInfoList.clear();
                photoInfoList.addAll(folderInfo.photoInfoList);
                photoAdapter.notifyDataSetChanged();
                tvGalleryFolder.setText(folderInfo.name);
            }
            folderListPopupWindow.dismiss();
            gridLayoutManager.scrollToPosition(0);
        }
    };

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            try {
                cameraTempFile = FileUtils.createTmpFile(mActivity, galleryConfig.getFilePath());
                Uri imageUri = FileProvider.getUriForFile(mContext, galleryConfig.getProvider(), cameraTempFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mContext.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } catch (Exception e) {
                System.out.println(TAG + " error: " + e.getMessage());
            }
        } else {
            Toast.makeText(mContext, R.string.gallery_msg_no_camera, Toast.LENGTH_SHORT).show();
            galleryConfig.getIHandlerCallBack().onError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK && cameraTempFile != null) {
                if (!galleryConfig.isMultiSelect()) {
                    resultPhoto.clear();
                    if (galleryConfig.isCrop()) {
                        cropTempFile = FileUtils.getCorpFile(galleryConfig.getFilePath(), mActivity);
                        UCropUtils.start(mActivity,
                                cameraTempFile,
                                cropTempFile,
                                galleryConfig.getAspectRatioX(),
                                galleryConfig.getAspectRatioY(),
                                galleryConfig.getMaxWidth(),
                                galleryConfig.getMaxHeight());
                    }
                }
                resultPhoto.add(cameraTempFile.getAbsolutePath());
                scannerSystemMediaLib();
                PhotoInfo photoInfo = new PhotoInfo();
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;//这个参数设置为true才有效，
                BitmapFactory.decodeFile(cameraTempFile.getAbsolutePath(), opts);
                photoInfo.height = opts.outHeight;
                photoInfo.with = opts.outWidth;
                photoInfo.name = cameraTempFile.getAbsolutePath();
                photoInfo.path = cameraTempFile.getAbsolutePath();
                sourceInfoList.add(photoInfo);
                mHandlerCallBack.onSuccess(resultPhoto);
                mHandlerCallBack.onBoSuccess(sourceInfoList);
                activityBack();
            } else {
                if (cameraTempFile != null && cameraTempFile.exists()) {
                    cameraTempFile.delete();
                }
                if (galleryConfig.isOpenCamera()) {
                    activityBack();
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            final Uri resultUri = UCrop.getOutput(data);
//            if (cameraTempFile != null && cameraTempFile.exists()) {
//                cameraTempFile.delete();
//            }
            resultPhoto.clear();
            resultPhoto.add(cropTempFile.getAbsolutePath());
            mHandlerCallBack.onSuccess(resultPhoto);
            //todo 裁剪没有回调
            //mHandlerCallBack.onBoSuccess(sourceInfoList);
            activityBack();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            galleryConfig.getIHandlerCallBack().onError();
//            final Throwable cropError = UCrop.getError(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 通知扫描系统库
     */
    private void scannerSystemMediaLib() {
        // 通知系统扫描该文件夹
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(FileUtils.getFilePath(mContext) + galleryConfig.getFilePath()));
        intent.setData(uri);
        sendBroadcast(intent);
    }

    /**
     * 退出
     */
    private void activityBack() {
        if (mHandlerCallBack != null) {
            mHandlerCallBack.onFinish();
        }
        finish();
    }

    /**
     * 回退键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (folderListPopupWindow != null && folderListPopupWindow.isShowing()) {
                folderListPopupWindow.dismiss();
                return true;
            }
            mHandlerCallBack.onCancel();
            activityBack();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_selected_confirm && mHandlerCallBack != null) {
            mHandlerCallBack.onCancel();
            activityBack();
        } else if (v.getId() == R.id.txt_back) {
            if (resultPhoto != null && resultPhoto.size() >= 0) {
                if (debug) {
                    System.out.println(TAG + " size is equal =" + (resultPhoto.size() == sourceInfoList.size()));
                }
                mHandlerCallBack.onSuccess(resultPhoto);
                mHandlerCallBack.onBoSuccess(sourceInfoList);
                activityBack();
            }
        } else if (v.getId() == R.id.txt_other_folder) {
            if (folderListPopupWindow != null && folderListPopupWindow.isShowing()) {
                folderListPopupWindow.dismiss();
                return;
            }
            folderListPopupWindow = new FolderListPopupWindow(mActivity, mContext, folderAdapter);
            folderListPopupWindow.showAsDropDown(tvGalleryFolder);
        }
    }
}
