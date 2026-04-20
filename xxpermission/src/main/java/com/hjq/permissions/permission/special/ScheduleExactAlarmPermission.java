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
 * Exact alarm permission class.
 */
public final class ScheduleExactAlarmPermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // Although this SystemService should never be null, keep the check for defensive programming.
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

            // If adding the package name data prevents navigation, remove the package name data.
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
        // checkmanifest permissionhas nodeclare, check, permission , need to check
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

        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_13 &&
            findPermissionInfoByList(permissionInfoList, useExactAlarmPermissionName) != null) {
            // If the current the project targets Android 13 , declared in the manifest file USE_EXACT_ALARM permission, SCHEDULE_EXACT_ALARM permissionin the manifest file this meansdeclare
            // <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" android:maxSdkVersion="32" />
            // relateddocumentation link: https://developer.android.google.cn/reference/android/Manifest.permission#USE_EXACT_ALARM
            // If app GooglePlay, need to add USE_EXACT_ALARM permission, becausenotcalendar, , appadd USE_EXACT_ALARM permission through GooglePlay
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName(), PermissionVersion.ANDROID_12_L);
            return;
        }

        checkPermissionRegistrationStatus(permissionInfoList, getPermissionName());
    }
}