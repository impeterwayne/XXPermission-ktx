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
 * Sensors permission class.
 */
public final class BodySensorsPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // Whenproject targetSdkVersion >= 36 , cannot request BODY_SENSORS permission, request read heart rate permission: READ_HEART_RATE
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_16) {
            throw new IllegalArgumentException("When the project targetSdkVersion is greater than or equal to " +
                PermissionVersion.ANDROID_16 + ", the \"" + getPermissionName() +
                "\" permission cannot be requested, but the \"" +
                PermissionNames.READ_HEART_RATE + "\" permission should be requested instead");
        }
    }
}