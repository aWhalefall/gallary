package com.whalefail.gallary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.tseng.pickgallery.config.GalleryConfig;
import com.tseng.pickgallery.config.GalleryPick;
import com.tseng.pickgallery.inter.IHandlerCallBack;

import java.util.ArrayList;
import java.util.List;

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
                .multiSelect(true)
                // 是否多选   默认：false
                // 配置是否多选的同时 配置多选数量   默认：false ， 9
                .maxSize(3)                             // 配置多选时 的多选数量。    默认：9
                .crop(true)                      // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
                .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                .filePath("/Gallery/Pictures")          // 图片存放路径
                .build();
    }

    IHandlerCallBack iHandlerCallBack = new IHandlerCallBack() {
        @Override
        public void onStart() {

        }

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
        public void onCancel() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onError() {

        }
    };

    private void initListenter() {

    }

    public void selectPhoto(View view) {
        GalleryPick.getInstance().setGalleryConfig(config).open(this);
    }
}
