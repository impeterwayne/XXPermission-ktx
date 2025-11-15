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
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Bluetooth scan permission class
 */
public final class BluetoothScanPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only and should not be referenced externally.
     * If you need the permission name string, please use the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.BLUETOOTH_SCAN;

    public static final Parcelable.Creator<BluetoothScanPermission> CREATOR = new Parcelable.Creator<BluetoothScanPermission>() {

        @Override
        public BluetoothScanPermission createFromParcel(Parcel source) {
            return new BluetoothScanPermission(source);
        }

        @Override
        public BluetoothScanPermission[] newArray(int size) {
            return new BluetoothScanPermission[size];
        }
    };

    public BluetoothScanPermission() {
        // default implementation ignored
    }

    private BluetoothScanPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        // Note: Starting with Android 12, Bluetooth-related permissions belong to the Nearby Devices group.
        // On versions prior to Android 12, they belong to the Location group.
        return PermissionVersion.isAndroid12() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public int getMinTargetSdkVersion(@NonNull Context context) {
        // Some OEMs modified Bluetooth permission behavior.
        // Even if targetSdk < 31, apps may still need to request this permission.
        // Related issues:
        // 1. https://github.com/getActivity/XXPermissions/issues/123
        // 2. https://github.com/getActivity/XXPermissions/issues/302
        return PermissionVersion.ANDROID_6;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // On Android 11 and below, scanning Bluetooth requires fine location permission
        return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission());
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        // If the version where this permission was introduced is greater than minSdkVersion,
        // it may still be requested on older systems, so old permissions must be declared in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH_ADMIN, PermissionVersion.ANDROID_11);
            // Legacy issue before Android 12: scanning Bluetooth results required fine location permission
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION, PermissionVersion.ANDROID_11);
        }

        // Skip check if ACCESS_FINE_LOCATION is already in the request list
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_FINE_LOCATION)) {
            return;
        }
        // Skip check if this permission is not registered in the manifest
        if (currentPermissionInfo == null) {
            return;
        }
        // Skip check if the permission in the manifest has neverForLocation flag set
        if (currentPermissionInfo.neverForLocation()) {
            return;
        }

        // Bluetooth permissions guide:
        // https://developer.android.google.cn/guide/topics/connectivity/bluetooth/permissions?hl=zh-cn#assert-never-for-location
        // If your app does not use Bluetooth scan results to obtain physical location, you can assert that
        // with android:usesPermissionFlags="neverForLocation".
        // Steps:
        // 1. Add android:usesPermissionFlags="neverForLocation" to your BLUETOOTH_SCAN permission in manifest.
        // 2. If your app DOES need location from Bluetooth, then you must also dynamically request ACCESS_FINE_LOCATION.
        // In most cases, apps do not use Bluetooth for physical location, so option (1) is recommended.
        String maxSdkVersionString = (currentPermissionInfo.maxSdkVersion != PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION) ?
                "android:maxSdkVersion=\"" + currentPermissionInfo.maxSdkVersion + "\" " : "";
        throw new IllegalArgumentException("If your app doesn't use " + currentPermissionInfo.name +
                " to get physical location, please change the <uses-permission android:name=\"" +
                currentPermissionInfo.name + "\" " + maxSdkVersionString + "/> node in the manifest file to " +
                "<uses-permission android:name=\"" + currentPermissionInfo.name +
                "\" android:usesPermissionFlags=\"neverForLocation\" " + maxSdkVersionString + "/>. " +
                "If your app does need \"" + currentPermissionInfo.name + "\" to get physical location, " +
                "you must also add \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permission.");
    }
}
