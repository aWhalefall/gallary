package com.tseng.pickgallery.bean;

import android.net.Uri;

/**
* author= awhalefail
* creatTime=2020/4/26
* descript=图片信息
*/
public class PhotoInfo {

    public String name;                 // 图片名
    public String path;                 // 图片路径
    public Uri uri;                 // 图片uri路径，兼容Android 10 。Android q 中废弃通过data拿到路径问题
    public long time;                   // 图片添加时间

    public PhotoInfo(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public PhotoInfo(String name, String path, Uri uri, long time) {
        this.name = name;
        this.path = path;
        this.uri = uri;
        this.time = time;
    }

    @Override
    public String toString() {
        return "PhotoInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", uri=" + uri +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        try {
            PhotoInfo other = (PhotoInfo) object;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(object);
    }


}
