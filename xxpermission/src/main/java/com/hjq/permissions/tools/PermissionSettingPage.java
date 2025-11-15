package com.hjq.permissions.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/03/12
 *    desc   : Permission settings page helpers.
 *             Builds intents to navigate to app/system settings.
 */
public final class PermissionSettingPage {

    /** Huawei Mobile Manager app package name */
    private static final String HUA_WEI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.huawei.systemmanager";

    /** Xiaomi Mobile Manager app package name */
    private static final String XiAO_MI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.miui.securitycenter";

    /** OPPO Security Center app package names */
    private static final String OPPO_SAFE_CENTER_APP_PACKAGE_NAME_1 = "com.oppo.safe";
    private static final String OPPO_SAFE_CENTER_APP_PACKAGE_NAME_2 = "com.color.safecenter";
    private static final String OPPO_SAFE_CENTER_APP_PACKAGE_NAME_3 = "com.oplus.safecenter";

    /** vivo Security Center app package name */
    private static final String VIVO_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.iqoo.secure";

    /** Smartisan Security Center package name */
    private static final String SMARTISAN_SECURITY_CENTER_APP_PACKAGE_NAME = "com.smartisanos.securitycenter";
    /** Smartisan Security Component package name */
    private static final String SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME = "com.smartisanos.security";

    /**
     * Get Samsung OneUI permission settings intent.
     */
    @NonNull
    public static Intent getOneUiPermissionPageIntent(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$AppOpsDetailsActivity");
        Bundle extraShowFragmentArguments = new Bundle();
        extraShowFragmentArguments.putString("package", context.getPackageName());
        intent.putExtra(":settings:show_fragment_args", extraShowFragmentArguments);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        return intent;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * Return Huawei Mobile Manager app intents.
     */
    @NonNull
    public static List<Intent> getHuaWeiMobileManagerAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(1);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(HUA_WEI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (intent != null) {
            intentList.add(intent);
        }
        return intentList;
    }

    /**
     * Return Xiaomi Mobile Manager app intents.
     */
    @NonNull
    public static List<Intent> getXiaoMiMobileManagerAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(3);

        // Xiaomi Mobile Manager -> App management
        intentList.add(new Intent("miui.intent.action.APP_MANAGER"));

        // Xiaomi Mobile Manager -> Home (implicit intent)
        intentList.add(new Intent("miui.intent.action.SECURITY_CENTER"));

        // Xiaomi Mobile Manager -> Home (explicit by package)
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(XiAO_MI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (intent != null) {
            intentList.add(intent);
        }
        return intentList;
    }

    /**
     * Get OPPO Security Center app intents.
     */
    @NonNull
    public static List<Intent> getOppoSafeCenterAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(3);

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(OPPO_SAFE_CENTER_APP_PACKAGE_NAME_1);
        if (intent != null) intentList.add(intent);

        intent = context.getPackageManager().getLaunchIntentForPackage(OPPO_SAFE_CENTER_APP_PACKAGE_NAME_2);
        if (intent != null) intentList.add(intent);

        intent = context.getPackageManager().getLaunchIntentForPackage(OPPO_SAFE_CENTER_APP_PACKAGE_NAME_3);
        if (intent != null) intentList.add(intent);

        return intentList;
    }

    /**
     * Get vivo Security Center app intent.
     */
    @NonNull
    public static List<Intent> getVivoMobileManagerAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(1);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(VIVO_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (intent != null) {
            intentList.add(intent);
        }
        return intentList;
    }

    /**
     * Get Smartisan Security Center app intents.
     */
    @NonNull
    public static List<Intent> getSmartisanSecurityCenterAppIntent(Context context) {
        List<Intent> intentList = new ArrayList<>(2);

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME);
        if (intent != null) intentList.add(intent);

        intent = context.getPackageManager().getLaunchIntentForPackage(SMARTISAN_SECURITY_CENTER_APP_PACKAGE_NAME);
        if (intent != null) intentList.add(intent);

        return intentList;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * Get Xiaomi app-specific permission settings intent.
     */
    @NonNull
    public static Intent getXiaoMiApplicationPermissionPageIntent(Context context) {
        return new Intent("miui.intent.action.APP_PERM_EDITOR")
                .putExtra("extra_pkgname", context.getPackageName());
    }

    /**
     * Get Smartisan Security Center permission settings intents.
     */
    @NonNull
    public static List<Intent> getSmartisanPermissionPageIntent() {
        List<Intent> intentList = new ArrayList<>(2);

        // Smartisan -> Permission overview
        intentList.add(new Intent(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".action.PACKAGE_OVERVIEW"));

        // Smartisan -> Packages overview activity
        Intent intent = new Intent();
        intent.setClassName(SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME, SMARTISAN_SECURITY_COMPONENT_APP_PACKAGE_NAME + ".PackagesOverview");
        intentList.add(intent);

        return intentList;
    }

    /* ---------------------------------------------------------------------------------------- */

    /**
     * Get a generic/common permission settings page.
     */
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

    /**
     * Get the "App details" settings intent.
     */
    @NonNull
    public static Intent getApplicationDetailsSettingsIntent(@NonNull Context context, @Nullable IPermission... permissions) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (permissions != null && permissions.length > 0 && DeviceOs.isColorOs()) {
            // OPPO blocked permission redirection optimization: https://open.oppomobile.com/new/developmentDoc/info?id=12983
            Bundle bundle = new Bundle();
            List<String> permissionList = PermissionUtils.convertPermissionList(permissions);
            // List of blocked native permission names
            bundle.putStringArrayList("permissionList", permissionList instanceof ArrayList ?
                    (ArrayList<String>) permissionList : new ArrayList<>(permissionList));
            intent.putExtras(bundle);
            // Pass optimization flag
            intent.putExtra("isGetPermission", true);
        }
        return intent;
    }

    /**
     * Get "Manage all apps" settings intent.
     */
    @NonNull
    public static Intent getManageApplicationSettingsIntent() {
        return new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
    }

    /**
     * Get "All applications" settings intent.
     */
    @NonNull
    public static Intent getApplicationSettingsIntent() {
        return new Intent(Settings.ACTION_APPLICATION_SETTINGS);
    }

    /**
     * Get system settings intent.
     */
    @NonNull
    public static Intent getAndroidSettingsIntent() {
        return new Intent(Settings.ACTION_SETTINGS);
    }
}
