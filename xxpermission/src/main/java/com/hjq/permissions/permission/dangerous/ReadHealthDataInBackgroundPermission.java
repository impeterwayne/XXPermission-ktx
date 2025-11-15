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
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : Permission class for reading health data in the background
 */
public final class ReadHealthDataInBackgroundPermission extends HealthDataBasePermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
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
            // Explanation: Why only return background sensor permission below Android 14?
            // Because before Android 14, the Android sensor permission was essentially for reading heart rate sensors.
            // Starting from Android 14, this was split into health data permissions such as "read heart rate".
            // However, Android 14 did not introduce a corresponding background permission — it only appeared in Android 15.
            // This creates a compatibility issue:
            // On Android 14, the framework assumes that using HealthConnectManager to read heart rate data in the background
            // does not require a permission. From Android 15 onwards, this background permission is required.
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
        // If the permission’s introduced version is higher than minSdkVersion,
        // it means the permission might still be requested on older systems.
        // In that case, the corresponding old permission must be registered in AndroidManifest.xml.
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
            // Please place READ_HEALTH_DATA_IN_BACKGROUND permission after READ_HEALTH_DATA_HISTORY
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.READ_HEALTH_DATA_HISTORY + "\" permission");
        }

        if (otherHealthDataPermissionIndex != -1 && otherHealthDataPermissionIndex > thisPermissionIndex) {
            // Please place READ_HEALTH_DATA_IN_BACKGROUND permission after other health data permissions
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + requestList.get(otherHealthDataPermissionIndex) + "\" permission");
        }
    }
}
