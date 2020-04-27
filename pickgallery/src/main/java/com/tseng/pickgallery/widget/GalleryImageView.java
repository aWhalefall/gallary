package com.tseng.pickgallery.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * author= awhalefail
 * creatTime=2020/4/26
 * descript=自定义 Image 用来兼容 fresco
 */
public class GalleryImageView extends AppCompatImageView {


    private OnImageViewListener onImageViewListener;

    public GalleryImageView(Context context) {
        super(context);
    }

    public GalleryImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnImageViewListener {
        void onDraw(Canvas canvas);

        boolean verifyDrawable(Drawable dr);

        void onDetach();

        void onAttach();
    }

    @Override
    protected boolean verifyDrawable(Drawable dr) {
        onImageViewListener.verifyDrawable(dr);
        return super.verifyDrawable(dr);
    }

    public void setOnImageViewListener(OnImageViewListener onImageViewListener) {
        this.onImageViewListener = onImageViewListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (onImageViewListener != null) {
            onImageViewListener.onDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (onImageViewListener != null) {
            onImageViewListener.onDetach();
        }
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (onImageViewListener != null) {
            onImageViewListener.onDetach();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (onImageViewListener != null) {
            onImageViewListener.onAttach();
        }
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (onImageViewListener != null) {
            onImageViewListener.onAttach();
        }
    }


}
