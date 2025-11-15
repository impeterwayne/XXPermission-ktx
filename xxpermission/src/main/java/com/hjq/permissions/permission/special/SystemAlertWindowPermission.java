package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Floating window (draw over other apps) permission class
 */
public final class SystemAlertWindowPermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.SYSTEM_ALERT_WINDOW;

    public static final Parcelable.Creator<SystemAlertWindowPermission> CREATOR = new Parcelable.Creator<SystemAlertWindowPermission>() {

        @Override
        public SystemAlertWindowPermission createFromParcel(Parcel source) {
            return new SystemAlertWindowPermission(source);
        }

        @Override
        public SystemAlertWindowPermission[] newArray(int size) {
            return new SystemAlertWindowPermission[size];
        }
    };

    private static final String OP_SYSTEM_ALERT_WINDOW_FIELD_NAME = "OP_SYSTEM_ALERT_WINDOW";
    private static final int OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE = 24;

    public SystemAlertWindowPermission() {
        // default implementation ignored
    }

    private SystemAlertWindowPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        // Although the floating window permission was officially added in Android 6.0,
        // some domestic manufacturers (OEMs) added it themselves in earlier versions,
        // and the framework already has compatibility for it.
        // To support lower Android versions, the introduced version here is set to API 17
        // (the minimum supported version of this framework).
        return PermissionVersion.ANDROID_4_2;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }

        if (!PermissionVersion.isAndroid4_4()) {
            return true;
        }

        // Tested on vivo X7 Plus (Android 5.1) and OPPO A53 (Android 5.1 ColorOS 2.1),
        // results were inaccurate. Debugging showed it was not due to changes in OP_SYSTEM_ALERT_WINDOW,
        // but likely because vivo and OPPO modified the entire overlay window mechanism.
        return checkOpPermission(context, OP_SYSTEM_ALERT_WINDOW_FIELD_NAME, OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE, true);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(7);
        Intent intent;

        if (PermissionVersion.isAndroid6()) {
            // If the system is HyperOS, do not jump to the MIUI permission page,
            // because the user still needs to go into “Other permissions” to find the overlay option.
            // It’s more straightforward to jump directly to the full overlay permission list.
            // Related Github issue: https://github.com/getActivity/XXPermissions/issues/342
            if (PermissionVersion.isAndroid11() && !DeviceOs.isHyperOs() &&
                    (DeviceOs.isMiui() && DeviceOs.isMiuiOptimization())) {
                // Starting with Android 11, you can’t directly jump to the specific permission page,
                // only to the list of apps with overlay permissions.
                // MIUI is more user-friendly and doesn’t block the jump, but other OEMs may prevent it.
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }

            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            // Adding a package name in Android 11 has no effect.
            // Official doc: https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intentList.add(intent);

        } else {

            // Note: No need to check for HarmonyOS. On HarmonyOS 2.0 the API level is reported as 29 (Android 10),
            // so it will follow the Android 10+ logic above, not this branch.
            if (DeviceOs.isEmui()) {
                // EMUI history: http://www.360doc.com/content/19/1017/10/9113704_867381705.shtml
                // Huawei EMUI versions overview: https://blog.csdn.net/weixin_39959369/article/details/117351161

                Intent addViewMonitorActivityIntent = new Intent();
                // EMUI 3.1 adaptation (e.g., Huawei Honor 7 Android 5.0, M2 Youth Android 5.1, Enjoy 5S Android 5.1)
                addViewMonitorActivityIntent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");

                Intent notificationManagementActivityIntent = new Intent();
                // EMUI 3.0 adaptation (e.g., Huawei Maimang 3S Android 4.4)
                notificationManagementActivityIntent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");

                // Get manufacturer OS version
                String osVersionName = DeviceOs.getOsVersionName();

                if (osVersionName.startsWith("3.0")) {
                    // EMUI 3.0, 3.0.1
                    intentList.add(notificationManagementActivityIntent);
                    intentList.add(addViewMonitorActivityIntent);
                } else {
                    // EMUI 3.1 and others
                    intentList.add(addViewMonitorActivityIntent);
                    intentList.add(notificationManagementActivityIntent);
                }

                // Huawei Mobile Manager main page
                intentList.addAll(PermissionSettingPage.getHuaWeiMobileManagerAppIntent(context));

            } else if (DeviceOs.isMiui()) {

                // If MIUI optimization is disabled, skip this logic.
                // Xiaomi phones can also enable overlay permission from the app details page
                // (it just requires one more step).
                if (DeviceOs.isMiuiOptimization()) {
                    intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                    intentList.add(intent);
                }

                // Xiaomi Mobile Manager main page
                intentList.addAll(PermissionSettingPage.getXiaoMiMobileManagerAppIntent(context));

            } else if (DeviceOs.isColorOs()) {
                // com.color.safecenter was the old OPPO security center package, later changed to com.oppo.safe in ColorOS 2.1.
                // Tested devices: OPPO R7 Plus (Android 5.0, ColorOS 2.1), OPPO R7s (Android 4.4, ColorOS 2.1).
                // Direct overlay activity intents caused SecurityException, so instead we jump to higher-level settings pages.

                intent = new Intent();
                intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionTopActivity");
                intentList.add(intent);

                intentList.addAll(PermissionSettingPage.getOppoSafeCenterAppIntent(context));

            } else if (DeviceOs.isFuntouchOs()) {
                // Direct overlay activity intents caused SecurityException on vivo devices.
                // On vivo X7 Plus (Android 5.1) it jumped but displayed a blank page.
                // So we fallback to opening the Vivo Mobile Manager main page.
                intentList.addAll(PermissionSettingPage.getVivoMobileManagerAppIntent(context));

            } else if (DeviceOs.isOneUi()) {
                intent = PermissionSettingPage.getOneUiPermissionPageIntent(context);
                intentList.add(intent);

            } else if (DeviceOs.isSmartisanOs() && !PermissionVersion.isAndroid5_1()) {
                // On SmartisanOS 5.1+, overlay can be enabled directly from app details.
                // On versions below 5.1 (e.g., Android 4.4), must open the Security Center instead.
                intentList.addAll(PermissionSettingPage.getSmartisanPermissionPageIntent());
                intentList.addAll(PermissionSettingPage.getSmartisanSecurityCenterAppIntent(context));
            }

            // Notes:
            // - 360’s first phone (360 N4) launched with Android 6.0, so no need for a special overlay setting page.
            // - On Meizu phones running 6.0, overlay can be enabled directly via app details page.
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
