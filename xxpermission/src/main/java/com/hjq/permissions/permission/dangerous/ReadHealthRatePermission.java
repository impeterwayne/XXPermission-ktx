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
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : Permission class for reading heart rate data
 */
public final class ReadHealthRatePermission extends HealthDataBasePermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.READ_HEART_RATE;

    public static final Creator<ReadHealthRatePermission> CREATOR = new Creator<ReadHealthRatePermission>() {

        @Override
        public ReadHealthRatePermission createFromParcel(Parcel source) {
            return new ReadHealthRatePermission(source);
        }

        @Override
        public ReadHealthRatePermission[] newArray(int size) {
            return new ReadHealthRatePermission[size];
        }
    };

    public ReadHealthRatePermission() {
        // default implementation ignored
    }

    private ReadHealthRatePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_14;
    }

    @Override
    public List<IPermission> getOldPermissions(Context context) {
        return PermissionUtils.asArrayList(PermissionLists.getBodySensorsPermission());
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // If the permission’s introduced version is higher than minSdkVersion,
        // it means this permission may still be requested on older systems.
        // In that case, you need to register the old permission in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.BODY_SENSORS, PermissionVersion.ANDROID_13);
        }
    }
}
