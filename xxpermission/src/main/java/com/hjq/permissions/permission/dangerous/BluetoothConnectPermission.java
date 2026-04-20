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
 * Bluetooth connect permission class.
 */
public final class BluetoothConnectPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // note: On Android 12, Bluetooth-related permissions belong to the nearby devices permission group, but before Android 12 they belonged to the location permission group
        return PermissionVersion.isAndroid12() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public int getMinTargetSdkVersion(@NonNull Context context) {
        // vendor permission , targetSdk case ( 31), need to apprequest permission, related issue :
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
        // If the version where this permission was introduced is lower than minSdkVersion, it may be requested on older systems, so the legacy permission must also be declared in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH, PermissionVersion.ANDROID_11);
        }
    }
}