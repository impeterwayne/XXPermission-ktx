package com.hjq.permissions.permission.special;

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
 *    desc   : Install applications permission class
 */
public final class RequestInstallPackagesPermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.REQUEST_INSTALL_PACKAGES;

    public static final Parcelable.Creator<RequestInstallPackagesPermission> CREATOR = new Parcelable.Creator<RequestInstallPackagesPermission>() {

        @Override
        public RequestInstallPackagesPermission createFromParcel(Parcel source) {
            return new RequestInstallPackagesPermission(source);
        }

        @Override
        public RequestInstallPackagesPermission[] newArray(int size) {
            return new RequestInstallPackagesPermission[size];
        }
    };

    public RequestInstallPackagesPermission() {
        // default implementation ignored
    }

    private RequestInstallPackagesPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_8;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid8()) {
            return true;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(6);
        Intent intent;

        if (PermissionVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            // If adding the package name data prevents jumping, remove the package name data
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intentList.add(intent);
        }

        intent = getApplicationDetailsSettingIntent(context);
        intentList.add(intent);

        intent = getManageApplicationSettingIntent();
        intentList.add(intent);

        intent = getApplicationSettingIntent();
        intentList.add(intent);

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Indicates that this permission must be statically registered in the AndroidManifest.xml file
        return true;
    }
}
