package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Background location permission class
 */
public final class AccessBackgroundLocationPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only and should not be referenced externally.
     * If you need the permission name string, use the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.ACCESS_BACKGROUND_LOCATION;

    public static final Parcelable.Creator<AccessBackgroundLocationPermission> CREATOR = new Parcelable.Creator<AccessBackgroundLocationPermission>() {

        @Override
        public AccessBackgroundLocationPermission createFromParcel(Parcel source) {
            return new AccessBackgroundLocationPermission(source);
        }

        @Override
        public AccessBackgroundLocationPermission[] newArray(int size) {
            return new AccessBackgroundLocationPermission[size];
        }
    };

    public AccessBackgroundLocationPermission() {
        // default implementation ignored
    }

    private AccessBackgroundLocationPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        // Background location permission is always a transparent Activity on HyperOS or MIUI
        if (DeviceOs.isHyperOs() || DeviceOs.isMiui()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        // Background location permission is always a transparent Activity on MagicOS
        if (DeviceOs.isMagicOs()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        // Background location permission is always a transparent Activity on HarmonyOS
        if (DeviceOs.isHarmonyOs()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        // On Android 10, the background location page is a transparent Activity,
        // but starting from Android 11 it became an opaque Activity
        if (PermissionVersion.isAndroid10() && !PermissionVersion.isAndroid11()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.LOCATION;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_10;
    }

    @NonNull
    @Override
    public List<IPermission> getForegroundPermissions(@NonNull Context context) {
        // Check if running on Android 12 or higher
        if (PermissionVersion.isAndroid12()) {
            // From Android 12 onward, foreground location can be either fine or coarse
            return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission(), PermissionLists.getAccessCoarseLocationPermission());
        } else {
            // On versions prior to Android 12, foreground location must be fine location
            return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission());
        }
    }

    @Override
    public boolean isBackgroundPermission(@NonNull Context context) {
        // This permission is a background permission
        return true;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid12()) {
            // On Android 12+, foreground location can be either fine or coarse
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest) &&
                    !PermissionLists.getAccessCoarseLocationPermission().isGrantedPermission(context, skipRequest)) {
                return false;
            }
        } else {
            // On Android 11 and below, foreground location must be fine location
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest)) {
                return false;
            }
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        // If foreground location permission is not granted,
        // the “don’t ask again” state for background location should follow it
        if (PermissionVersion.isAndroid12()) {
            // On Android 12+, foreground location can be either fine or coarse
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(activity) &&
                    !PermissionLists.getAccessCoarseLocationPermission().isGrantedPermission(activity)) {
                return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity) &&
                        PermissionLists.getAccessCoarseLocationPermission().isDoNotAskAgainPermission(activity);
            }
        } else {
            // On Android 11 and below, foreground location must be fine location
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(activity)) {
                return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity);
            }
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    public int getRequestIntervalTime(@NonNull Context context) {
        // On Android 11 devices, requesting foreground permission immediately followed by background permission
        // often fails. To avoid this, add a small delay.
        // Why 150ms? Tests show that 100ms still fails occasionally, but 150ms worked consistently.
        // Official docs: https://developer.android.google.cn/about/versions/11/privacy?hl=zh-cn
        return isSupportRequestPermission(context) ? 150 : 0;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // If targeting Android 12 and requesting ACCESS_FINE_LOCATION,
        // you must also request ACCESS_COARSE_LOCATION in the same runtime request.
        // Otherwise, the system ignores the request and logs:
        // "ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION"
        // Docs: https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_12) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_COARSE_LOCATION);
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION);
        } else {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // If targeting Android 12 and requesting ACCESS_FINE_LOCATION,
        // you must also request ACCESS_COARSE_LOCATION in the same runtime request.
        // Otherwise, the system ignores the request and logs:
        // "ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION"
        // Docs: https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_12 &&
                PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_COARSE_LOCATION) &&
                !PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_FINE_LOCATION)) {
            // Background location request can omit coarse location,
            // but must include fine location (otherwise no permission dialog will appear).
            // On Android 12+, both fine and coarse can serve as foreground permissions.
            // For compatibility with Android 11 and below, fine location is still required.
            // The framework checks specifically for cases where coarse is included but fine is missing,
            // since developers may split foreground and background permission requests separately.
            throw new IllegalArgumentException("Applying for background positioning permissions must include \"" +
                    PermissionNames.ACCESS_FINE_LOCATION + "\"");
        }

        int thisPermissionIndex = -1;
        int accessFineLocationPermissionIndex = -1;
        int accessCoarseLocationPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.ACCESS_FINE_LOCATION)) {
                accessFineLocationPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.ACCESS_COARSE_LOCATION)) {
                accessCoarseLocationPermissionIndex = i;
            }
        }

        if (accessFineLocationPermissionIndex != -1 && accessFineLocationPermissionIndex > thisPermissionIndex) {
            // ACCESS_BACKGROUND_LOCATION must come after ACCESS_FINE_LOCATION
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permission");
        }

        if (accessCoarseLocationPermissionIndex != -1 && accessCoarseLocationPermissionIndex > thisPermissionIndex) {
            // ACCESS_BACKGROUND_LOCATION must come after ACCESS_COARSE_LOCATION
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.ACCESS_COARSE_LOCATION + "\" permission");
        }
    }
}
