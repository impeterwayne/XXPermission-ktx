package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/16
 *    desc   : Body sensors permission class
 */
public final class BodySensorsPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only and should not be referenced externally.
     * If you need the permission name string, please use the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.BODY_SENSORS;

    public static final Creator<BodySensorsPermission> CREATOR = new Creator<BodySensorsPermission>() {

        @Override
        public BodySensorsPermission createFromParcel(Parcel source) {
            return new BodySensorsPermission(source);
        }

        @Override
        public BodySensorsPermission[] newArray(int size) {
            return new BodySensorsPermission[size];
        }
    };

    public BodySensorsPermission() {
        // default implementation ignored
    }

    private BodySensorsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.SENSORS;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_6;
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // When targetSdkVersion >= 36, BODY_SENSORS cannot be requested.
        // Instead, the READ_HEART_RATE permission should be requested.
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_16) {
            throw new IllegalArgumentException("When the project targetSdkVersion is greater than or equal to " +
                    PermissionVersion.ANDROID_16 + ", the \"" + getPermissionName() +
                    "\" permission cannot be requested, but the \"" +
                    PermissionNames.READ_HEART_RATE + "\" permission should be requested instead");
        }
    }
}
