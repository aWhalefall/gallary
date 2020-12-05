package com.tseng.pickgallery.config;

import android.util.Log;

import com.tseng.pickgallery.bean.PhotoInfo;
import com.tseng.pickgallery.inter.IHandlerCallBack;

import java.util.List;
/**
 * Author: yangweichao
 * Date:   2020/12/5 7:49 PM
 * Description: 简单回调
 */


public class SimpleHandlerCallBack implements IHandlerCallBack {

    private String TAG = SimpleHandlerCallBack.class.getSimpleName();

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: 开启");
    }

    @Override
    public void onSuccess(List<String> photoList) {

    }

    @Override
    public void onBoSuccess(List<PhotoInfo> list) {

    }

    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel: 取消");
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "onFinish: 结束");
    }

    @Override
    public void onError() {
        Log.d(TAG, "onError: 出错");
    }
}
