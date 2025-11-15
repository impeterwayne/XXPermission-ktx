package com.hjq.permissions.permission.dangerous;

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
 *    desc   : Wi-Fi permission class
 */
public final class NearbyWifiDevicesPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant is for internal framework use only and not for external reference.
     * If you need the permission name string, please use {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.NEARBY_WIFI_DEVICES;

    public static final Parcelable.Creator<NearbyWifiDevicesPermission> CREATOR = new Parcelable.Creator<NearbyWifiDevicesPermission>() {

        @Override
        public NearbyWifiDevicesPermission createFromParcel(Parcel source) {
            return new NearbyWifiDevicesPermission(source);
        }

        @Override
        public NearbyWifiDevicesPermission[] newArray(int size) {
            return new NearbyWifiDevicesPermission[size];
        }
    };

    public NearbyWifiDevicesPermission() {
        // default implementation ignored
    }

    private NearbyWifiDevicesPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        // Note: On Android 13, Wi-Fi-related permissions belong to the Nearby Devices group;
        // before Android 13, they belong to the Location group.
        return PermissionVersion.isAndroid13() ? PermissionGroups.NEARBY_DEVICES : PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_13;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // On Android 12 and below, using Wi-Fi features requires the fine location permission
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
        // If the version when this permission was introduced is greater than minSdkVersion,
        // the permission may be requested on older systems; register the legacy permission in AndroidManifest.xml
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION, PermissionVersion.ANDROID_12_L);
        }

        // Skip checks if ACCESS_FINE_LOCATION is already requested
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_FINE_LOCATION)) {
            return;
        }
        // Skip if this permission is not declared in the manifest
        if (currentPermissionInfo == null) {
            return;
        }
        // Skip if the manifest declaration has the neverForLocation flag
        if (currentPermissionInfo.neverForLocation()) {
            return;
        }

        // Wi-Fi permission docs:
        // https://developer.android.google.cn/about/versions/13/features/nearby-wifi-devices-permission?hl=en#assert-never-for-location
        // When targeting Android 13, consider whether your app infers physical location via Wi-Fi APIs.
        // If not, explicitly assert this by setting usesPermissionFlags="neverForLocation" in the manifest.
        String maxSdkVersionString = (currentPermissionInfo.maxSdkVersion != PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION) ?
                "android:maxSdkVersion=\"" + currentPermissionInfo.maxSdkVersion + "\" " : "";
        // Depending on your scenario, there are two solutions:
        //   1) If you DO NOT use Wi-Fi to derive physical location: add android:usesPermissionFlags="neverForLocation" to the manifest permission.
        //   2) If you DO use Wi-Fi to derive physical location: when requesting the Wi-Fi permission, also request ACCESS_FINE_LOCATION at runtime.
        // In most cases, apps do not use Wi-Fi to derive location, so option (1) is recommended.
        throw new IllegalArgumentException("If your app doesn't use " + currentPermissionInfo.name +
                " to get physical location, please change the <uses-permission android:name=\"" +
                currentPermissionInfo.name + "\" " + maxSdkVersionString + "/> node in the " +
                "manifest file to <uses-permission android:name=\"" + currentPermissionInfo.name +
                "\" android:usesPermissionFlags=\"neverForLocation\" " + maxSdkVersionString + "/>. " +
                "If your app does need to use \"" + currentPermissionInfo.name + "\" to get physical location, " +
                "you must also add the \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permission.");
    }
}
