package com.tseng.pickgallery.inter;

import java.util.List;

/**
* author= awhalefail
* creatTime=2020/4/27
* descript=IHandlerCallBack
*/

public interface IHandlerCallBack {

    void onStart();

    void onSuccess(List<String> photoList);

    void onCancel();

    void onFinish();

    void onError();

}
