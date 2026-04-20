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
 * Background location permission class.
 */
public final class AccessBackgroundLocationPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // background location permission request page Android 10 also transparent Activity, Android 11 opaque Activity
        if (PermissionVersion.getSdkVersion() == getFromAndroidVersion(context)) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        // background location permission HyperOS, MIUI, MagicOS, HarmonyOS, EMUI has consistently been a transparent Activity
        if (DeviceOs.isHyperOs() || DeviceOs.isMiui() || DeviceOs.isMagicOs() ||
            DeviceOs.isHarmonyOs() || DeviceOs.isHarmonyOsNextAndroidCompatible() || DeviceOs.isEmui()) {
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
        // Check whether the current runtime is on Android 12 above
        if (PermissionVersion.isAndroid12()) {
            // If , foreground location permission precise location permission approximate location permission
            return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission(), PermissionLists.getAccessCoarseLocationPermission());
        } else {
            // Ifnot , foreground location permission precise location permission
            return PermissionUtils.asArrayList(PermissionLists.getAccessFineLocationPermission());
        }
    }

    @Override
    public boolean isBackgroundPermission(@NonNull Context context) {
        // Indicates that the current permission is a background permission
        return true;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid12()) {
            // on Android 12 and later, foreground location permission precise location permission approximate location permission
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(context, skipRequest) &&
                !PermissionLists.getAccessCoarseLocationPermission().isGrantedPermission(context, skipRequest)) {
                return false;
            }
        } else {
            // on Android 11 and earlier, foreground location permissionrequires the precise location permission
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
        // If the foreground location permission is not granted, background location permissionDo not ask again state should follow the foreground location permission
        if (PermissionVersion.isAndroid12()) {
            // on Android 12 and later, foreground location permission precise location permission approximate location permission
            if (!PermissionLists.getAccessFineLocationPermission().isGrantedPermission(activity) &&
                !PermissionLists.getAccessCoarseLocationPermission().isGrantedPermission(activity)) {
                return PermissionLists.getAccessFineLocationPermission().isDoNotAskAgainPermission(activity) &&
                        PermissionLists.getAccessCoarseLocationPermission().isDoNotAskAgainPermission(activity);
            }
        } else {
            // on Android 11 and earlier, foreground location permissionrequires the precise location permission
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
        // , on Android 11 devices, requestforeground permission, requestbackground permission will failure
        // hereto avoid this case, so a small delay is added, which avoids the problem
        // Why is the delay 150 milliseconds? In practice, 100 ms can still fail occasionally, but 150 ms worked reliably in repeated tests
        // Official documentation: https://developer.android.google.cn/about/versions/11/privacy?hl=zh-cn
        return isSupportRequestPermission(context) ? 150 : 0;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // If your app targets Android 12 and requests ACCESS_FINE_LOCATION permission
        // you must also request ACCESS_COARSE_LOCATION permission.You must includes both permissions in the same runtime request
        // If you try to request only ACCESS_FINE_LOCATION, the system ignores the request and logs the following error in Logcat:
        // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
        // Official compatibility documentation: https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_12) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_COARSE_LOCATION);
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION);
        } else {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // If your app targets Android 12 and requests ACCESS_FINE_LOCATION permission
        // you must also request ACCESS_COARSE_LOCATION permission.You must includes both permissions in the same runtime request
        // If you try to request only ACCESS_FINE_LOCATION, the system ignores the request and logs the following error in Logcat:
        // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
        // Official compatibility documentation: https://developer.android.google.cn/develop/sensors-and-location/location/permissions/runtime?hl=zh-cn#approximate-request
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_12 &&
            PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_COARSE_LOCATION) &&
            !PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_FINE_LOCATION)) {
            // A background location request does not have to includes approximate location permission, but it must includes the precise location permission, otherwise the background location permission cannot be requested
            // causesthe authorization dialog cannot be shown, , Android 12 this issue has been resolved
            // on Android 12 and later, request the background location permission precise location permission approximate location permission foreground location permission
            // compatibility Android 12 below devicealso , otherwise Android 11 belowdevicewill
            // This also explains why the code does not simply check whether precise location permission is includesd, and instead specifically checks for the case where approximate location permission is present but precise location permission is missing
            // This is because the framework considers that callers may splitforeground location permission (includesprecise location and approximate location permission) and background location permissioninto two separate permission requests
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
            // Please place the ACCESS_BACKGROUND_LOCATION permission after the ACCESS_FINE_LOCATION permission.
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.ACCESS_FINE_LOCATION + "\" permission");
        }

        if (accessCoarseLocationPermissionIndex != -1 && accessCoarseLocationPermissionIndex > thisPermissionIndex) {
            // Please place the ACCESS_BACKGROUND_LOCATION permission after the ACCESS_COARSE_LOCATION permission.
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.ACCESS_COARSE_LOCATION + "\" permission");
        }
    }
}