package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Full-screen notification permission class
 */
public final class UseFullScreenIntentPermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.USE_FULL_SCREEN_INTENT;

    public static final Parcelable.Creator<UseFullScreenIntentPermission> CREATOR = new Parcelable.Creator<UseFullScreenIntentPermission>() {

        @Override
        public UseFullScreenIntentPermission createFromParcel(Parcel source) {
            return new UseFullScreenIntentPermission(source);
        }

        @Override
        public UseFullScreenIntentPermission[] newArray(int size) {
            return new UseFullScreenIntentPermission[size];
        }
    };

    public UseFullScreenIntentPermission() {
        // default implementation ignored
    }

    private UseFullScreenIntentPermission(Parcel in) {
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
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid14()) {
            return true;
        }
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        // Although this SystemService is never null, still apply defensive programming just in case
        if (notificationManager == null) {
            return false;
        }
        return notificationManager.canUseFullScreenIntent();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(6);
        Intent intent;

        if (PermissionVersion.isAndroid14()) {
            intent = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);
        }

        // Based on testing, MIUI and HyperOS do not support setting full-screen notification permission in the notification page,
        // but stock Android does.
        if (DeviceOs.isHyperOs() || DeviceOs.isMiui()) {
            intent = getAndroidSettingIntent();
            intentList.add(intent);
            return intentList;
        }

        if (PermissionVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
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

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // Full-screen notification permission must be used together with notification permissions (NOTIFICATION_SERVICE or POST_NOTIFICATIONS)
        if (!PermissionUtils.containsPermission(requestList, PermissionNames.NOTIFICATION_SERVICE) &&
                !PermissionUtils.containsPermission(requestList, PermissionNames.POST_NOTIFICATIONS)) {
            throw new IllegalArgumentException("The \"" + getPermissionName() + "\" permission must be requested together with a notification permission. "
                    + "(\"" + PermissionNames.NOTIFICATION_SERVICE + "\" or \"" + PermissionNames.POST_NOTIFICATIONS + "\")");
        }

        int thisPermissionIndex = -1;
        int notificationServicePermissionIndex = -1;
        int postNotificationsPermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, getPermissionName())) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.NOTIFICATION_SERVICE)) {
                notificationServicePermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.POST_NOTIFICATIONS)) {
                postNotificationsPermissionIndex = i;
            }
        }

        if (notificationServicePermissionIndex != -1 && notificationServicePermissionIndex > thisPermissionIndex) {
            // Please place USE_FULL_SCREEN_INTENT permission after NOTIFICATION_SERVICE permission
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.NOTIFICATION_SERVICE + "\" permission");
        }

        if (postNotificationsPermissionIndex != -1 && postNotificationsPermissionIndex > thisPermissionIndex) {
            // Please place USE_FULL_SCREEN_INTENT permission after POST_NOTIFICATIONS permission
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.POST_NOTIFICATIONS + "\" permission");
        }
    }
}
