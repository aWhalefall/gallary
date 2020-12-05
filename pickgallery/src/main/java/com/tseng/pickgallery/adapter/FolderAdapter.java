package com.tseng.pickgallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tseng.pickgallery.R;
import com.tseng.pickgallery.bean.FolderInfo;
import com.tseng.pickgallery.bean.PhotoInfo;
import com.tseng.pickgallery.config.GalleryConfig;
import com.tseng.pickgallery.config.GalleryPick;
import com.tseng.pickgallery.utils.ScreenUtils;
import com.tseng.pickgallery.widget.GalleryImageView;

import java.util.List;


/**
 * Author: yangweichao
 * Date:   2020/12/5 7:58 PM
 * Description: 文件夹列表适配器
 */


public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private List<FolderInfo> result;
    private final static String TAG = "FolderAdapter";

    private GalleryConfig galleryConfig = GalleryPick.getInstance().getGalleryConfig();
    private int mSelector = 0;
    private OnClickListener onClickListener;

    public FolderAdapter(Activity mActivity, Context mContext, List<FolderInfo> result) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.result = result;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.gallery_item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == 0) {
            holder.tvGalleryFolderName.setText(mContext.getString(R.string.gallery_all_folder));
            holder.tvGalleryPhotoNum.setText(mContext.getString(R.string.gallery_photo_num, getTotalImageSize()));


            if (result.size() > 0) {
                PhotoInfo photoInfo = result.get(0).photoInfo;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    galleryConfig.getImageLoader().displayImage(mActivity,
                            mContext,
                            photoInfo.uri
                            , holder.ivGalleryFolderImage,
                            ScreenUtils.getScreenWidth(mContext),
                            ScreenUtils.getScreenWidth(mContext));
                } else {
                    galleryConfig.getImageLoader().displayImage(mActivity,
                            mContext,
                            photoInfo.path
                            , holder.ivGalleryFolderImage,
                            ScreenUtils.getScreenWidth(mContext),
                            ScreenUtils.getScreenWidth(mContext));
                }
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelector = 0;
                    onClickListener.onClick(null);
                }
            });

            if (mSelector == 0) {
                holder.ivGalleryIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.ivGalleryIndicator.setVisibility(View.GONE);
            }
            return;
        }
        final FolderInfo folderInfo = result.get(position - 1);
        holder.tvGalleryFolderName.setText(folderInfo.name);
        holder.tvGalleryPhotoNum.setText(mContext.getString(R.string.gallery_photo_num, folderInfo.photoInfoList.size()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            galleryConfig.getImageLoader().displayImage(mActivity,
                    mContext,
                    folderInfo.photoInfo.uri
                    , holder.ivGalleryFolderImage,
                    ScreenUtils.getScreenWidth(mContext),
                    ScreenUtils.getScreenWidth(mContext));
        } else {
            galleryConfig.getImageLoader().displayImage(mActivity,
                    mContext,
                    folderInfo.photoInfo.path
                    , holder.ivGalleryFolderImage,
                    ScreenUtils.getScreenWidth(mContext),
                    ScreenUtils.getScreenWidth(mContext));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelector = holder.getAdapterPosition() + 1;
                onClickListener.onClick(folderInfo);
            }
        });

        if (mSelector == holder.getAdapterPosition() + 1) {
            holder.ivGalleryIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.ivGalleryIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return result.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private GalleryImageView ivGalleryFolderImage;
        private TextView tvGalleryFolderName;
        private TextView tvGalleryPhotoNum;
        private ImageView ivGalleryIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            ivGalleryFolderImage = (GalleryImageView) itemView.findViewById(R.id.ivGalleryFolderImage);
            tvGalleryFolderName = (TextView) itemView.findViewById(R.id.tvGalleryFolderName);
            tvGalleryPhotoNum = (TextView) itemView.findViewById(R.id.tvGalleryPhotoNum);
            ivGalleryIndicator = (ImageView) itemView.findViewById(R.id.ivGalleryIndicator);
        }

    }


    public interface OnClickListener {
        void onClick(FolderInfo folderInfo);
    }

    /**
     * @return 所有图片数量
     */
    private int getTotalImageSize() {
        int result = 0;
        if (this.result != null && this.result.size() > 0) {
            for (FolderInfo folderInfo : this.result) {
                result += folderInfo.photoInfoList.size();
            }
        }
        return result;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
