package com.hjq.permissions.permission.dangerous;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/14
 *    desc   : Bluetooth connect permission class
 */
public final class BluetoothConnectPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only and should not be referenced externally.
     * If you need the permission name string, please use the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.BLUETOOTH_CONNECT;

    public static final Parcelable.Creator<BluetoothConnectPermission> CREATOR = new Parcelable.Creator<BluetoothConnectPermission>() {

        @Override
        public BluetoothConnectPermission createFromParcel(Parcel source) {
            return new BluetoothConnectPermission(source);
        }

        @Override
        public BluetoothConnectPermission[] newArray(int size) {
            return new BluetoothConnectPermission[size];
        }
    };

    public BluetoothConnectPermission() {
        // default implementation ignored
    }

    private BluetoothConnectPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        // Note: Starting from Android 12, Bluetooth-related permissions belong to the Nearby Devices group.
        // On versions before Android 12, they belong to the Location group.
        return PermissionVersion.isAndroid12() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public int getMinTargetSdkVersion(@NonNull Context context) {
        // Some OEMs modified the Bluetooth permission mechanism.
        // Even if targetSdk < 31, apps still need to request this permission.
        // Related issues:
        // 1. https://github.com/getActivity/XXPermissions/issues/123
        // 2. https://github.com/getActivity/XXPermissions/issues/302
        return PermissionVersion.ANDROID_6;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // If the introduced version of this permission is greater than minSdkVersion,
        // it means this permission may still need to be requested on older systems,
        // so the legacy permission must be declared in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH, PermissionVersion.ANDROID_11);
        }
    }
}
