package com.whalefail.gallary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.tseng.pickgallery.bean.PhotoInfo;
import com.tseng.pickgallery.config.GalleryConfig;
import com.tseng.pickgallery.config.GalleryPick;
import com.tseng.pickgallery.config.SimpleHandlerCallBack;
import com.tseng.pickgallery.inter.IHandlerCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yangweichao
 * Date:   2020/12/5 8:46 PM
 * Description: 图片裁切功能需要自行开发
 */


public class MainActivity extends AppCompatActivity {

    GalleryConfig config;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);

        Glide.init(this, new GlideBuilder());
        initListenter();
        config = new GalleryConfig.Builder()
                .imageLoader(new GlideImageLoader())    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                .provider("com.whalefail.gallary.fileProvider")   // provider (必填)
                .pathList(new ArrayList<String>())                 // 记录已选的图片
                .multiSelect(true, 1) // 是否多选   默认：false
                .crop(true)                      // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
                .isShowCamera(true)                     // 是否显示相机按钮  默认：false
                .filePath("/Gallery/Pictures")          // 图片存放路径
                .layoutStyle(0) // 0 显示多选样式 1 显示数字样式
                .setDisplayCol(3)  //默认显示列数
                .build();
    }

    IHandlerCallBack iHandlerCallBack = new SimpleHandlerCallBack() {

        @Override
        public void onSuccess(List<String> photoList) {
            text.setText("");
            //返回选择图片路径  Android Q 以上包含Android   Q 返回的是 Uri
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < photoList.size(); i++) {
                stringBuilder.append(photoList.get(i)).append("\n");
            }
            text.setText(stringBuilder.toString());
        }

        @Override
        public void onBoSuccess(List<PhotoInfo> list) {
            // FIXME: 2020/12/5 增加新的回调入口
        }

    };

    private void initListenter() {

    }

    public void selectPhoto(View view) {
        GalleryPick.getInstance().setGalleryConfig(config).open(this);
    }
}
