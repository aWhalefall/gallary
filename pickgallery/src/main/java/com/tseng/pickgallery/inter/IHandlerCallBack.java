package com.tseng.pickgallery.inter;

import com.tseng.pickgallery.bean.PhotoInfo;

import java.util.List;

/**
 * IHandlerCallBack
 * Created by Yancy on 2016/10/26.
 */

public interface IHandlerCallBack {

    void onStart();

    void onSuccess(List<String> photoList);

    /**
     * 回到原始数据
     * @param list
     */
    void onBoSuccess(List<PhotoInfo> list);

    void onCancel();

    void onFinish();

    void onError();

}
