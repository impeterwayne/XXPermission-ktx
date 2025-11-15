package com.hjq.permissions.permission.dangerous;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Get installed apps permission class
 */
public final class GetInstalledAppsPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only and should not be referenced externally.
     * If you need the permission name string, please use the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.GET_INSTALLED_APPS;

    private static final String MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME = "OP_GET_INSTALLED_APPS";
    private static final int MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE = 10022;

    private static final String ONE_UI_GET_APP_LIST_PERMISSION_NAME = "com.samsung.android.permission.GET_APP_LIST";

    public static final Parcelable.Creator<GetInstalledAppsPermission> CREATOR = new Parcelable.Creator<GetInstalledAppsPermission>() {

        @Override
        public GetInstalledAppsPermission createFromParcel(Parcel source) {
            return new GetInstalledAppsPermission(source);
        }

        @Override
        public GetInstalledAppsPermission[] newArray(int size) {
            return new GetInstalledAppsPermission[size];
        }
    };

    public GetInstalledAppsPermission() {
        // default implementation ignored
    }

    private GetInstalledAppsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getRequestPermissionName(Context context) {
        if (PermissionVersion.isAndroid6() &&
                !isSupportRequestPermissionBySystem(context) &&
                isSupportRequestPermissionByOneUi(context)) {
            return ONE_UI_GET_APP_LIST_PERMISSION_NAME;
        }
        return super.getRequestPermissionName(context);
    }

    @NonNull
    @Override
    public PermissionChannel getPermissionChannel(@NonNull Context context) {
        if (PermissionVersion.isAndroid6() && (isSupportRequestPermissionBySystem(context) || isSupportRequestPermissionByOneUi(context))) {
            return PermissionChannel.REQUEST_PERMISSIONS;
        }
        return PermissionChannel.START_ACTIVITY_FOR_RESULT;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        if (this.getPermissionChannel(context) == PermissionChannel.REQUEST_PERMISSIONS) {
            return PermissionPageType.TRANSPARENT_ACTIVITY;
        }
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_4_2;
    }

    @Override
    public boolean isSupportRequestPermission(@NonNull Context context) {
        // Get parent method result to check whether requesting is supported, this is a prerequisite
        boolean superMethodSupportRequestPermission = super.isSupportRequestPermission(context);
        if (superMethodSupportRequestPermission) {
            if (PermissionVersion.isAndroid6() && (isSupportRequestPermissionBySystem(context) || isSupportRequestPermissionByOneUi(context))) {
                // Supported by system or OneUI
                return true;
            }

            if (PermissionVersion.isAndroid4_4() && DeviceOs.isMiui() && isSupportRequestPermissionByMiui()) {
                // Use MIUI optimization toggle to decide support
                return DeviceOs.isMiuiOptimization();
            }
        }
        return superMethodSupportRequestPermission;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid6() && (isSupportRequestPermissionBySystem(context) || isSupportRequestPermissionByOneUi(context))) {
            return checkSelfPermission(context, getRequestPermissionName(context));
        }

        if (PermissionVersion.isAndroid4_4() && isSupportRequestPermissionByMiui()) {
            if (!DeviceOs.isMiuiOptimization()) {
                // If MIUI optimization is not enabled, just return true.
                // Even if the user enables it in settings, code checks will still show not granted.
                // To avoid unnecessary redirects to MIUI settings, treat as granted.
                return true;
            }
            // Testing found OP_GET_INSTALLED_APPS introduced in MIUI on Android 6.0,
            // not present on Android 5.0 MIUI.
            return checkOpPermission(context, MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME, MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE, true);
        }

        // If not supported, return true (assume granted). App won’t crash, just won’t get third-party app list.
        return true;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid6() && (isSupportRequestPermissionBySystem(activity) || isSupportRequestPermissionByOneUi(activity))) {
            // If supported, check standard "don’t ask again"
            return isDoNotAskAgainPermissionByStandardVersion(activity);
        }

        if (PermissionVersion.isAndroid4_4() && DeviceOs.isMiui() && isSupportRequestPermissionByMiui()) {
            if (!DeviceOs.isMiuiOptimization()) {
                return false;
            }
            // Returning true here forces external caller to redirect user to MIUI settings
            return !isGrantedPermission(activity);
        }

        // If not supported, always false (no permanent denial)
        return false;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>();
        Intent intent;

        if ((DeviceOs.isHyperOsByChina() && DeviceOs.isHyperOsOptimization()) ||
                (DeviceOs.isMiuiByChina() && DeviceOs.isMiuiOptimization())) {
            intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
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
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // On Samsung devices, testing showed com.samsung.android.permission.GET_APP_LIST is not required in manifest.

        // TargetSdk must be >= Android 11, otherwise skip check
        if (PermissionVersion.getTargetVersion(activity) < PermissionVersion.ANDROID_11) {
            return;
        }

        String queryAllPackagesPermissionName;
        if (PermissionVersion.isAndroid11()) {
            queryAllPackagesPermissionName = permission.QUERY_ALL_PACKAGES;
        } else {
            queryAllPackagesPermissionName = "android.permission.QUERY_ALL_PACKAGES";
        }

        PermissionManifestInfo permissionInfo = findPermissionInfoByList(permissionInfoList, queryAllPackagesPermissionName);
        if (permissionInfo != null || !manifestInfo.queriesPackageList.isEmpty()) {
            return;
        }

        // For targetSdk >= 30, additional handling is required:
        // 1. Read all apps: register QUERY_ALL_PACKAGES in manifest.
        // 2. Read specific apps: add those package names inside <queries>.
        // Otherwise, even with GET_INSTALLED_APPS granted, third-party app list cannot be retrieved.
        // Note: Google Play may restrict QUERY_ALL_PACKAGES, so option 2 is sometimes required.
        throw new IllegalStateException("Please register permissions in the AndroidManifest.xml file " +
                "<uses-permission android:name=\"" + queryAllPackagesPermissionName + "\" />, "
                + "or add the app package name to the <queries> tag in the AndroidManifest.xml file");
    }

    /**
     * Check if system supports GET_INSTALLED_APPS permission
     */
    @SuppressWarnings("deprecation")
    @RequiresApi(PermissionVersion.ANDROID_6)
    private boolean isSupportRequestPermissionBySystem(Context context) {
        try {
            PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(getPermissionName(), 0);
            if (permissionInfo != null) {
                final int protectionLevel;
                if (PermissionVersion.isAndroid9()) {
                    protectionLevel = permissionInfo.getProtection();
                } else {
                    protectionLevel = (permissionInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE);
                }
                return protectionLevel == PermissionInfo.PROTECTION_DANGEROUS;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Thrown if permission does not exist
            e.printStackTrace();
        }

        try {
            // Industry guideline: http://www.taf.org.cn/upload/AssociationStandard/TTAF%20108-2022%20移动终端应用软件列表权限实施指南.pdf
            // Only Honor’s Magic UI follows this, others (including HarmonyOS) don’t.
            // Keeping both checks ensures wider compatibility.
            return Settings.Secure.getInt(context.getContentResolver(), "oem_installed_apps_runtime_permission_enable") == 1;
        } catch (Settings.SettingNotFoundException e) {
            // Thrown if property does not exist
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if current MIUI supports GET_INSTALLED_APPS
     */
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    private static boolean isSupportRequestPermissionByMiui() {
        if (!DeviceOs.isMiui()) {
            return false;
        }
        return isExistOpPermission(MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME);
    }

    /**
     * Check if current OneUI supports GET_APP_LIST
     */
    @RequiresApi(PermissionVersion.ANDROID_6)
    @SuppressWarnings("deprecation")
    private static boolean isSupportRequestPermissionByOneUi(@NonNull Context context) {
        if (!DeviceOs.isOneUi()) {
            return false;
        }
        try {
            PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(ONE_UI_GET_APP_LIST_PERMISSION_NAME, 0);
            if (permissionInfo != null) {
                final int protectionLevel;
                if (PermissionVersion.isAndroid9()) {
                    protectionLevel = permissionInfo.getProtection();
                } else {
                    protectionLevel = (permissionInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE);
                }
                return protectionLevel == PermissionInfo.PROTECTION_DANGEROUS;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // On OneUI 5.1, not present. On OneUI 5.1.1, permission exists.
            // Conclusion: support added in OneUI 5.1.1
            e.printStackTrace();
        }
        return false;
    }
}
