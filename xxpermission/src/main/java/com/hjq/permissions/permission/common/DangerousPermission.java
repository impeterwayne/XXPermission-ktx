package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import androidx.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.manager.AlreadyRequestPermissionsManager;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for dangerous permissions.
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
        // checkpermission not older system (permission version > current system version)
        if (getFromAndroidVersion(context) > PermissionVersion.getSdkVersion()) {
            return isGrantedPermissionByLowVersion(context, skipRequest);
        }
        return isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    /**
     * On standard Android versions, checkpermission is granted
     */
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid6()) {
            return true;
        }
        return checkSelfPermission(context, getPermissionName());
    }

    /**
     * On lower Android versions, checkpermission is granted
     */
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return true;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        // checkpermission not older system (permission version > current system version)
        if (getFromAndroidVersion(activity) > PermissionVersion.getSdkVersion()) {
            return isDoNotAskAgainPermissionByLowVersion(activity);
        }
        return isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    /**
     * On standard Android versions, checkpermission was marked by the user as "Do not ask again"
     */
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (!PermissionVersion.isAndroid6()) {
            return false;
        }
        // Check whether the user selected Do not ask againoption preconditions
        // 1. the permission must have been requested during the current app session
        // 2. the permission must be ungranted
        // throughabove checkuser deniedwhether "Do not ask again" option, will ？
        // because Google shouldShowRequestPermissionRationale , user no "Do not ask again" optioncase ,
        // shouldShowRequestPermissionRationale return false, case state norequest permission ,
        // Google user not "Do not ask again" option, staterequest permission , otherwiseno .
        // framework issue , request foreground permission and background permission , user denied foreground permission ,
        // background permissionframework nocontinue request(becauserequest failure), causes shouldShowRequestPermissionRationale check issue.
        // this means has , app stateIfnorequest permission, directly shouldShowRequestPermissionRationale check has issue ,
        // has app staterequest permission shouldShowRequestPermissionRationale Check whether the user selected "Do not ask again" option.
        // will : not permanently store shouldShowRequestPermissionRationale stateto disk？would not that be even better than this approach？
        // issue already , here , : https://github.com/getActivity/XXPermissions/issues/154,
        // This is the best solution available so far, If also has , tell me through an issue, will issue.
        return AlreadyRequestPermissionsManager.isAlreadyRequestPermissions(this) &&
            !checkSelfPermission(activity, getPermissionName()) &&
            !shouldShowRequestPermissionRationale(activity, getPermissionName());
    }

    /**
     * On lower Android versions, checkpermission was marked by the user as "Do not ask again"
     */
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return false;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(5);
        Intent intent;

        // If the current vendorsystem HyperOS or MIUI , already Xiaomisystem
        // preferentiallynavigate Xiaomi has apppermission settings page, this means userauthorization
        // Please note the following, has MIUI cannot navigate Xiaomi has permission settings page settings permission
        // Github : https://github.com/getActivity/XXPermissions/issues/398
        if (DeviceOs.isMiuiByChina() && DeviceOs.isMiuiOptimization()) {
            intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
            intentList.add(intent);
        } else if (DeviceOs.isHyperOsByChina() && DeviceOs.isHyperOsOptimization()) {
            String osVersionName = DeviceOs.getOsVersionName();
            // hereneed to 2.0.0.0 ~ 2.0.5.0 scope version, because Xiaomi , scope versiondirectlynavigate Xiaomi has apppermission settings pagehas issue
            // 2.0.6.0 issue , HyperOS 1.0 version no issue, so issue 2.0.0.0 ~ 2.0.5.0 version
            // becauseXiaomi HyperOS 2.0 , Xiaomi has permission settings pagealso , navigate no permission option, has " permission" option
            // permission option also has permission: desktop shortcuts, notification SMS, lock-screen display, background page, system alert window
            if (!osVersionName.matches("^2\\.0\\.[0-5]\\.\\d+$")) {
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }
        } else if (DeviceOs.isFlyme()) {
            intent = PermissionSettingPage.getMeiZuApplicationPermissionPageIntent(context);
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
        // permissiondefaultyou need to declare, this means the caller defines a custom special permission, also
        return true;
    }
}