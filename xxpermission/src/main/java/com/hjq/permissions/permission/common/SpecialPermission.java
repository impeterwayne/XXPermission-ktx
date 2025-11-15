package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.PermissionVersion;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Base class for special permissions
 */
public abstract class SpecialPermission extends BasePermission {

    protected SpecialPermission() {
        super();
    }

    protected SpecialPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public PermissionChannel getPermissionChannel(@NonNull Context context) {
        return PermissionChannel.START_ACTIVITY_FOR_RESULT;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        return false;
    }

    @Override
    public int getResultWaitTime(@NonNull Context context) {
        if (!isSupportRequestPermission(context)) {
            return 0;
        }

        // Special permissions always require a certain waiting time
        int waitTime;
        if (PermissionVersion.isAndroid11()) {
            waitTime = 200;
        } else {
            waitTime = 300;
        }

        if (DeviceOs.isEmui() || DeviceOs.isHarmonyOs()) {
            // Need to increase waiting time, otherwise some Huawei models may fail
            // to recognize granted permissions immediately after authorization
            if (PermissionVersion.isAndroid8()) {
                waitTime = 300;
            } else {
                waitTime = 500;
            }
        }
        return waitTime;
    }

    /**
     * Whether the current permission must be statically registered in the manifest file
     */
    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Special permissions by default do not need to be registered in the manifest.
        // This avoids forcing subclasses that define custom special permissions to override this method.
        return false;
    }
}
