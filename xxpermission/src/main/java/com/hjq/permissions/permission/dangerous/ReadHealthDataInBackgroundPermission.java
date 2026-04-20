package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Background health data read permission class.
 */
public final class ReadHealthDataInBackgroundPermission extends HealthDataBasePermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND;

    public static final Creator<ReadHealthDataInBackgroundPermission> CREATOR = new Creator<ReadHealthDataInBackgroundPermission>() {

        @Override
        public ReadHealthDataInBackgroundPermission createFromParcel(Parcel source) {
            return new ReadHealthDataInBackgroundPermission(source);
        }

        @Override
        public ReadHealthDataInBackgroundPermission[] newArray(int size) {
            return new ReadHealthDataInBackgroundPermission[size];
        }
    };

    public ReadHealthDataInBackgroundPermission() {
        // default implementation ignored
    }

    private ReadHealthDataInBackgroundPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_15;
    }

    @Nullable
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        if (!PermissionVersion.isAndroid14()) {
            // here Android 14 below version returnbackground sensors permission, because Android 14 earlier,
            // Android sensorspermission read sensors , Android 14 health datapermission read permission,
            // Android 14 no background permission, Android 15 background permission, here compatibility issue,
            // hereframework Android 14 HealthConnectManager backgroundread need topermission , Android 15 need to.
            return PermissionUtils.asArrayList(PermissionLists.getBodySensorsBackgroundPermission());
        }
        return null;
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
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.BODY_SENSORS_BACKGROUND, PermissionVersion.ANDROID_14);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        int thisPermissionIndex = -1;
        int readHealthDataHistoryPermissionIndex = -1;
        int otherHealthDataPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_HEALTH_DATA_HISTORY)) {
                readHealthDataHistoryPermissionIndex = i;
            } else if (PermissionApi.isHealthPermission(permission)) {
                otherHealthDataPermissionIndex = i;
            }
        }

        if (readHealthDataHistoryPermissionIndex != -1 && readHealthDataHistoryPermissionIndex > thisPermissionIndex) {
            // Please place the READ_HEALTH_DATA_IN_BACKGROUND permission after the READ_HEALTH_DATA_HISTORY permission.
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_HEALTH_DATA_HISTORY + "\" permission");
        }

        if (otherHealthDataPermissionIndex != -1 && otherHealthDataPermissionIndex > thisPermissionIndex) {
            // please READ_HEALTH_DATA_IN_BACKGROUND permission health datapermission after it
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + requestList.get(otherHealthDataPermissionIndex) + "\" permission");
        }
    }
}