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
 * System alert window permission class.
 */
public final class SystemAlertWindowPermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, obtain it directly from {@link PermissionNames}. */
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

    // Xiaomi-specific op codes (not present in AOSP AppOpsManager)
    private static final String OP_XIAOMI_BACKGROUND_START_ACTIVITY_FIELD_NAME = "OP_BACKGROUND_START_ACTIVITY";
    private static final int OP_XIAOMI_BACKGROUND_START_ACTIVITY_DEFAULT_VALUE = 10021;

    private static final String OP_XIAOMI_SHOW_WHEN_LOCKED_FIELD_NAME = "OP_SHOW_WHEN_LOCKED";
    private static final int OP_XIAOMI_SHOW_WHEN_LOCKED_DEFAULT_VALUE = 10020;

    private static final String OP_XIAOMI_DISPLAY_POP_UP_WINDOW_FIELD_NAME = "OP_DISPLAY_POP_UP_WINDOW";
    private static final int OP_XIAOMI_DISPLAY_POP_UP_WINDOW_DEFAULT_VALUE = 10024;

    private boolean forceXiaomi;

    public SystemAlertWindowPermission() {
        // default implementation ignored
    }

    public SystemAlertWindowPermission(boolean forceXiaomi) {
        this.forceXiaomi = forceXiaomi;
    }

    private SystemAlertWindowPermission(Parcel in) {
        super(in);
        forceXiaomi = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (forceXiaomi ? 1 : 0));
    }

    /**
     * Whether the forceXiaomi mode is active on the current device.
     * Requires: forceXiaomi flag + MIUI + Android 11+ + (HyperOS or MIUI optimization).
     */
    private boolean isForceXiaomiActive() {
        return forceXiaomi
            && PermissionVersion.isAndroid11()
            && ( DeviceOs.isMiui() || DeviceOs.isHyperOs() || DeviceOs.isMiuiOptimization());
    }

    /**
     * Checks whether all Xiaomi-specific ops are granted.
     */
    private boolean isXiaomiOpsGranted(@NonNull Context context) {
        return checkOpPermission(context, OP_XIAOMI_BACKGROUND_START_ACTIVITY_FIELD_NAME, OP_XIAOMI_BACKGROUND_START_ACTIVITY_DEFAULT_VALUE, true)
            && checkOpPermission(context, OP_XIAOMI_SHOW_WHEN_LOCKED_FIELD_NAME, OP_XIAOMI_SHOW_WHEN_LOCKED_DEFAULT_VALUE, true)
            && checkOpPermission(context, OP_XIAOMI_DISPLAY_POP_UP_WINDOW_FIELD_NAME, OP_XIAOMI_DISPLAY_POP_UP_WINDOW_DEFAULT_VALUE, true);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        // Although system alert window permission was introduced in Android 6.0, some vendor systems added it before Android 6.0, and the framework already handles that compatibility
        // So to support lower Android versions, this treats the introduction version of the system alert window permission as API 17, which is also the framework minSdkVersion requirement
        return PermissionVersion.ANDROID_4_2;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid6()) {
            boolean granted = Settings.canDrawOverlays(context);
            // When forceXiaomi is active, additionally check Xiaomi-specific ops
            if (granted && isForceXiaomiActive()) {
                return isXiaomiOpsGranted(context);
            }
            return granted;
        }

        if (!PermissionVersion.isAndroid4_4()) {
            return true;
        }

        // vivo X7 Plus (Android 5.1) and OPPO A53 (Android 5.1 ColorOS 2.1) check:
        // OP_SYSTEM_ALERT_WINDOW checking may not be fully supported, so we fall back to checkOpPermission
        return checkOpPermission(context, OP_SYSTEM_ALERT_WINDOW_FIELD_NAME, OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE, true);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(7);
        Intent intent;

        if (PermissionVersion.isAndroid6()) {
            // When forceXiaomi is active, always navigate to Xiaomi permission page first
            if (isForceXiaomiActive()) {
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            } else if (PermissionVersion.isAndroid11() && !DeviceOs.isHyperOs() &&
                        (DeviceOs.isMiui() && DeviceOs.isMiuiOptimization())) {
                // Skip HyperOS — its "Permissions" page already has a system alert window option,
                // so we can navigate directly to the system overlay permission list.
                // On MIUI (non-HyperOS) with Android 11+, the standard overlay settings page
                // redirects to the full app list instead of the specific app, so we navigate
                // to the Xiaomi app permission page instead to avoid this issue.
                // Related GitHub issue: https://github.com/getActivity/XXPermissions/issues/342
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            } else if (DeviceOs.isFlyme()) {
                // On Meizu phones, directly navigating to the permission settings page requires the user to find the app in the list to grant authorization
                intent = PermissionSettingPage.getMeiZuApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }

            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            // Navigating with a package name is not supported on Android 11, see documentation:
            // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intentList.add(intent);

        } else {

            // Please note the following: we need to check for HarmonyOS here because HarmonyOS 2.0 targeting API 29 (Android 10) will bypass the logic below
            if (DeviceOs.isEmui()) {
                // EMUI: http://www.360doc.com/content/19/1017/10/9113704_867381705.shtml
                // Huawei Android versions, Huawei EMUI: https://blog.csdn.net/weixin_39959369/article/details/117351161

                Intent addViewMonitorActivityIntent = new Intent();
                // EMUI 3.1 (Huawei 7 Android 5.0, Huawei M2 Android 5.1, Huawei 5S Android 5.1)
                addViewMonitorActivityIntent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");

                Intent notificationManagementActivityIntent = new Intent();
                // EMUI 3.0 (Huawei 3S Android 4.4)
                notificationManagementActivityIntent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");

                // Get the vendor version number
                String osVersionName = DeviceOs.getOsVersionName();

                if (osVersionName.startsWith("3.0")) {
                    // 3.0, 3.0.1
                    intentList.add(notificationManagementActivityIntent);
                    intentList.add(addViewMonitorActivityIntent);
                } else {
                    // 3.1,
                    intentList.add(addViewMonitorActivityIntent);
                    intentList.add(notificationManagementActivityIntent);
                }

                // Huawei phone manager home page
                intentList.addAll(PermissionSettingPage.getHuaWeiMobileManagerAppIntent(context));

            } else if (DeviceOs.isMiui()) {

                // When forceXiaomi is active or MIUI optimization is on, navigate to Xiaomi permission page
                if (isForceXiaomiActive() || DeviceOs.isMiuiOptimization()) {
                    intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                    intentList.add(intent);
                }

                // Xiaomi phone manager home page
                intentList.addAll(PermissionSettingPage.getXiaoMiMobileManagerAppIntent(context));

            }  else if (DeviceOs.isFlyme()) {

                intent = PermissionSettingPage.getMeiZuApplicationPermissionPageIntent(context);
                intentList.add(intent);

            } else if (DeviceOs.isColorOs()) {
                // com.color.safecenter is the earlier OPPO security center package name, com.oppo.safe is the later OPPO security center package name
                // ColorOS 2.1, Android 4.4 uses com.color.safecenter, Android 5.0 uses com.oppo.safe

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.oppo.safe/.permission.floatwindow.FloatWindowListActivity (has extras) } from
                // ProcessRecord{839a7c5 10595:com.hjq.permissions.demo/u0a3781} (pid=10595, uid=13781) not exported from uid 1000
                // intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.color.safecenter/.permission.floatwindow.FloatWindowListActivity (has extras) } from
                // ProcessRecord{42b660b0 31279:com.hjq.permissions.demo/u0a204} (pid=31279, uid=10204) not exported from uid 1000
                // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.color.safecenter/.permission.PermissionAppAllPermissionActivity (has extras) } from
                // ProcessRecord{42c49dd8 1791:com.hjq.permissions.demo/u0a204} (pid=1791, uid=10204) not exported from uid 1000
                // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionAppAllPermissionActivity");

                // Cannot directly navigate to the system alert window page, nor the permission page, so
                // on OPPO R7 Plus (Android 5.0, ColorOS 2.1), OPPO R7s (Android 4.4, ColorOS 2.1)
                // com.oppo.safe.permission.PermissionTopActivity
                // com.oppo.safe..permission.PermissionAppListActivity
                // com.color.safecenter.permission.PermissionTopActivity

                intent = new Intent();
                intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionTopActivity");
                intentList.add(intent);

                intentList.addAll(PermissionSettingPage.getOppoSafeCenterAppIntent(context));

            } else if (DeviceOs.isFuntouchOs()) {
                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.iqoo.secure/.ui.phoneoptimize.FloatWindowManager (has extras) } from
                // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
                // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.iqoo.secure/.safeguard.PurviewTabActivity (has extras) } from
                // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
                // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.PurviewTabActivity");

                // vivo X7 Plus (Android 5.1) cannot navigate to the detailed permission page
                // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");

                // Vivo phone manager home page
                intentList.addAll(PermissionSettingPage.getVivoMobileManagerAppIntent(context));

            } else if (DeviceOs.isOneUi()) {
                intent = PermissionSettingPage.getOneUiPermissionPageIntent(context);
                intentList.add(intent);
            } else if (DeviceOs.isSmartisanOs() && !PermissionVersion.isAndroid5_1()) {
                // For Smartisan phones on Android 5.1 and above, we can directly navigate to the system alert window permission via app details, but for Android 4.4 and below, we need to navigate to the security center instead
                intentList.addAll(PermissionSettingPage.getSmartisanPermissionPageIntent());
                intentList.addAll(PermissionSettingPage.getSmartisanSecurityCenterAppIntent(context));
            }

            // The 360 N4 phone running Android 6.0 needs to navigate to the system alert window permission page
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