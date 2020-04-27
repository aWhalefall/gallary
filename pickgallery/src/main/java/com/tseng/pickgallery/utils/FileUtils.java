package com.tseng.pickgallery.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author= awhalefail
 * creatTime=2020/4/26
 * descript= 兼容android 10
 */
public class FileUtils {


    private final static String PATTERN = "yyyyMMddHHmmss";    // 时间戳命名


    /**
     * 创建文件
     *
     * @param context  context
     * @param filePath 文件路径
     * @return file
     */
    public static File createTmpFile(Context context, String filePath) {

        String timeStamp = new SimpleDateFormat(PATTERN, Locale.CHINA).format(new Date());
        //兼容android 10 + ，存取分区
        String externalStorageState = ContextCompat.getExternalFilesDirs(context, null)[0].getAbsolutePath();
        File dir = new File(externalStorageState + filePath);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return new File(dir, timeStamp + ".jpg");
        } else {
            File cacheDir = context.getCacheDir();
            return new File(cacheDir, timeStamp + ".jpg");
        }

    }


    /**
     * 创建初始文件夹。保存拍摄图片和裁剪后的图片
     *
     * @param filePath 文件夹路径
     */
    public static void createFile(String filePath, Context context) {
        String externalStorageState = ContextCompat.getExternalFilesDirs(context, null)[0].getAbsolutePath();

        File dir = new File(externalStorageState + filePath);
        File cropFile = new File(externalStorageState + filePath + "/crop");

        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            if (!cropFile.exists()) {
                cropFile.mkdirs();
            }

            File file = new File(cropFile, ".nomedia");    // 创建忽视文件。   有该文件，系统将检索不到此文件夹下的图片。
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public static String getFilePath(Context context) {
        String status = ContextCompat.getExternalFilesDirs(context, null)[0].getAbsolutePath();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return status;
        } else {
            return context.getExternalCacheDir().getAbsolutePath();
        }
    }


    /**
     * @param filePath  文件夹路径
     * @param mActivity
     * @return 截图完成的 file
     */
    public static File getCorpFile(String filePath, Activity mActivity) {
        String timeStamp = new SimpleDateFormat(PATTERN, Locale.CHINA).format(new Date());
        return new File(ContextCompat.getExternalFilesDirs(mActivity, null)[0].getAbsolutePath() + filePath + "/crop/" + timeStamp + ".jpg");
    }


}