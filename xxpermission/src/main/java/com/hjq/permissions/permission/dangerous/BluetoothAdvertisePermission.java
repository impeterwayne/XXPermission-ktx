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
 *    desc   : Bluetooth advertise permission class
 */
public final class BluetoothAdvertisePermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only and should not be referenced externally.
     * If you need the permission name string, please use the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.BLUETOOTH_ADVERTISE;

    public static final Parcelable.Creator<BluetoothAdvertisePermission> CREATOR = new Parcelable.Creator<BluetoothAdvertisePermission>() {

        @Override
        public BluetoothAdvertisePermission createFromParcel(Parcel source) {
            return new BluetoothAdvertisePermission(source);
        }

        @Override
        public BluetoothAdvertisePermission[] newArray(int size) {
            return new BluetoothAdvertisePermission[size];
        }
    };

    public BluetoothAdvertisePermission() {
        // default implementation ignored
    }

    private BluetoothAdvertisePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        // Note: In Android 12, Bluetooth-related permissions belong to the Nearby Devices group.
        // Before Android 12, Bluetooth-related permissions belonged to the Location group.
        return PermissionVersion.isAndroid12() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getMinTargetSdkVersion(@NonNull Context context) {
        // Some OEMs changed Bluetooth permission mechanics so that even when targetSdk is below 31,
        // apps still need to request this permission. Related issues:
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
        // If the version where this permission was introduced is greater than minSdkVersion,
        // it indicates the permission may be requested on older systems. In that case,
        // the legacy permission must be declared in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH_ADMIN, PermissionVersion.ANDROID_11);
        }
    }
}
