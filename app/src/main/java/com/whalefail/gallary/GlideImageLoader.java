package com.whalefail.gallary;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tseng.pickgallery.inter.ImageLoader;
import com.tseng.pickgallery.widget.GalleryImageView;


public class GlideImageLoader implements ImageLoader {

    private final static String TAG = "GlideImageLoader";

    @Override
    public void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height) {
        Glide.with(context)
                .load(path).apply(new RequestOptions()
                .placeholder(R.mipmap.gallery_pick_photo)
                .centerCrop())
                .into(galleryImageView);
    }

    @Override
    public void displayImage(Activity activity, Context context, Uri path, GalleryImageView galleryImageView, int width, int height) {
        Glide.with(context)
                .load(path).apply(new RequestOptions()
                .placeholder(R.mipmap.gallery_pick_photo)
                .centerCrop())
                .into(galleryImageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}