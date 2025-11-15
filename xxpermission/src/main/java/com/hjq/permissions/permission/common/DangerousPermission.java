package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.manager.AlreadyRequestPermissionsManager;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Base class for dangerous permissions
 */
public abstract class DangerousPermission extends BasePermission {

    protected DangerousPermission() {
        super();
    }

    protected DangerousPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public PermissionChannel getPermissionChannel(@NonNull Context context) {
        return PermissionChannel.REQUEST_PERMISSIONS;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        return PermissionPageType.TRANSPARENT_ACTIVITY;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        // Check whether the permission is running on an older system
        // (permission’s introduced version > current system version)
        if (getFromAndroidVersion(context) > PermissionVersion.getCurrentVersion()) {
            return isGrantedPermissionByLowVersion(context, skipRequest);
        }
        return isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    /**
     * Determine whether the permission is granted on a standard-version system
     */
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid6()) {
            return true;
        }
        return checkSelfPermission(context, getRequestPermissionName(context));
    }

    /**
     * Determine whether the permission is granted on a low-version system
     */
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return true;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        // Check whether the permission is running on an older system
        // (permission’s introduced version > current system version)
        if (getFromAndroidVersion(activity) > PermissionVersion.getCurrentVersion()) {
            return isDoNotAskAgainPermissionByLowVersion(activity);
        }
        return isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    /**
     * On a standard-version system, determine whether the user has checked
     * “Don’t ask again” for this permission
     */
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (!PermissionVersion.isAndroid6()) {
            return false;
        }
        // Preconditions to decide whether the user checked “Don’t ask again”:
        // 1) The permission must have been requested at least once during this app run.
        // 2) The permission is currently denied.
        // With these two conditions we can infer whether “Don’t ask again” was checked.
        // Why so complicated? Because Google’s shouldShowRequestPermissionRationale is tricky:
        // even if the user did NOT check “Don’t ask again”, the method may still return false
        // when you call it BEFORE you’ve ever requested that permission during the current run.
        // In short, Google doesn’t want you to know whether “Don’t ask again” was checked
        // unless you’ve actually requested the permission in this run.
        //
        // The framework applies optimizations for cases where both foreground and background
        // permissions are requested together. If the user explicitly denies the foreground
        // permission, the framework won’t proceed to request the related background permission
        // (since it would inevitably fail). This can make shouldShowRequestPermissionRationale
        // less reliable.
        //
        // The flaw remains: if the app hasn’t requested a permission in this run, using
        // shouldShowRequestPermissionRationale to judge “Don’t ask again” is inaccurate.
        // Only after requesting at least once in this run can it be used reliably.
        //
        // You might ask: why not persist shouldShowRequestPermissionRationale to disk so it’s
        // “very good”? This has already been discussed here:
        // https://github.com/getActivity/XXPermissions/issues/154
        //
        // This is currently the best solution we can think of. If you have a better approach,
        // please file an issue—we’ll keep improving it.
        return AlreadyRequestPermissionsManager.isAlreadyRequestPermissions(this) &&
                !checkSelfPermission(activity, getRequestPermissionName(activity)) &&
                !shouldShowRequestPermissionRationale(activity, getRequestPermissionName(activity));
    }

    /**
     * On a low-version system, determine whether “Don’t ask again” was checked
     */
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return false;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(5);
        Intent intent;

        // If the current OEM system is HyperOS or MIUI and Xiaomi “system optimization” is enabled,
        // prefer jumping to Xiaomi’s dedicated app-permission settings page to improve the user experience.
        // Note: some users reported that MIUI Global cannot jump to Xiaomi’s dedicated permission page
        // for dangerous permissions.
        // GitHub: https://github.com/getActivity/XXPermissions/issues/398
        if (DeviceOs.isMiuiByChina() && DeviceOs.isMiuiOptimization()) {
            intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
            intentList.add(intent);
        } else if (DeviceOs.isHyperOsByChina() && DeviceOs.isHyperOsOptimization()) {
            String osVersionName = DeviceOs.getOsVersionName();
            // Filter versions 2.0.0.0 ~ 2.0.5.0. Tests on Xiaomi Cloud Test show
            // that jumping directly to Xiaomi’s dedicated app-permission page has issues
            // in this range. It appears fixed in 2.0.6.0. HyperOS 1.0 does not have this issue.
            // Likely cause: early HyperOS 2.0 builds had an incomplete permission page that
            // showed no dangerous-permission options—only “Other permissions”, and inside there
            // were just a few options like: Home screen shortcuts, SMS notifications, Lock screen display,
            // Background pop-ups, and Floating windows.
            if (!osVersionName.matches("^2\\.0\\.[0-5]\\.\\d+$")) {
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }
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
        // Dangerous permissions must be registered in the manifest by default.
        // This avoids forcing subclasses for special/custom permissions to override this method.
        return true;
    }
}
