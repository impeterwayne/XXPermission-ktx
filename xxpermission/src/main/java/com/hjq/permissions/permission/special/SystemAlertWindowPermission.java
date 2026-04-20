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

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // Although system alert window permission was introduced in Android 6.0, some vendor systems added it before Android 6.0, and the framework already handles that compatibility
        // So to support lower Android versions, this treats the introduction version of the system alert window permission as API 17, which is also the framework minSdkVersion requirement
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

        // vivo x7 Plus(Android 5.1) and OPPO A53 (Android 5.1 ColorOS 2.1) check
        // debug not vivo and oppo OP_SYSTEM_ALERT_WINDOW
        // vivo and oppo system alert window , no
        return checkOpPermission(context, OP_SYSTEM_ALERT_WINDOW_FIELD_NAME, OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE, true);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(7);
        Intent intent;

        if (PermissionVersion.isAndroid6()) {
            // If the current system is HyperOS, do not navigate to the MIUI permission settings page, becausealso " permission"entry findssystem alert window permission settingsoption
            // this means also directlynavigate allapp system alert window permission settingslist,
            // Related GitHub issue:https://github.com/getActivity/XXPermissions/issues/342
            if (PermissionVersion.isAndroid11() && !DeviceOs.isHyperOs() &&
                        (DeviceOs.isMiui() && DeviceOs.isMiuiOptimization())) {
                // because Android 11 after it version directlynavigate permission settings page, navigate system alert window permissionapp list, , here
                // MIUI , will navigate issue, vendor , navigate
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            } else if (DeviceOs.isFlyme()) {
                // Meizu phone directlynavigate permission settings page, this means need to app list finds app authorization
                intent = PermissionSettingPage.getMeiZuApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }

            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            // Android 11 package namenavigate no , documentation :
            // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intentList.add(intent);

        } else {

            // Please note the following, here need tocheck HarmonyOS, because HarmonyOS 2.0 codecheck API 29(Android 10)will directly logic, will below
            if (DeviceOs.isEmui()) {
                // EMUI : http://www.360doc.com/content/19/1017/10/9113704_867381705.shtml
                // android Huaweiversion , HuaweiEMUI : https://blog.csdn.net/weixin_39959369/article/details/117351161

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

                // MIUI , here logic
                // Xiaomi phone throughapp details page system alert window permission( will )
                if (DeviceOs.isMiuiOptimization()) {
                    intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                    intentList.add(intent);
                }

                // Xiaomi phone manager home page
                intentList.addAll(PermissionSettingPage.getXiaoMiMobileManagerAppIntent(context));

            }  else if (DeviceOs.isFlyme()) {

                intent = PermissionSettingPage.getMeiZuApplicationPermissionPageIntent(context);
                intentList.add(intent);

            } else if (DeviceOs.isColorOs()) {
                // com.color.safecenter earlier oppo security center package name, com.oppo.safe oppo after it security center package name
                // ColorOs 2.1 , Android 4.4 also com.color.safecenter, Android 5.0 com.oppo.safe

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

                // cannot directly system alert windowpage, page(permission page)also , so
                // OPPO R7 Plus(Android 5.0, ColorOs 2.1), OPPO R7s(Android 4.4, ColorOs 2.1)
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

                // vivo x7 Plus(Android 5.1) navigate , page
                // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");

                // Vivo phone manager home page
                intentList.addAll(PermissionSettingPage.getVivoMobileManagerAppIntent(context));

            } else if (DeviceOs.isOneUi()) {
                intent = PermissionSettingPage.getOneUiPermissionPageIntent(context);
                intentList.add(intent);
            } else if (DeviceOs.isSmartisanOs() && !PermissionVersion.isAndroid5_1()) {
                // , Smartisan phone 5.1 above phone directlythroughdirectlynavigate appdetails system alert window permission, 4.4 below phone , need tonavigate security center
                intentList.addAll(PermissionSettingPage.getSmartisanPermissionPageIntent());
                intentList.addAll(PermissionSettingPage.getSmartisanSecurityCenterAppIntent(context));
            }

            // 360 phone 360 N4, Android version 6.0 , so need tonavigate page system alert window permission
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