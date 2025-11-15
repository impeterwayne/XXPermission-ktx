package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
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
 *    desc   : Permission class for reading past health data
 */
public final class ReadHealthDataHistoryPermission extends HealthDataBasePermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly from {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.READ_HEALTH_DATA_HISTORY;

    public static final Creator<ReadHealthDataHistoryPermission> CREATOR = new Creator<ReadHealthDataHistoryPermission>() {

        @Override
        public ReadHealthDataHistoryPermission createFromParcel(Parcel source) {
            return new ReadHealthDataHistoryPermission(source);
        }

        @Override
        public ReadHealthDataHistoryPermission[] newArray(int size) {
            return new ReadHealthDataHistoryPermission[size];
        }
    };

    public ReadHealthDataHistoryPermission() {
        // default implementation ignored
    }

    private ReadHealthDataHistoryPermission(Parcel in) {
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

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        int thisPermissionIndex = -1;
        int otherHealthDataPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionApi.isHealthPermission(permission) &&
                    !PermissionUtils.equalsPermission(permission, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                otherHealthDataPermissionIndex = i;
            }
        }

        if (otherHealthDataPermissionIndex != -1 && otherHealthDataPermissionIndex > thisPermissionIndex) {
            // Please place READ_HEALTH_DATA_HISTORY permission after other health data permissions
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + requestList.get(otherHealthDataPermissionIndex) + "\" permission");
        }
    }
}
