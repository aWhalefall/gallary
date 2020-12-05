package com.tseng.pickgallery.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tseng.pickgallery.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    private final int mRequestCode = 100;//权限请求码
    private AlertDialog dialog;
    private PermissionCallback mListener;
    private static PermissionUtils permissionUtils;
    
    private PermissionUtils() {
    }
    
    public static PermissionUtils getInstance() {
        if (permissionUtils == null) {
            synchronized (PermissionUtils.class) {
                if (permissionUtils == null) {
                    permissionUtils = new PermissionUtils();
                }
            }
        }
        return permissionUtils;
    }

    public void onRequestPermission(Activity context, String[] permissions, PermissionCallback listener) {
        mListener = listener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0才用动态权限
            //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPermissionList中
            List<String> mPermissionList = new ArrayList<>();
            //逐个判断你要的权限是否已经通过
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);//添加还未授予的权限
                }
            }
            //申请权限
            if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
                ActivityCompat.requestPermissions(context, mPermissionList.toArray(new String[mPermissionList.size()]), mRequestCode);
            } else {
                //说明权限都已经通过，可以做你想做的事情去
                mListener.onGranted();
            }
        }

    }

    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限

    public void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mRequestCode) {
            List<String> deniedPermissions = new ArrayList<>();
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }
            }
            if (!deniedPermissions.isEmpty()) {
                mListener.onDenied(deniedPermissions);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 用户还是想用我的 APP 的
                    // 提示用户去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSetting(context);
                }
            }else {
                mListener.onGranted();
            }
        } else {
            //所有的权限都被接受了
            mListener.onGranted();
        }
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     */
    private void showDialogTipUserGoToAppSetting(final Activity context){
        dialog = new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert_title))
                .setMessage(context.getResources().getString(R.string.alert_content))
                .setPositiveButton(context.getResources().getString(R.string.confirm_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        goToAppSetting(context);
                        context.finish();
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.permissionDenied();
//                        //新提示AlertDialog
//                        new AlertDialog.Builder(context)
//                                .setTitle(context.getResources().getString(R.string.alert_title))
//                                .setMessage(context.getResources().getString(R.string.alert_content))
//                                .setPositiveButton(context.getResources().getString(R.string.confirm_btn), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                }).setCancelable(false).show();
                    }
                }).setCancelable(false).show();
    }

    /**
     * 跳转至当前应用的设置界面
     */
    private void goToAppSetting(final Activity context){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",context.getPackageName(),null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
