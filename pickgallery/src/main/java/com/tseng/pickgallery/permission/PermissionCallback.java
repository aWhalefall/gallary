package com.tseng.pickgallery.permission;

import java.util.List;

public interface PermissionCallback {
    void onGranted();
    void permissionDenied();
    void onDenied(List<String> deniedPermissions);
}
