package com.tseng.pickgallery.bean;

import java.util.List;

/**
* author= awhalefail
* creatTime=2020/4/26
* descript=文件夹信息
*/
public class FolderInfo {

    public String name;                         // 文件夹名称
    public String path;                         // 文件夹路径
    public PhotoInfo photoInfo;                 // 文件夹中第一张图片的信息
    public List<PhotoInfo> photoInfoList;       // 文件夹中的图片集合

    @Override
    public boolean equals(Object object) {
        try {
            FolderInfo other = (FolderInfo) object;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "FolderInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", photoInfo=" + photoInfo +
                ", photoInfoList=" + photoInfoList +
                '}';
    }
}
