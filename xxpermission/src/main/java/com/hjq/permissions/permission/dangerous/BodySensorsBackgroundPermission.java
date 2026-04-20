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
 * Background sensors permission class.
 */
public final class BodySensorsBackgroundPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.BODY_SENSORS_BACKGROUND;

    public static final Parcelable.Creator<BodySensorsBackgroundPermission> CREATOR = new Parcelable.Creator<BodySensorsBackgroundPermission>() {

        @Override
        public BodySensorsBackgroundPermission createFromParcel(Parcel source) {
            return new BodySensorsBackgroundPermission(source);
        }

        @Override
        public BodySensorsBackgroundPermission[] newArray(int size) {
            return new BodySensorsBackgroundPermission[size];
        }
    };

    public BodySensorsBackgroundPermission() {
        // default implementation ignored
    }

    private BodySensorsBackgroundPermission(Parcel in) {
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
        // background sensors permission HyperOS, MIUI, MagicOS, HarmonyOS, EMUI has consistently been a transparent Activity
        if (DeviceOs.isHyperOs() || DeviceOs.isMiui() || DeviceOs.isMagicOs() ||
            DeviceOs.isHarmonyOs() || DeviceOs.isHarmonyOsNextAndroidCompatible() || DeviceOs.isEmui()) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.SENSORS;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_13;
    }

    @NonNull
    @Override
    public List<IPermission> getForegroundPermissions(@NonNull Context context) {
        return PermissionUtils.asArrayList(PermissionLists.getBodySensorsPermission());
    }

    @Override
    public boolean isBackgroundPermission(@NonNull Context context) {
        // Indicates that the current permission is a background permission
        return true;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        // checkbackground sensors permissiongrantedearlier, need to checkforegroundsensorspermission is granted, If the foregroundsensorspermissionnot granted, background sensors permission granted
        if (!PermissionLists.getBodySensorsPermission().isGrantedPermission(context, skipRequest)) {
            return false;
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getBodySensorsPermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        // If the foregroundsensorspermissionnot granted, background sensors permissionDo not ask again state should follow theforegroundsensorspermission
        if (!PermissionLists.getBodySensorsPermission().isGrantedPermission(activity)) {
            return PermissionLists.getBodySensorsPermission().isDoNotAskAgainPermission(activity);
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getBodySensorsPermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    public int getRequestIntervalTime(@NonNull Context context) {
        // , Android 13 devices, requestforeground permission, requestbackground permission will failure
        // hereto avoid this case, so a small delay is added, which avoids the problem
        // Why is the delay 150 milliseconds? In practice, 100 ms can still fail occasionally, but 150 ms worked reliably in repeated tests
        return isSupportRequestPermission(context) ? 150 : 0;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // requestbackground sensorspermissionmust declareforeground sensorspermission
        checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.BODY_SENSORS);
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // Whenproject targetSdkVersion >= 36 , cannot request BODY_SENSORS_BACKGROUND permission, requestread health data in the background permission: READ_HEALTH_DATA_IN_BACKGROUND
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_16) {
            throw new IllegalArgumentException("When the project targetSdkVersion is greater than or equal to " + PermissionVersion.ANDROID_16 +
                                                ", the \"" + getPermissionName() + "\" permission cannot be requested, but the \"" +
                                                PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND + "\" permission should be requested instead");
        }

        // must requestforegroundsensorspermission requestbackground sensors permission
        if (!PermissionUtils.containsPermission(requestList, PermissionNames.BODY_SENSORS)) {
            throw new IllegalArgumentException("Applying for background sensor permissions must contain \"" + PermissionNames.BODY_SENSORS + "\"");
        }

        int thisPermissionIndex = -1;
        int bodySensorsPermissionindex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.BODY_SENSORS)) {
                bodySensorsPermissionindex = i;
            }
        }

        if (bodySensorsPermissionindex != -1 && bodySensorsPermissionindex > thisPermissionIndex) {
            // Please place the BODY_SENSORS_BACKGROUND permission after the BODY_SENSORS permission.
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.BODY_SENSORS + "\" permission");
        }
    }
}