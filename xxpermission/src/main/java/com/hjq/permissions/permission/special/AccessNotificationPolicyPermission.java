package com.hjq.permissions.permission.special;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceBrand;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * Do Not Disturb permission class.
 */
public final class AccessNotificationPolicyPermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // Although this SystemService should never be null, keep the check for defensive programming.
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

        // here , HarmonyOS and MagicOS, because code can detect the Intent and even navigate to it, but access is denied immediately
        // Testing on other vendor systems and stock Android did not show this issue, has HarmonyOS has issue
        // because this Intent is hidden, it cannot be used, HarmonyOS 2.0, 3.0, 4.2.0 has issue
        // Do not ask whether HarmonyOS 1.0 has this issue, HarmonyOS was released as version 2.0 from the start, 1.0 version 1.0 never shipped publicly
        // ------------------------ Decorative separator ----------------------------
        // related issue :
        // 1. https://github.com/getActivity/XXPermissions/issues/190
        // 2. https://github.com/getActivity/XXPermissions/issues/233
        // , The Honor devices listed below all fail to navigate correctly when a package name is added
        // Magic V5 Android 15 MagicOS 9.0.1
        // magic4 Android 13 MagicOS 7.0
        // 80 Pro Android 12 MagicOS 7.0
        // X20 SE Android 11 MagicOS 4.1
        // Play5 Android 10 MagicOS 4.0
        // Huawei nova 8 Android 10 EMUI 11.0
        if (PermissionVersion.isAndroid10() && !(DeviceOs.isHarmonyOs() || DeviceOs.isHarmonyOsNextAndroidCompatible() ||
                                                 DeviceOs.isMagicOs() || DeviceOs.isEmui() ||
                                                 DeviceBrand.isHuaWei() || DeviceBrand.isHonor())) {
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
        // Indicates that this permission must be declared statically in AndroidManifest.xml.
        return true;
    }
}