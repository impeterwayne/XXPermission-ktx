package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.PermissionVersion;

/**
 * Base class for special permissions.
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
        return PermissionChannel.START_ACTIVITY;
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

        // Special permissions always require a short wait time
        int waitTime;
        if (PermissionVersion.isAndroid11()) {
            waitTime = 200;
        } else {
            waitTime = 300;
        }

        if (DeviceOs.isEmui() || DeviceOs.isHarmonyOs() || DeviceOs.isHarmonyOsNextAndroidCompatible()) {
            // The wait time must be slightly longer, otherwise some Huawei devices may grant the permission before the result can be detected
            if (PermissionVersion.isAndroid8()) {
                waitTime = 300;
            } else {
                waitTime = 500;
            }
        }
        return waitTime;
    }

    /**
     * Whether the current permission must be declared statically in the manifest file
     */
    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Special permissions do not need to be declared by default, this means the caller defines a custom special permission, also
        return false;
    }
}