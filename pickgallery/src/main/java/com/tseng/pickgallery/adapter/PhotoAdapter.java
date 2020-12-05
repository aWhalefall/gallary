package com.tseng.pickgallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tseng.pickgallery.R;
import com.tseng.pickgallery.bean.PhotoInfo;
import com.tseng.pickgallery.config.GalleryConfig;
import com.tseng.pickgallery.config.GalleryPick;
import com.tseng.pickgallery.utils.ScreenUtils;
import com.tseng.pickgallery.widget.GalleryImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yangweichao
 * Date:   2020/12/5 7:46 PM
 * Description:增加数字选择逻辑
 */


public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private List<PhotoInfo> photoInfoList;                      // 本地照片数据
    private List<String> selectPhoto = new ArrayList<>();                   // 选择的图片数据
    private List<PhotoInfo> selectPhotoBo = new ArrayList<>();             // 选择的图片业务对象
    private OnCallBack onCallBack;
    private final static String TAG = "PhotoAdapter";
    private GalleryConfig galleryConfig = GalleryPick.getInstance().getGalleryConfig();
    private int LAYOUT_TYPE = 0; // 0.默认 1 数字

    private final static int CAMERA_TYPE = 0;    // 开启相机时需要显示的布局
    private final static int ITEM = 1;    // 照片布局
    private int defaultColumns;    //列数

    public PhotoAdapter(Activity mActivity, Context mContext, List<PhotoInfo> photoInfoList, int layoutType) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.photoInfoList = photoInfoList;
        this.mActivity = mActivity;
        LAYOUT_TYPE = layoutType;
    }

    public PhotoAdapter(Activity mActivity, Context mContext, List<PhotoInfo> photoInfoList, GalleryConfig galleryConfig) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.photoInfoList = photoInfoList;
        this.mActivity = mActivity;
        LAYOUT_TYPE = galleryConfig.getLayoutType();
        defaultColumns=galleryConfig.getDefaultColumns();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CAMERA_TYPE) {
            return new HeadHolder(mLayoutInflater.inflate(R.layout.gallery_item_camera, parent, false));
        }
        return LAYOUT_TYPE == 0 ? new ViewHolder(mLayoutInflater.inflate(R.layout.gallery_item_photo, parent, false)) :
                new ViewHolder(mLayoutInflater.inflate(R.layout.gallery_item_photo_cam, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // 设置 每个imageView 的大小
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = ScreenUtils.getScreenWidth(mContext) / defaultColumns;
        params.width = ScreenUtils.getScreenWidth(mContext) / defaultColumns;
        holder.itemView.setLayoutParams(params);

        if (getItemViewType(position) == CAMERA_TYPE) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (galleryConfig.getMaxSize() <= selectPhoto.size()) {        // 当选择图片达到上限时， 禁止继续添加
                        return;
                    }
                    onCallBack.OnClickCamera(selectPhoto);
                }
            });
            return;
        }

        final PhotoInfo photoInfo = galleryConfig.isShowCamera() ? photoInfoList.get(position - 1) : photoInfoList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && photoInfo.uri != null) {
            galleryConfig.getImageLoader().displayImage(mActivity, mContext, photoInfo.uri, viewHolder.ivPhotoImage, ScreenUtils.getScreenWidth(mContext) / 3, ScreenUtils.getScreenWidth(mContext) / 3);
        } else {
            galleryConfig.getImageLoader().displayImage(mActivity, mContext, photoInfo.path, viewHolder.ivPhotoImage, ScreenUtils.getScreenWidth(mContext) / 3, ScreenUtils.getScreenWidth(mContext) / 3);
        }

        if (LAYOUT_TYPE == 0) {
            if (selectPhoto.contains(photoInfo.path) || (photoInfo.uri != null && selectPhoto.contains(photoInfo.uri.toString()))) {
                viewHolder.chkPhotoSelector.setChecked(true);
                viewHolder.chkPhotoSelector.setButtonDrawable(R.mipmap.gallery_pick_select_checked);
                viewHolder.vPhotoMask.setVisibility(View.VISIBLE);
            } else {
                viewHolder.chkPhotoSelector.setChecked(false);
                viewHolder.chkPhotoSelector.setButtonDrawable(R.mipmap.gallery_pick_select_unchecked);
                viewHolder.vPhotoMask.setVisibility(View.GONE);
            }
        } else {
            if (selectPhoto.size() > 0) {
                for (int i = 0; i < selectPhoto.size(); i++) {
                    if (selectPhoto.get(i).equals(photoInfo.path)) {
                        viewHolder.tv_num.setText(String.valueOf(i + 1));
                        viewHolder.tv_num.setVisibility(View.VISIBLE);
                        break;
                    } else {
                        viewHolder.tv_num.setText("");
                        viewHolder.tv_num.setVisibility(View.GONE);
                    }
                }

            } else {
                viewHolder.tv_num.setText("");
                viewHolder.tv_num.setVisibility(View.GONE);
            }
        }

        if (!galleryConfig.isMultiSelect()) {
            viewHolder.chkPhotoSelector.setVisibility(View.GONE);
            viewHolder.vPhotoMask.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 兼容android 10
//                String compatPath = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? photoInfo.uri.toString() : photoInfo.path);
                String compatPath = photoInfo.path;
                //单选
                if (!galleryConfig.isMultiSelect()) {
                    selectPhoto.clear();
                    selectPhoto.add(compatPath);

                    selectPhotoBo.clear();
                    selectPhotoBo.add(photoInfo);
                    onCallBack.OnClickPhoto(selectPhoto);
                    onCallBack.OnClickPhotoAndoridQplus(selectPhotoBo);
                    return;
                }
                //选中/反选
                if (selectPhoto.contains(compatPath)) {
                    selectPhoto.remove(compatPath);
                    selectPhotoBo.remove(photoInfo);
                    if (LAYOUT_TYPE == 0) {
                        viewHolder.chkPhotoSelector.setChecked(false);
                        viewHolder.chkPhotoSelector.setButtonDrawable(R.mipmap.gallery_pick_select_unchecked);
                    } else {
                        viewHolder.tv_num.setText("");
                        viewHolder.tv_num.setVisibility(View.GONE);
                    }

                    viewHolder.vPhotoMask.setVisibility(View.GONE);

                    notifyDataSetChanged();

                } else {
                    if (galleryConfig.getMaxSize() <= selectPhoto.size()) {        // 当选择图片达到上限时， 禁止继续添加
                        return;
                    }
                    selectPhoto.add(compatPath);
                    selectPhotoBo.add(photoInfo);
                    if (LAYOUT_TYPE == 0) {
                        viewHolder.chkPhotoSelector.setChecked(true);
                        viewHolder.chkPhotoSelector.setButtonDrawable(R.mipmap.gallery_pick_select_checked);
                    } else {
                        viewHolder.tv_num.setText(String.valueOf(selectPhoto.size()));
                        viewHolder.tv_num.setVisibility(View.VISIBLE);
                    }
                    viewHolder.vPhotoMask.setVisibility(View.VISIBLE);
                }
                onCallBack.OnClickPhoto(selectPhoto);
                onCallBack.OnClickPhotoAndoridQplus(selectPhotoBo);
            }
        });

    }


    /**
     * 照片的 Holder
     */
    private class ViewHolder extends RecyclerView.ViewHolder {
        private GalleryImageView ivPhotoImage;
        private View vPhotoMask;
        private CheckBox chkPhotoSelector;
        private TextView tv_num;

        private ViewHolder(View itemView) {
            super(itemView);
            ivPhotoImage = (GalleryImageView) itemView.findViewById(R.id.ivGalleryPhotoImage);
            vPhotoMask = itemView.findViewById(R.id.vGalleryPhotoMask);
            chkPhotoSelector = (CheckBox) itemView.findViewById(R.id.chkGalleryPhotoSelector);
            tv_num = itemView.findViewById(R.id.tv_num);
        }
    }


    /**
     * 相机按钮的 Holder
     */
    private class HeadHolder extends RecyclerView.ViewHolder {
        private HeadHolder(View itemView) {
            super(itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (galleryConfig.isShowCamera() && position == 0) {
            return CAMERA_TYPE;
        }
        return ITEM;
    }

    @Override
    public int getItemCount() {
        if (galleryConfig.isShowCamera())
            return photoInfoList.size() + 1;
        else
            return photoInfoList.size();
    }

    public interface OnCallBack {
        void OnClickPhoto(List<String> selectPhoto);

        void OnClickPhotoAndoridQplus(List<PhotoInfo> selectPhoto);

        void OnClickCamera(List<String> selectPhoto);
    }

    public void setOnCallBack(OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    /**
     * 传入已选的图片
     *
     * @param selectPhoto 已选的图片路径
     */
    public void setSelectPhoto(List<String> selectPhoto) {
        this.selectPhoto.addAll(selectPhoto);

//        for (String filePath : selectPhoto) {
//            PhotoInfo photoInfo = getPhotoByPath(filePath);
//            if (photoInfo != null) {
//                this.photoInfoList.add(photoInfo);
//            }
//        }
//        if (selectPhoto.size() > 0) {
//            notifyDataSetChanged();
//        }
    }

    /**
     * 根据图片路径，获取图片 PhotoInfo 对象
     *
     * @param filePath 图片路径
     * @return PhotoInfo 对象
     */
    private PhotoInfo getPhotoByPath(String filePath) {
        if (photoInfoList != null && photoInfoList.size() > 0) {
            for (PhotoInfo photoInfo : photoInfoList) {
                if (photoInfo.path.equalsIgnoreCase(filePath)) {
                    return photoInfo;
                }
            }
        }
        return null;
    }


}
