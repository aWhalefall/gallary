package com.tseng.pickgallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.tseng.pickgallery.R;
import com.tseng.pickgallery.bean.PhotoInfo;
import com.tseng.pickgallery.config.GalleryConfig;
import com.tseng.pickgallery.config.GalleryPick;
import com.tseng.pickgallery.utils.ScreenUtils;
import com.tseng.pickgallery.widget.GalleryImageView;

import java.util.List;

/**
 * author= awhalefail
 * creatTime=2020/4/26
 * descript=Mini选择器 适配器
 */
public class MiniPhotoAdapter extends RecyclerView.Adapter<MiniPhotoAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private List<PhotoInfo> photoInfoList;
    private final static String TAG = "MiniPhotoAdapter";

    private GalleryConfig galleryConfig = GalleryPick.getInstance().getGalleryConfig();

    public MiniPhotoAdapter(Context context, List<PhotoInfo> photoInfoList) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.photoInfoList = photoInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.gallery_mini_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoInfo photoInfo = photoInfoList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            galleryConfig.getImageLoader().displayImage(mActivity,
                    mContext,
                    photoInfo.uri
                    , holder.ivPhotoImage,
                    ScreenUtils.getScreenWidth(mContext),
                    ScreenUtils.getScreenWidth(mContext));
        } else {
            galleryConfig.getImageLoader().displayImage(mActivity,
                    mContext,
                    photoInfo.path
                    , holder.ivPhotoImage,
                    ScreenUtils.getScreenWidth(mContext),
                    ScreenUtils.getScreenWidth(mContext));
        }

    }

    @Override
    public int getItemCount() {
        return photoInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private GalleryImageView ivPhotoImage;
        private CheckBox chkPhotoSelector;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhotoImage = (GalleryImageView) itemView.findViewById(R.id.ivGalleryPhotoImage);
            chkPhotoSelector = (CheckBox) itemView.findViewById(R.id.chkGalleryPhotoSelector);
        }

    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }
}
