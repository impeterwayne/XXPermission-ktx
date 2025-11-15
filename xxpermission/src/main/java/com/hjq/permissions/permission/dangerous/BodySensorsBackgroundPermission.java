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
 *    desc   : Background body sensor permission class
 */
public final class BodySensorsBackgroundPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only and should not be referenced externally.
     * If you need the permission name string, please use the {@link PermissionNames} class.
     */
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
        if (DeviceOs.isHyperOs() || DeviceOs.isMiui()) {
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
        // Indicates this is a background permission
        return true;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        // Before granting background body sensors permission, check that foreground body sensors is granted.
        // If the foreground permission is not granted, the background one is useless even if granted.
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
        // If foreground body sensors is not granted, the "don’t ask again" state of background
        // body sensors follows the foreground one
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
        // On Android 13 devices, requesting foreground sensors immediately followed by
        // background sensors often fails. To avoid this, add a short delay.
        // Why 150ms? Tests showed 100ms still occasionally failed, but 150ms worked consistently.
        return isSupportRequestPermission(context) ? 150 : 0;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // Requesting background body sensors requires foreground body sensors to be registered in manifest
        checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.BODY_SENSORS);
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        // When targetSdkVersion >= 36, BODY_SENSORS_BACKGROUND should not be requested.
        // Instead, request READ_HEALTH_DATA_IN_BACKGROUND permission.
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_16) {
            throw new IllegalArgumentException("When the project targetSdkVersion is greater than or equal to " +
                    PermissionVersion.ANDROID_16 + ", the \"" + getPermissionName() +
                    "\" permission cannot be requested. Use \"" +
                    PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND + "\" instead.");
        }

        // Foreground body sensors must be requested before requesting background body sensors
        if (!PermissionUtils.containsPermission(requestList, PermissionNames.BODY_SENSORS)) {
            throw new IllegalArgumentException("Applying for background sensor permissions must include \"" +
                    PermissionNames.BODY_SENSORS + "\"");
        }

        int thisPermissionIndex = -1;
        int bodySensorsPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.BODY_SENSORS)) {
                bodySensorsPermissionIndex = i;
            }
        }

        if (bodySensorsPermissionIndex != -1 && bodySensorsPermissionIndex > thisPermissionIndex) {
            // Place BODY_SENSORS_BACKGROUND after BODY_SENSORS
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.BODY_SENSORS + "\" permission");
        }
    }
}
