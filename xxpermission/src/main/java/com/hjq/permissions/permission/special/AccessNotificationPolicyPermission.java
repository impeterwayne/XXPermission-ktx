package com.hjq.permissions.permission.special;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : "Do Not Disturb" permission class
 */
public final class AccessNotificationPolicyPermission extends SpecialPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.ACCESS_NOTIFICATION_POLICY;

    public static final Creator<AccessNotificationPolicyPermission> CREATOR = new Creator<AccessNotificationPolicyPermission>() {

        @Override
        public AccessNotificationPolicyPermission createFromParcel(Parcel source) {
            return new AccessNotificationPolicyPermission(source);
        }

        @Override
        public AccessNotificationPolicyPermission[] newArray(int size) {
            return new AccessNotificationPolicyPermission[size];
        }
    };

    public AccessNotificationPolicyPermission() {
        // default implementation ignored
    }

    private AccessNotificationPolicyPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_6;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid6()) {
            return true;
        }
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        // Even though this SystemService should never be null, defensive programming is applied here just in case.
        if (notificationManager == null) {
            return false;
        }
        return notificationManager.isNotificationPolicyAccessGranted();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(6);
        Intent intent;

        // Explanation: Why exclude HarmonyOS and MagicOS?
        // Although the Intent exists and can be detected, and even launches,
        // the system immediately denies the request on these OSes.
        // This issue only occurs on HarmonyOS (tested on 2.0, 3.0, 4.2.0) and MagicOS,
        // not on stock Android or other OEM systems.
        // Note: HarmonyOS never had a 1.0 release — it launched directly at 2.0.
        // -------------------- Divider Line ----------------------
        // Related issues:
        // 1. https://github.com/getActivity/XXPermissions/issues/190
        // 2. https://github.com/getActivity/XXPermissions/issues/233
        // Tested devices where adding the package name causes navigation failure:
        // - Honor Magic V5, Android 15, MagicOS 9.0.1
        // - Honor Magic4, Android 13, MagicOS 7.0
        // - Honor 80 Pro, Android 12, MagicOS 7.0
        // - Honor X20 SE, Android 11, MagicOS 4.1
        // - Honor Play5, Android 10, MagicOS 4.0
        // - Huawei nova 8, Android 10, EMUI 11.0
        if (PermissionVersion.isAndroid10() && !(DeviceOs.isHarmonyOs() || DeviceOs.isMagicOs() || DeviceOs.isEmui())) {
            // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
            intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);
        }

        if (PermissionVersion.isAndroid6()) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
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
        // This permission must be statically declared in AndroidManifest.xml
        return true;
    }
}
