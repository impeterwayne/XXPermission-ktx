package com.hjq.permissions.permission.special;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Alarm permission class
 */
public final class ScheduleExactAlarmPermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.SCHEDULE_EXACT_ALARM;

    public static final Parcelable.Creator<ScheduleExactAlarmPermission> CREATOR = new Parcelable.Creator<ScheduleExactAlarmPermission>() {

        @Override
        public ScheduleExactAlarmPermission createFromParcel(Parcel source) {
            return new ScheduleExactAlarmPermission(source);
        }

        @Override
        public ScheduleExactAlarmPermission[] newArray(int size) {
            return new ScheduleExactAlarmPermission[size];
        }
    };

    public ScheduleExactAlarmPermission() {
        // default implementation ignored
    }

    private ScheduleExactAlarmPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid12()) {
            return true;
        }
        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        // Although this SystemService is never null, still apply defensive programming just in case
        if (alarmManager == null) {
            return false;
        }
        return alarmManager.canScheduleExactAlarms();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(6);
        Intent intent;

        if (PermissionVersion.isAndroid12()) {
            intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            // If adding the package name data prevents jumping, remove the package name data
            intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
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
        // Do not use the parent class’s method to check whether the manifest permission is registered.
        // This does not mean we skip checking — this permission is more complex and requires a custom check.
        return false;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        String useExactAlarmPermissionName;
        if (PermissionVersion.isAndroid13()) {
            useExactAlarmPermissionName = permission.USE_EXACT_ALARM;
        } else {
            useExactAlarmPermissionName = "android.permission.USE_EXACT_ALARM";
        }

        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13 &&
                findPermissionInfoByList(permissionInfoList, useExactAlarmPermissionName) != null) {
            // If the project already targets Android 13 and the manifest includes USE_EXACT_ALARM permission,
            // then SCHEDULE_EXACT_ALARM can be registered in the manifest like this:
            // <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" android:maxSdkVersion="32" />
            // Reference docs: https://developer.android.google.cn/reference/android/Manifest.permission#USE_EXACT_ALARM
            // ⚠️ Important: If your app is to be published on Google Play, you should be cautious about adding USE_EXACT_ALARM.
            // Unless your app is in categories like calendar, alarm clock, or timer,
            // it will be very difficult to pass Play Store review with USE_EXACT_ALARM included.
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName(), PermissionVersion.ANDROID_12_L);
            return;
        }

        checkPermissionRegistrationStatus(permissionInfoList, getPermissionName());
    }
}
