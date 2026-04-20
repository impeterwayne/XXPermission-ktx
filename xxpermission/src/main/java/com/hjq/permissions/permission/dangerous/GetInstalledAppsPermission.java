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
import android.provider.Settings.Secure;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * Read installed apps permission class.
 */
public final class GetInstalledAppsPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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

    @NonNull
    @Override
    public PermissionChannel getPermissionChannel(@NonNull Context context) {
        if (PermissionVersion.isAndroid6()) {
            if (isSupportRequestPermissionBySystem(context)) {
                return PermissionChannel.REQUEST_PERMISSIONS;
            } else if (isSupportRequestPermissionByOneUi(context)) {
                // : read the app list Samsungdevices permission, requestPermissions, it means that startActivity request ？
                // because Samsung OneUI , permission permission, system requestPermissions request permission,
                // Permission namenot com.android.permission.GET_INSTALLED_APPS it means that com.samsung.android.permission.GET_APP_LIST,
                // here issue, read installed apps permissionhas , , OneUI ,
                // requestPermissions passed in Permission name, will issue, request？
                // here will , codecheck , If the current device OneUI Samsung , otherwise ？
                // , there is a problem, this meanscausesPermissionName valueget return runtime , ,
                // frameworkcheck whether permission, PermissionName check, If runtime ,
                // cannot permission , because OneUI check whether permissionthrough Context objectcheck,
                // List or Map , check objectwhether , object equals check, equals no Context ,
                // switch toruntimeget , need to equals Context object, , because equals Object ,
                // also has issue , caller throughpermission objectcheck permission , will PermissionName checkpermission not ,
                // switch toruntimeget , causescaller , check , nocheck OneUI , not ？
                // issue has , introduced PermissionId , checkpermission PermissionName ,
                // this meanscausesframework , issue OneUI , , also startActivity request permission,
                // When best solution: Samsung Permission name request permission, issue already , .
                return PermissionChannel.START_ACTIVITY;
            } else if (isSupportRequestPermissionByFlyme()) {
                return PermissionChannel.START_ACTIVITY;
            }
        }
        return PermissionChannel.START_ACTIVITY;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        if (getPermissionChannel(context) == PermissionChannel.REQUEST_PERMISSIONS) {
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
        // get return , not request , preconditions
        boolean superMethodSupportRequestPermission = super.isSupportRequestPermission(context);
        if (superMethodSupportRequestPermission) {
            if (PermissionVersion.isAndroid6() && (isSupportRequestPermissionBySystem(context) || isSupportRequestPermissionByOneUi(context))) {
                // request
                return true;
            }

            if (PermissionVersion.isAndroid4_4() && DeviceOs.isMiui() && isSupportRequestPermissionByMiui()) {
                // through MIUI not
                return DeviceOs.isMiuiOptimization();
            }
        }
        return superMethodSupportRequestPermission;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid6()) {
            if (isSupportRequestPermissionBySystem(context)) {
                return checkSelfPermission(context, getPermissionName());
            } else if (isSupportRequestPermissionByOneUi(context)) {
                return checkSelfPermission(context, ONE_UI_GET_APP_LIST_PERMISSION_NAME);
            } else if (isSupportRequestPermissionByFlyme()) {
                // Flyme 10.5.0.1 Android 13 com.android.permissioncontroller app
                // com.meizu.safe.newpermission.data FlymePermission queryState
                int permissionState = Secure.getInt(context.getContentResolver(), getOpsNameByFlyme(context), -1);
                // default permission state: -1( earliercodepassed in default )
                // permission state: 4
                // permission state: 6
                // denied permission state: 3
                return permissionState == 4 || permissionState == 6;
            }
        }

        if (PermissionVersion.isAndroid4_4() && isSupportRequestPermissionByMiui()) {
            if (!DeviceOs.isMiuiOptimization()) {
                // If the current no MIUI , directlyreturn true, already authorization , because case
                // navigate MIUI permission settings page, user authorization , codecheckpermissionalso not granted state
                // so no MIUI case , calleralready granted , caller guideusernavigate permission settings page
                return true;
            }
            // , OP_GET_INSTALLED_APPS Xiaomi Android 6.0 , Android 5.0 MIUI no read the app list permission
            return checkOpPermission(context, MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME, MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE, true);
        }

        // If it does not support requests, directlyreturn true( has permission), will , app list
        return true;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid6()) {
            if (isSupportRequestPermissionBySystem(activity)) {
                return isDoNotAskAgainPermissionByStandardVersion(activity);
            } else if (isSupportRequestPermissionByOneUi(activity)) {
                return false;
            }
        }

        if (PermissionVersion.isAndroid4_4() && DeviceOs.isMiui() && isSupportRequestPermissionByMiui()) {
            if (!DeviceOs.isMiuiOptimization()) {
                return false;
            }
            // If noauthorization case return true permanentlydenied, this means after it check, caller navigate Xiaomi permission settings page
            return !isGrantedPermission(activity);
        }

        // If it does not support requests, directlyreturn false( nopermanentlydenied)
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
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // Samsung devices , need toadd to the manifest file com.samsung.android.permission.GET_APP_LIST request succeeds successread app list
        // PermissionManifestInfo oneUiGetAppListPermission = findPermissionInfoByList(permissionInfoList, ONE_UI_GET_APP_LIST_PERMISSION_NAME);
        // checkPermissionRegistrationStatus(oneUiGetAppListPermission, ONE_UI_GET_APP_LIST_PERMISSION_NAME, PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION);

        // current targetSdk must Android 11, otherwise check
        if (PermissionVersion.getTargetSdkVersion(activity) < PermissionVersion.ANDROID_11) {
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

        // targetSdk >= 30 , requestread installed apps permissionneed to
        // 1. read all apps: declare in the manifest file QUERY_ALL_PACKAGES permission
        // 2. read some specific apps: addneed toreadapp package name <queries>
        // above need to , otherwise request GET_INSTALLED_APPS permission succeeds , list
        // case , If GooglePlay , directlydeclare QUERY_ALL_PACKAGES permission , need to
        // Github issue: https://github.com/getActivity/XXPermissions/issues/359
        throw new IllegalStateException("Please register permissions in the AndroidManifest.xml file " +
            "<uses-permission android:name=\"" + queryAllPackagesPermissionName + "\" />, "
            + "or add the app package name to the <queries> tag in the AndroidManifest.xml file");
    }

    /**
     * check whetherapp listpermission
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
            // no permission will : android.content.pm.PackageManager$NameNotFoundException: com.android.permission.GET_INSTALLED_APPS
            // e.printStackTrace();
        }

        try {
            // Guideline for mobile terminal app list permission implementation: http://www.taf.org.cn/upload/AssociationStandard/TTAF%20108-2022%20%E7%A7%BB%E5%8A%A8%E7%BB%88%E7%AB%AF%E5%BA%94%E7%94%A8%E8%BD%AF%E4%BB%B6%E5%88%97%E8%A1%A8%E6%9D%83%E9%99%90%E5%AE%9E%E6%96%BD%E6%8C%87%E5%8D%97.pdf
            // , because device model, Magic UI has , vendor( Huawei HarmonyOS) no
            // checkpermission not permission , has phonevendor below , so , ,
            return Settings.Secure.getInt(context.getContentResolver(), "oem_installed_apps_runtime_permission_enable") == 1;
        } catch (Settings.SettingNotFoundException e) {
            // no systemattribute will : android.provider.Settings$SettingNotFoundException: oem_installed_apps_runtime_permission_enable
            // e.printStackTrace();
        }
        return false;
    }

    /**
     * checkcurrent MIUI versionwhether requestread installed apps permission
     */
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    private static boolean isSupportRequestPermissionByMiui() {
        if (!DeviceOs.isMiui()) {
            return false;
        }
        return isExistOpPermission(MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME);
    }

    /**
     * checkcurrent OneUI versionwhether requestread installed apps permission
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
            // no permission will : android.content.pm.PackageManager$NameNotFoundException: com.samsung.android.permission.GET_APP_LIST
            // OneUI 5.1 no permission, OneUI 5.1.1 has permission,
            // so OneUI 5.1.1 version request permission
            e.printStackTrace();
        }
        return false;
    }

    /**
     * checkcurrent Flyme versionwhether requestread installed apps permission
     */
    @RequiresApi(PermissionVersion.ANDROID_6)
    @SuppressWarnings("deprecation")
    private static boolean isSupportRequestPermissionByFlyme() {
        if (!DeviceOs.isFlyme()) {
            return false;
        }
        // Flyme - , : https://zh.wikipedia.org/wiki/Flyme
        // read installed apps permission, will : http://www.360doc.com/content/20/0626/16/29478554_920626341.shtml
        // Flyme 2020-06-26 has read installed apps permission entry , Flyme version , Flyme 9 above permission
        return DeviceOs.getOsBigVersionCode() >= 9;
    }

    /**
     * get Flyme system read installed apps permission Ops
     */
    private String getOpsNameByFlyme(@NonNull Context context) {
        // Flyme 10.5.0.1 Android 13 com.android.permissioncontroller app
        // com.meizu.safe.newpermission.data FlymePermissionOpsName
        int flymePermissionId = 56;
        return context.getPackageName() + "_op_" + flymePermissionId;
    }
}