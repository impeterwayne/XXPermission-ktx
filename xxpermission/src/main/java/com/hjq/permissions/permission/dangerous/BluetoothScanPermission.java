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
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Bluetooth scan permission.
 */
public final class BluetoothScanPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // Android 12 below requires the precise location permission
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
        // If the version where this permission was introduced is lower than minSdkVersion, it may be requested on older systems, so the legacy permission must also be declared in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, Manifest.permission.BLUETOOTH_ADMIN, PermissionVersion.ANDROID_11);
            // Android 12 earlier issue, resultrequires the precise location permission
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION, PermissionVersion.ANDROID_11);
        }

        // IfRequested permissionsalready includes precise location permission, check
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_FINE_LOCATION)) {
            return;
        }
        // Skip this check if the current permission is not declared in the manifest.
        if (currentPermissionInfo == null) {
            return;
        }
        // Skip this check if the current permission is declared in the manifest and has the neverForLocation flag set.
        if (currentPermissionInfo.neverForLocation()) {
            return;
        }

        // permission: https://developer.android.google.cn/guide/topics/connectivity/bluetooth/permissions?hl=zh-cn#assert-never-for-location
        // If app result location, app permission location. , please completebelow :
        // attributeadd android:usesPermissionFlags BLUETOOTH_SCAN permissiondeclare , attribute valuesettings neverForLocation
        String maxSdkVersionString = (currentPermissionInfo.maxSdkVersion != PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION) ?
            "android:maxSdkVersion=\"" + currentPermissionInfo.maxSdkVersion + "\" " : "";
        // , :
        // 1. need to permission location: you need to declare permission android:usesPermissionFlags="neverForLocation"
        // 2. need to permission location: request permission , also need toruntime request ACCESS_FINE_LOCATION permission
        // case , need to permission location, so
        throw new IllegalArgumentException("If your app doesn't use " + currentPermissionInfo.name +
            " to get physical location, " + "please change the <uses-permission android:name=\"" +
            currentPermissionInfo.name + "\" " + maxSdkVersionString + "/> node in the " +
            "manifest file to <uses-permission android:name=\"" + currentPermissionInfo.name +
            "\" android:usesPermissionFlags=\"neverForLocation\" " + maxSdkVersionString + "/> node, " +
            "if your app need use \"" + currentPermissionInfo.name + "\" to get physical location, " +
            "also need to add \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permissions");
    }
}