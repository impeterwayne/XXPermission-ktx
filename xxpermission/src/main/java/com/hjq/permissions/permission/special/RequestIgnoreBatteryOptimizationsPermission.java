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
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Permission class for requesting "Ignore Battery Optimizations"
 */
public final class RequestIgnoreBatteryOptimizationsPermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
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
        // On Android 10, Xiaomi devices still used Google's native page for this special permission
        // However, starting from Android 11, Xiaomi replaced it with their own customized page
        if (PermissionVersion.isAndroid11() && (DeviceOs.isHyperOs() || DeviceOs.isMiui())) {
            return PermissionPageType.OPAQUE_ACTIVITY;
        }
        // On OPPO devices with Android 15 and above, this permission page is an opaque Activity
        if (DeviceOs.isColorOs() && PermissionVersion.isAndroid15()) {
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
        // Although this SystemService is never null, still apply defensive programming just in case
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
            // Based on testing:
            // - If already granted, this intent cannot be opened again.
            //   Otherwise, it opens but immediately finishes, giving the illusion that no page was shown.
            // - On HyperOS, even if granted, it can still open; but on MIUI and stock Android, it cannot.
            // Therefore, exclude HyperOS from this check.
            if (isGrantedPermission(context, skipRequest) && !DeviceOs.isHyperOs()) {
                requestIgnoreBatteryOptimizationsIntent = null;
            }
        }

        Intent advancedPowerUsageDetailIntent = null;
        if (PermissionVersion.isAndroid12()) {
            // Battery usage detail page: Settings.ACTION_VIEW_ADVANCED_POWER_USAGE_DETAIL
            // Although ACTION_VIEW_ADVANCED_POWER_USAGE_DETAIL was added in Android 10,
            // testing shows it only works starting from Android 12
            advancedPowerUsageDetailIntent = new Intent("android.settings.VIEW_ADVANCED_POWER_USAGE_DETAIL");
            advancedPowerUsageDetailIntent.setData(getPackageNameUri(context));
        }

        Intent ignoreBatteryOptimizationSettingsIntent = null;
        if (PermissionVersion.isAndroid6()) {
            ignoreBatteryOptimizationSettingsIntent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }

        // On Android 10, Xiaomi used the Google native page,
        // but starting from Android 11, Xiaomi replaced it with their own customized page
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
        // Based on testing, MIUI and HyperOS support setting this permission on the app details page:
        // 1. MIUI: App Details -> Power Saving Strategy
        // 2. HyperOS: App Details -> Power Consumption
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

        // Default wait time for Xiaomi devices
        final int xiaomiPhoneDefaultWaitTime = 1000;
        if (DeviceOs.isHyperOs()) {
            // Tested cases:
            // 1. HyperOS 2.0.112.0, Android 15, Xiaomi 14 → 200 ms is fine
            // 2. HyperOS 2.0.8.0, Android 15, Xiaomi 12S Pro → 200 ms is fine
            // 3. HyperOS 2.0.5.0, Android 15, Redmi K60 → 200 ms is fine
            // 4. HyperOS 2.0.1.0, Android 15, Redmi 14R → 200 ms is fine
            // 5. HyperOS 2.0.4.0, Android 14, Xiaomi Pad 5 → 200 ms is fine
            // 6. HyperOS 2.0.1.0, Android 14, Xiaomi 12 Pro Dimensity Edition → 200 ms is fine
            // 7. HyperOS 1.0.7.0, Android 14, Redmi Note 14 → requires 1000 ms
            //
            // Conclusion:
            // - HyperOS 2.0 and above has no issue (UI was significantly redesigned).
            // - HyperOS 1.0 on Android 14 still has the problem.
            if (PermissionVersion.isAndroid15()) {
                return super.getResultWaitTime(context);
            }

            if (PermissionVersion.isAndroid14()) {
                int osBigVersionCode = DeviceOs.getOsBigVersionCode();
                // If the big version number is not available or < 2, fall back to Xiaomi default wait time
                if (osBigVersionCode < 2) {
                    return xiaomiPhoneDefaultWaitTime;
                }
                return super.getResultWaitTime(context);
            }

            return xiaomiPhoneDefaultWaitTime;
        }

        if (DeviceOs.isMiui() && PermissionVersion.isAndroid11()) {
            // On Xiaomi devices with Android 11+, requesting this permission requires 1000 ms to detect (800 ms is not enough).
            // On Android 10, Xiaomi still used Google’s native page.
            // On Android 11+, Xiaomi replaced it with their own customized page.
            // Tests on stock Android emulator and Vivo cloud show no issue,
            // so this bug is confirmed to be Xiaomi-specific.
            return xiaomiPhoneDefaultWaitTime;
        }

        return super.getResultWaitTime(context);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Indicates that this permission must be statically registered in the AndroidManifest.xml file
        return true;
    }
}
