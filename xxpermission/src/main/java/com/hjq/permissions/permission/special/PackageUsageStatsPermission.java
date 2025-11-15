package com.hjq.permissions.permission.special;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Permission class for viewing app usage statistics
 */
public final class PackageUsageStatsPermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.PACKAGE_USAGE_STATS;

    public static final Parcelable.Creator<PackageUsageStatsPermission> CREATOR = new Parcelable.Creator<PackageUsageStatsPermission>() {

        @Override
        public PackageUsageStatsPermission createFromParcel(Parcel source) {
            return new PackageUsageStatsPermission(source);
        }

        @Override
        public PackageUsageStatsPermission[] newArray(int size) {
            return new PackageUsageStatsPermission[size];
        }
    };

    public PackageUsageStatsPermission() {
        // default implementation ignored
    }

    private PackageUsageStatsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_5;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid5()) {
            return true;
        }
        return checkOpPermission(context, AppOpsManager.OPSTR_GET_USAGE_STATS, false);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        if (PermissionVersion.isAndroid10()) {
            intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            // Based on testing, adding the package name only works on Android 10 and above
            // If you add the package name on Android 9 or below, it will fail to jump
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);
        }

        if (PermissionVersion.isAndroid5()) {
            intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intentList.add(intent);
        }

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Indicates that this permission needs to be statically registered in the AndroidManifest.xml file
        return true;
    }
}
