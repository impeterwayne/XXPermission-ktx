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
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : Base class for health data permissions
 *    doc    : https://developer.android.google.cn/health-and-fitness/guides/health-connect/develop/get-started?hl=en
 *             https://developer.android.google.cn/health-and-fitness/guides/health-connect/plan/data-types?hl=en
 *             https://developer.android.google.cn/health-and-fitness/guides/health-connect/plan/availability?hl=en
 *             https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_m5mZzWneZ6MbLDBhKcyMS
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
        // On some Android 14 ~ Android 15 devices, the app settings page does not provide an entry
        // for Health Data Sharing permissions. In this case, jump directly to the Health Data Sharing
        // settings page. On Android 16, jumping to the app details page works directly.
        if (PermissionVersion.isAndroid14() && !PermissionVersion.isAndroid16()) {
            List<Intent> healthIntentList = new ArrayList<>(3);

            // ACTION_MANAGE_HEALTH_PERMISSIONS works on Android 14 but fails on Android 15:
            // java.lang.SecurityException: Permission Denial ... requires android.permission.GRANT_RUNTIME_PERMISSIONS
            if (!PermissionVersion.isAndroid15()) {
                String action = HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS;
                intent = new Intent(action);
                intent.putExtra(Intent.EXTRA_PACKAGE_NAME, context.getPackageName());
                healthIntentList.add(intent);

                // If failure is caused by including package extras, also try without package name
                intent = new Intent(action);
                healthIntentList.add(intent);
            }

            // android.provider.Settings.ACTION_HEALTH_HOME_SETTINGS
            intent = new Intent("android.health.connect.action.HEALTH_HOME_SETTINGS");
            healthIntentList.add(intent);

            // Insert Health Data Sharing intents at the beginning so they are prioritized
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

        // Check if the manifest has registered the Health Privacy Policy activity intent
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
                // Stop once found
                break;
            }
        }

        if (!registeredHealthPrivacyPolicyAction) {
            String xmlCode = "\t\t<intent-filter>\n"
                    + "\t\t    <action android:name=\"" + healthAction + "\" />\n"
                    + "\t\t    <category android:name=\"" + healthCategory + "\" />\n"
                    + "\t\t</intent-filter>";
            // Health Connect requires declaring an entry point for displaying the app’s privacy policy dialog:
            // https://developer.android.google.cn/health-and-fitness/guides/health-connect/develop/get-started?hl=en#show-privacy-policy
            // Entry points for users include:
            //   1. App details > Permissions > Health Data Sharing > Read Privacy Policy
            //   2. Settings > Security & Privacy > Privacy > Health Connect > Selected App > Read Privacy Policy
            //   3. Settings > Security & Privacy > Privacy > Privacy Dashboard > Other Permissions > Health Connect > Selected App > Read Privacy Policy
            //   4. Settings > Security & Privacy > Privacy > Permission Manager > Health Connect > Selected App > Read Privacy Policy
            throw new IllegalArgumentException("Please add an intent filter for \"" + activity.getClass() +
                    "\" in the AndroidManifest.xml file.\n" + xmlCode);
        }
    }
}
