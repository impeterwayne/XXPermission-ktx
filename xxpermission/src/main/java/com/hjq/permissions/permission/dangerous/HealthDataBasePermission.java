package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.health.connect.HealthConnectManager;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Health Connect data permissions.
 */
public abstract class HealthDataBasePermission extends DangerousPermission {

    protected HealthDataBasePermission() {
        super();
    }

    protected HealthDataBasePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.HEALTH;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = super.getPermissionSettingIntents(context, skipRequest);

        Intent intent;
        // Android 14 ~ Android 15 devices, permission settings page nohealth datasharingpermission entry ,
        // soheredirectlynavigate health datasharing permission settings page, Android 16 directlynavigate app details page
        if (PermissionVersion.isAndroid14() && !PermissionVersion.isAndroid16()) {
            List<Intent> healthIntentList = new ArrayList<>(3);

            // ACTION_MANAGE_HEALTH_PERMISSIONS Intent Android 14 navigate, Android 15 navigatewill , nopermission navigate page
            // java.lang.SecurityException: Permission Denial: starting Intent { act=android.health.connect.action.MANAGE_HEALTH_PERMISSIONS xflg=0x4
            // cmp=com.google.android.healthconnect.controller/com.android.healthconnect.controller.PermissionControllerEntryPoint (has extras) } from
            // ProcessRecord{18b95b4 25796:com.hjq.permissions.demo/u0a222} (pid=25796, uid=10222) requires android.permission.GRANT_RUNTIME_PERMISSIONS
            if (!PermissionVersion.isAndroid15()) {
                String action = HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS;
                intent = new Intent(action);
                intent.putExtra(Intent.EXTRA_PACKAGE_NAME, context.getPackageName());
                healthIntentList.add(intent);

                // If adding the package name data prevents navigation, remove the package name data.
                intent = new Intent(action);
                healthIntentList.add(intent);
            }

            // android.provider.Settings.ACTION_HEALTH_HOME_SETTINGS
            intent = new Intent("android.health.connect.action.HEALTH_HOME_SETTINGS");
            healthIntentList.add(intent);

            // health datasharing permission settings pageadd Intentlist , earlier, will preferentially navigate Intent
            intentList.addAll(0, healthIntentList);
        }

        return intentList;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        final String healthAction;
        if (PermissionVersion.isAndroid16()) {
            healthAction = Intent.ACTION_VIEW_PERMISSION_USAGE;
        } else {
            healthAction = "android.intent.action.VIEW_PERMISSION_USAGE";
        }

        final String healthCategory;
        if (PermissionVersion.isAndroid16()) {
            healthCategory = HealthConnectManager.CATEGORY_HEALTH_PERMISSIONS;
        } else {
            healthCategory = "android.intent.category.HEALTH_PERMISSIONS";
        }

        // current whether declare privacy policypage Intent
        boolean registeredHealthPrivacyPolicyAction = false;
        for (ActivityManifestInfo activityInfo : manifestInfo.activityInfoList) {
            List<IntentFilterManifestInfo> intentFilterInfoList = activityInfo.intentFilterInfoList;
            if (intentFilterInfoList == null) {
                continue;
            }
            for (IntentFilterManifestInfo intentFilterInfo : intentFilterInfoList) {
                if (intentFilterInfo.actionList.contains(healthAction) &&
                    intentFilterInfo.categoryList.contains(healthCategory)) {
                    registeredHealthPrivacyPolicyAction = true;
                    break;
                }
            }
            if (registeredHealthPrivacyPolicyAction) {
                // Ifalready declare,
                break;
            }
        }

        if (!registeredHealthPrivacyPolicyAction) {
            String xmlCode = "\t\t<intent-filter>\n"
                           + "\t\t    <action android:name=\"" + healthAction + "\" />\n"
                           + "\t\t    <category android:name=\"" + healthCategory + "\" />\n"
                           + "\t\t</intent-filter>";
            // must app privacy dialog
            // https://developer.android.google.cn/health-and-fitness/guides/health-connect/develop/get-started?hl=zh-cn#show-privacy-policy
            // entry , user below :
            // 1. app details page > permission > health datasharing > privacy policy
            // 2. settings > security privacy > privacy > Health Connect > selectedapp > privacy policy
            // 3. settings > security privacy > privacy > > view permission > Health Connect > selectedapp > privacy policy
            // 4. settings > security privacy > privacy > permissionmanager > Health Connect > selectedapp > privacy policy
            throw new IllegalArgumentException("Please add an intent filter for \"" + activity.getClass() +
                                                "\" in the AndroidManifest.xml file.\n" + xmlCode);
        }
    }
}
