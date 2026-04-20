package com.hjq.permissions.permission.special;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * Ignore battery optimizations permission class.
 */
public final class RequestIgnoreBatteryOptimizationsPermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

    public static final Creator<RequestIgnoreBatteryOptimizationsPermission> CREATOR = new Creator<RequestIgnoreBatteryOptimizationsPermission>() {

        @Override
        public RequestIgnoreBatteryOptimizationsPermission createFromParcel(Parcel source) {
            return new RequestIgnoreBatteryOptimizationsPermission(source);
        }

        @Override
        public RequestIgnoreBatteryOptimizationsPermission[] newArray(int size) {
            return new RequestIgnoreBatteryOptimizationsPermission[size];
        }
    };

    public RequestIgnoreBatteryOptimizationsPermission() {
        // default implementation ignored
    }

    private RequestIgnoreBatteryOptimizationsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @SuppressLint("BatteryLife")
    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        // because Android 10 , special permission pageXiaomialso
        // Android 11 later , permissionpage Xiaomi page
        if (PermissionVersion.isAndroid11() && (DeviceOs.isHyperOs() || DeviceOs.isMiui())) {
            return PermissionPageType.OPAQUE_ACTIVITY;
        }
        // On some ColorOS devices this permission opens an opaque activity. Test results are listed below.
        // ColorOS 16.0.0(Beta)Android 15 OPPO Find X8: transparent Activity
        // ColorOS 16.0.0(Beta)Android 15 13: transparent Activity
        // ColorOS 15.0.2 Android 15 OPPO Find X8s+: opaque Activity
        // ColorOS 15.0.1 Android 15 2 Pro: opaque Activity
        // ColorOS 15.0.0 Android 15 OPPO Pad2: opaque Activity
        // ColorOS 15.0.0 Android 15 12: opaque Activity
        // ColorOS 14.1.0 Android 14 OPPO Find X7: transparent Activity
        // ColorOS 14.0.1 Android 14 OPPO A3 Pro 5G: transparent Activity
        // ColorOS 14.0.0 Android 14 Reno8 Pro: transparent Activity
        if (DeviceOs.isColorOs() && DeviceOs.getOsBigVersionCode() == 15) {
            return PermissionPageType.OPAQUE_ACTIVITY;
        }
        if (PermissionVersion.isAndroid6() && !isGrantedPermission(context)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(getPackageNameUri(context));
            if (PermissionUtils.areActivityIntent(context, intent)) {
                return PermissionPageType.TRANSPARENT_ACTIVITY;
            }
        }
        return PermissionPageType.OPAQUE_ACTIVITY;
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
        PowerManager powerManager = context.getSystemService(PowerManager.class);
        // Although this SystemService should never be null, keep the check for defensive programming.
        if (powerManager == null) {
            return false;
        }
        return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    @SuppressLint("BatteryLife")
    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(7);

        Intent requestIgnoreBatteryOptimizationsIntent = null;
        if (PermissionVersion.isAndroid6()) {
            requestIgnoreBatteryOptimizationsIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            requestIgnoreBatteryOptimizationsIntent.setData(getPackageNameUri(context));
            // , If already authorization case , cannot navigate Intent , otherwisecauses Intent, navigate ,
            // permission settings pagewill finish, causescode navigate userno has navigatepermission settings page issue
            // , has HyperOS authorization navigate , MIUI , Android , sohere HyperOS
            if (isGrantedPermission(context, skipRequest) && !DeviceOs.isHyperOs()) {
                requestIgnoreBatteryOptimizationsIntent = null;
            }
        }

        Intent advancedPowerUsageDetailIntent = null;
        if (PermissionVersion.isAndroid12()) {
            // app casedetails : Settings.ACTION_VIEW_ADVANCED_POWER_USAGE_DETAIL
            // ACTION_VIEW_ADVANCED_POWER_USAGE_DETAIL Android 10
            // , Android 10 navigate , has Android 12 navigate
            advancedPowerUsageDetailIntent = new Intent("android.settings.VIEW_ADVANCED_POWER_USAGE_DETAIL");
            advancedPowerUsageDetailIntent.setData(getPackageNameUri(context));
        }

        Intent ignoreBatteryOptimizationSettingsIntent = null;
        if (PermissionVersion.isAndroid6()) {
            ignoreBatteryOptimizationSettingsIntent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }

        // because Android 10 , special permission pageXiaomialso
        // Android 11 later , permissionpage Xiaomi page
        if (skipRequest && !(PermissionVersion.isAndroid11() && (DeviceOs.isHyperOs() || DeviceOs.isMiui()))) {
            if (advancedPowerUsageDetailIntent != null) {
                intentList.add(advancedPowerUsageDetailIntent);
            }
            if (ignoreBatteryOptimizationSettingsIntent != null) {
                intentList.add(ignoreBatteryOptimizationSettingsIntent);
            }
            if (requestIgnoreBatteryOptimizationsIntent != null) {
                intentList.add(requestIgnoreBatteryOptimizationsIntent);
            }
        } else {
            if (requestIgnoreBatteryOptimizationsIntent != null) {
                intentList.add(requestIgnoreBatteryOptimizationsIntent);
            }
            if (advancedPowerUsageDetailIntent != null) {
                intentList.add(advancedPowerUsageDetailIntent);
            }
            if (ignoreBatteryOptimizationSettingsIntent != null) {
                intentList.add(ignoreBatteryOptimizationSettingsIntent);
            }
        }

        Intent intent;
        // , , MIUI and HyperOS app details pagesettings permission:
        // 1. MIUI app details page -> battery saver policy
        // 2. HyperOS app details page -> battery usage
        if (DeviceOs.isHyperOs() || DeviceOs.isMiui()) {
            intent = getApplicationDetailsSettingIntent(context);
            intentList.add(intent);

            intent = getManageApplicationSettingIntent();
            intentList.add(intent);

            intent = getApplicationSettingIntent();
            intentList.add(intent);
        }

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    public int getResultWaitTime(@NonNull Context context) {
        if (!isSupportRequestPermission(context)) {
            return 0;
        }

        // Xiaomi phonedefaultwaits
        final int xiaomiPhoneDefaultWaitTime = 1000;
        if (DeviceOs.isHyperOs()) {
            // 1. HyperOS 2.0.112.0, Android 15, Xiaomi 14, 200 noissue
            // 2. HyperOS 2.0.8.0, Android 15, Xiaomi 12S Pro, 200 noissue
            // 3. HyperOS 2.0.5.0, Android 15, K60, 200 noissue
            // 4. HyperOS 2.0.1.0, Android 15, 14R, 200 noissue
            // 5. HyperOS 2.0.4.0, Android 14, Xiaomi 5, 200 noissue
            // 6. HyperOS 2.0.1.0, Android 14, Xiaomi 12 Pro , 200 noissue
            // 7. HyperOS 1.0.7.0, Android 14, Note 14, need to 1000
            // Approximate : HyperOS 2.0 above systemnoissue, HyperOS 2.0 Android versionhas Android 15 and Android 14 ,
            // Android 14 HyperOS has 1.0 , nofinds Android 14 HyperOS 2.0 version,
            // so issue HyperOS 2.0 , HyperOS 2.0 UI ( UI has )
            // result , , otherwisewill , Bug
            // Android 15 HyperOS 2.0 version 200 noissue, Android 14 version HyperOS 1.0 also has has issue
            if (PermissionVersion.isAndroid15()) {
                return super.getResultWaitTime(context);
            }

            if (PermissionVersion.isAndroid14()) {
                int osBigVersionCode = DeviceOs.getOsBigVersionCode();
                // Ifget version or version 2, returnXiaomidevice modeldefault waits
                if (osBigVersionCode < 2) {
                    return xiaomiPhoneDefaultWaitTime;
                }
                return super.getResultWaitTime(context);
            }

            return xiaomiPhoneDefaultWaitTime;
        }

        if (DeviceOs.isMiui() && PermissionVersion.isAndroid11()) {
            // , Xiaomi Android 11 above version, request permissionneed to 1000 check ( 800 also )
            // because Android 10 , special permission pageXiaomialso
            // Android 11 later , permissionpage Xiaomi page
            // and vivo no issue, so Bug Xiaomi has
            return xiaomiPhoneDefaultWaitTime;
        }

        return super.getResultWaitTime(context);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Indicates that this permission must be declared statically in AndroidManifest.xml.
        return true;
    }
}
