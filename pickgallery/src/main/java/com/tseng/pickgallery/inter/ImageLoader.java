package com.tseng.pickgallery.inter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.tseng.pickgallery.widget.GalleryImageView;

import java.io.Serializable;

/**
 * author= awhalefail
 * creatTime=2020/4/26
 * descript=自定义图片加载框架
 */
public interface ImageLoader extends Serializable {
    void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height);

    void displayImage(Activity activity, Context context, Uri path, GalleryImageView galleryImageView, int width, int height);

    void clearMemoryCache();
}
