package com.hjq.permissions.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 * Helpers for building permission settings page intents.
 */
public final class PermissionSettingPage {

    /** Smartisan security component package name */
    private static final String SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME = "com.smartisanos.security";

    /** Huawei phone manager App package name */
    private static final String[] HUA_WEI_MOBILE_MANAGER_APP_PACKAGE_NAMES = { "com.huawei.systemmanager" };

    /** Xiaomi phone manager App package name */
    private static final String[] XIAO_MI_MOBILE_MANAGER_APP_PACKAGE_NAMES = { "com.miui.securitycenter" };

    /** OPPO security center App package name */
    private static final String[] OPPO_SAFE_CENTER_APP_PACKAGE_NAMES = { "com.coloros.safecenter",
                                                                         "com.oplus.safecenter",
                                                                         "com.color.safecenter",
                                                                         "com.oppo.safe" };

    /** vivo security center App package name */
    private static final String[] VIVO_MOBILE_MANAGER_APP_PACKAGE_NAMES = { "com.bairenkeji.icaller",
                                                                            "com.iqoo.secure" };

    /** Smartisan security center package names. */
    private static final String[] SMARTISAN_SECURITY_CENTER_APP_PACKAGE_NAMES = { "com.smartisanos.securitycenter",
                                                                                  SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME };

    /** Returns the Samsung One UI permission page intent. */
    @NonNull
    public static Intent getOneUiPermissionPageIntent(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$AppOpsDetailsActivity");
        Bundle extraShowFragmentArguments = new Bundle();
        extraShowFragmentArguments.putString("package", context.getPackageName());
        intent.putExtra(":settings:show_fragment_args", extraShowFragmentArguments);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        return intent;
    }

    /* ---------------------------------------------------------------------------------------- */

    /** Returns launch intents for Huawei's phone manager app. */
    @NonNull
    public static List<Intent> getHuaWeiMobileManagerAppIntent(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>(1);
        Intent intent;

        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            for (String appPackageName : HUA_WEI_MOBILE_MANAGER_APP_PACKAGE_NAMES) {
                intent = packageManager.getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    intentList.add(intent);
                }
            }
        }

        return intentList;
    }

    /** Returns launch intents for Xiaomi's security center app. */
    @NonNull
    public static List<Intent> getXiaoMiMobileManagerAppIntent(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>(2 + XIAO_MI_MOBILE_MANAGER_APP_PACKAGE_NAMES.length);
        Intent intent;

        // Xiaomi phone manager App -> app management
        intent = new Intent("miui.intent.action.APP_MANAGER");
        intentList.add(intent);

        // Xiaomi phone manager app -> home page, implicit Intent form
        intent = new Intent("miui.intent.action.SECURITY_CENTER");
        intentList.add(intent);

        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            // Xiaomi phone manager app -> home page, explicit package name form
            for (String appPackageName : XIAO_MI_MOBILE_MANAGER_APP_PACKAGE_NAMES) {
                intent = packageManager.getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    intentList.add(intent);
                }
            }
        }

        return intentList;
    }

    /** Returns launch intents for OPPO's security center app. */
    @NonNull
    public static List<Intent> getOppoSafeCenterAppIntent(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>(OPPO_SAFE_CENTER_APP_PACKAGE_NAMES.length);

        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            for (String appPackageName : OPPO_SAFE_CENTER_APP_PACKAGE_NAMES) {
                Intent intent = packageManager.getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    intentList.add(intent);
                }
            }
        }

        return intentList;
    }

    /** Returns launch intents for vivo's phone manager app. */
    @NonNull
    public static List<Intent> getVivoMobileManagerAppIntent(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>(VIVO_MOBILE_MANAGER_APP_PACKAGE_NAMES.length);

        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            for (String appPackageName : VIVO_MOBILE_MANAGER_APP_PACKAGE_NAMES) {
                Intent intent = packageManager.getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    intentList.add(intent);
                }
            }
        }

        return intentList;
    }

    /**
     * Returns launch intents for the Smartisan Security Center app.
     */
    @NonNull
    public static List<Intent> getSmartisanSecurityCenterAppIntent(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>(SMARTISAN_SECURITY_CENTER_APP_PACKAGE_NAMES.length);

        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            for (String appPackageName : SMARTISAN_SECURITY_CENTER_APP_PACKAGE_NAMES) {
                Intent intent = packageManager.getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    intentList.add(intent);
                }
            }
        }

        return intentList;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * Returns the Xiaomi app permission settings page intent.
     */
    @NonNull
    public static Intent getXiaoMiApplicationPermissionPageIntent(@NonNull Context context) {
        return new Intent("miui.intent.action.APP_PERM_EDITOR")
            .putExtra("extra_pkgname", context.getPackageName());
    }

    /**
     * Returns the Meizu app permission settings page intent.
     */
    @NonNull
    public static Intent getMeiZuApplicationPermissionPageIntent(@NonNull Context context) {
        return new Intent("com.meizu.safe.security.SHOW_APPSEC")
            .putExtra("packageName", context.getPackageName());
    }

    /** Returns Smartisan specific permission page intents. */
    @NonNull
    public static List<Intent> getSmartisanPermissionPageIntent() {
        List<Intent> intentList = new ArrayList<>(2);
        Intent intent;

        intent = new Intent(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".action.PACKAGE_OVERVIEW");
        intentList.add(intent);

        intent = new Intent();
        intent.setClassName(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME, SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".PackagesOverview");
        intentList.add(intent);

        return intentList;
    }

    /* ---------------------------------------------------------------------------------------- */

    /** Returns generic permission settings page intents. */
    @NonNull
    public static List<Intent> getCommonPermissionSettingIntent(@NonNull Context context) {
        return getCommonPermissionSettingIntent(context, (IPermission[]) null);
    }

    @NonNull
    public static List<Intent> getCommonPermissionSettingIntent(@NonNull Context context, @Nullable IPermission... permissions) {
        List<Intent> intentList = new ArrayList<>(4);
        intentList.add(getApplicationDetailsSettingsIntent(context, permissions));
        intentList.add(getManageApplicationSettingsIntent());
        intentList.add(getApplicationSettingsIntent());
        intentList.add(getAndroidSettingsIntent());
        return intentList;
    }

    /** Returns the app details settings intent. */
    @NonNull
    public static Intent getApplicationDetailsSettingsIntent(@NonNull Context context, @Nullable IPermission... permissions) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (permissions != null && permissions.length > 0 && DeviceOs.isColorOs()) {
            // OPPO app permission blocked-navigation optimization: https://open.oppomobile.com/new/developmentDoc/info?id=12983
            Bundle bundle = new Bundle();
            List<String> permissionList = PermissionUtils.convertPermissionList(permissions);
            // Elements are native permission name string constants for blocked permissions
            bundle.putStringArrayList("permissionList", permissionList instanceof ArrayList ?
                (ArrayList<String>) permissionList : new ArrayList<>(permissionList));
            intent.putExtras(bundle);
            // Pass the navigation optimization flag
            intent.putExtra("isGetPermission", true);
        }
        return intent;
    }

    /** Returns the manage applications settings intent. */
    @NonNull
    public static Intent getManageApplicationSettingsIntent() {
        return new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
    }

    /** Returns the application settings intent. */
    @NonNull
    public static Intent getApplicationSettingsIntent() {
        return new Intent(Settings.ACTION_APPLICATION_SETTINGS);
    }

    /** Returns the system settings intent. */
    @NonNull
    public static Intent getAndroidSettingsIntent() {
        return new Intent(Settings.ACTION_SETTINGS);
    }
}
