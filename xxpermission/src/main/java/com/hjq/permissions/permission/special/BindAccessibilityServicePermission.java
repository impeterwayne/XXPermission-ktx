package com.hjq.permissions.permission.special;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Accessibility service permission class.
 */
public final class BindAccessibilityServicePermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.BIND_ACCESSIBILITY_SERVICE;

    public static final Creator<BindAccessibilityServicePermission> CREATOR = new Creator<BindAccessibilityServicePermission>() {

        @Override
        public BindAccessibilityServicePermission createFromParcel(Parcel source) {
            return new BindAccessibilityServicePermission(source);
        }

        @Override
        public BindAccessibilityServicePermission[] newArray(int size) {
            return new BindAccessibilityServicePermission[size];
        }
    };

    /** Accessibility Service class name */
    @NonNull
    private final String mAccessibilityServiceClassName;

    public BindAccessibilityServicePermission(@NonNull Class<? extends AccessibilityService> accessibilityServiceClass) {
        this(accessibilityServiceClass.getName());
    }

    public BindAccessibilityServicePermission(@NonNull String accessibilityServiceClassName) {
        mAccessibilityServiceClassName = accessibilityServiceClassName;
    }

    private BindAccessibilityServicePermission(Parcel in) {
        this(Objects.requireNonNull(in.readString()));
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mAccessibilityServiceClassName);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_4_1;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        final String enabledNotificationListeners = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (TextUtils.isEmpty(enabledNotificationListeners)) {
            return false;
        }

        String serviceClassName = PermissionUtils.isClassExist(mAccessibilityServiceClassName) ? mAccessibilityServiceClassName : null;
        // hello.litiaotiao.app/hello.litiaotiao.app.LttService:com.hjq.permissions.demo/com.hjq.permissions.demo.DemoAccessibilityService
        final String[] allComponentNameArray = enabledNotificationListeners.split(":");
        for (String component : allComponentNameArray) {
            ComponentName componentName = ComponentName.unflattenFromString(component);
            if (componentName == null) {
                continue;
            }
            if (serviceClassName != null) {
                // Exact match: match the application package name and the Service class name
                if (context.getPackageName().equals(componentName.getPackageName()) &&
                    serviceClassName.equals(componentName.getClassName())) {
                    return true;
                }
            } else {
                // Fuzzy match: match only the application package name
                if (context.getPackageName().equals(componentName.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(2);
        // here navigate accessibility settings page？ notcurrent app accessibility settings page？
        // This is because the system does not expose that path to the application layer, will , not ？
        // Although Settings defines an Intent named ACTION_ACCESSIBILITY_DETAILS_SETTINGS, that does not mean normal apps can use it
        // Just because it exists does not mean it is usable. This Action was already tested, regular appsno navigate ,
        intentList.add(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        intentList.add(getAndroidSettingIntent());
        return intentList;
    }

    @Override
    public void checkCompliance(@NonNull Activity activity, @NonNull List<IPermission> requestList, @Nullable AndroidManifestInfo manifestInfo) {
        super.checkCompliance(activity, requestList, manifestInfo);
        if (TextUtils.isEmpty(mAccessibilityServiceClassName)) {
            throw new IllegalArgumentException("Pass the ServiceClass parameter as empty");
        }
        if (!PermissionUtils.isClassExist(mAccessibilityServiceClassName)) {
            throw new IllegalArgumentException("The passed-in " + mAccessibilityServiceClassName + " is an invalid class");
        }
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        List<ServiceManifestInfo> serviceInfoList = manifestInfo.serviceInfoList;
        for (ServiceManifestInfo serviceInfo : serviceInfoList) {

            if (serviceInfo == null) {
                continue;
            }

            if (!PermissionUtils.reverseEqualsString(mAccessibilityServiceClassName, serviceInfo.name)) {
                // This is not the target Service, so continue the loop
                continue;
            }

            if (serviceInfo.permission == null || !PermissionUtils.equalsPermission(this, serviceInfo.permission)) {
                // The permission node declared for this Service component is missing or incorrect
                throw new IllegalArgumentException("Please register permission node in the AndroidManifest.xml file, for example: "
                    + "<service android:name=\"" + mAccessibilityServiceClassName + "\" android:permission=\"" + getPermissionName() + "\" />");
            }

            String action = "android.accessibilityservice.AccessibilityService";
            // current whether declare accessibility service Intent
            boolean registeredAccessibilityServiceAction = false;
            List<IntentFilterManifestInfo> intentFilterInfoList = serviceInfo.intentFilterInfoList;
            if (intentFilterInfoList != null) {
                for (IntentFilterManifestInfo intentFilterInfo : intentFilterInfoList) {
                    if (intentFilterInfo.actionList.contains(action)) {
                        registeredAccessibilityServiceAction = true;
                        break;
                    }
                }
            }

            if (!registeredAccessibilityServiceAction) {
                String xmlCode = "\t\t<intent-filter>\n"
                               + "\t\t    <action android:name=\"" + action + "\" />\n"
                               + "\t\t</intent-filter>";
                throw new IllegalArgumentException("Please add an intent filter for \"" + mAccessibilityServiceClassName +
                                                    "\" in the AndroidManifest.xml file.\n" + xmlCode);
            }

            String metaDataName = AccessibilityService.SERVICE_META_DATA;
            // Check whether the accessibility service meta-data is declared
            boolean registeredAccessibilityServiceMetaData = false;
            List<MetaDataManifestInfo> metaDataInfoList = serviceInfo.metaDataInfoList;
            if (metaDataInfoList != null) {
                for (MetaDataManifestInfo metaDataInfo : metaDataInfoList) {
                    if (metaDataName.equals(metaDataInfo.name) && metaDataInfo.resource != 0) {
                        registeredAccessibilityServiceMetaData = true;
                        break;
                    }
                }
            }

            if (!registeredAccessibilityServiceMetaData) {
                String xmlCode = "\t\t<meta-data>\n"
                               + "\t\t    android:name=\"" + metaDataName + "\"\n"
                               + "\t\t    android:resource=\"@xml/accessibility_service_config" + "\""+ " />";
                throw new IllegalArgumentException("Please add an meta data for \"" + mAccessibilityServiceClassName +
                                                   "\" in the AndroidManifest.xml file.\n" + xmlCode);
            }

            // The requirements are satisfied, so stop all loops and return to avoid reaching the exception code below
            return;
        }

        // This Service component is not declared in the manifest file
        throw new IllegalArgumentException("The \"" + mAccessibilityServiceClassName + "\" component is not registered in the AndroidManifest.xml file");
    }

    @NonNull
    public String getAccessibilityServiceClassName() {
        return mAccessibilityServiceClassName;
    }
}