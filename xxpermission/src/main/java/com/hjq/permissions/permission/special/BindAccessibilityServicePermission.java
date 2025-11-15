package com.hjq.permissions.permission.special;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
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
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/15
 *    desc   : Accessibility Service permission class
 */
public final class BindAccessibilityServicePermission extends SpecialPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
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
        final String enabledServices = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (TextUtils.isEmpty(enabledServices)) {
            return false;
        }

        String serviceClassName = PermissionUtils.isClassExist(mAccessibilityServiceClassName) ? mAccessibilityServiceClassName : null;
        // Example format: package1/Service1:package2/Service2
        final String[] allComponentNameArray = enabledServices.split(":");
        for (String component : allComponentNameArray) {
            ComponentName componentName = ComponentName.unflattenFromString(component);
            if (componentName == null) {
                continue;
            }
            if (serviceClassName != null) {
                // Exact match: both package name and Service class name must match
                if (context.getPackageName().equals(componentName.getPackageName()) &&
                        serviceClassName.equals(componentName.getClassName())) {
                    return true;
                }
            } else {
                // Fuzzy match: only package name must match
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
        // Why can we only navigate to the general Accessibility Settings page
        // instead of the app-specific Accessibility page?
        // Because the system doesn’t expose that option to apps.
        // You may think, “But I see Settings.ACTION_ACCESSIBILITY_DETAILS_SETTINGS!”
        // Just because it exists doesn’t mean apps can use it — it’s blocked.
        // I’ve tested this already: normal apps cannot navigate to it. Give up.
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
                // Not the target Service, continue looping
                continue;
            }

            if (serviceInfo.permission == null || !PermissionUtils.equalsPermission(this, serviceInfo.permission)) {
                // The Service component either has no permission node or it’s incorrect
                throw new IllegalArgumentException("Please register a permission node in the AndroidManifest.xml file, for example: "
                        + "<service android:name=\"" + mAccessibilityServiceClassName + "\" android:permission=\"" + getPermissionName() + "\" />");
            }

            String action = "android.accessibilityservice.AccessibilityService";
            // Check whether the service has an intent filter for AccessibilityService
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
            // Check whether the service has AccessibilityService metadata
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
                        + "\t\t    android:resource=\"@xml/accessibility_service_config\" />";
                throw new IllegalArgumentException("Please add a meta-data tag for \"" + mAccessibilityServiceClassName +
                        "\" in the AndroidManifest.xml file.\n" + xmlCode);
            }

            // All checks passed, stop loop and return
            return;
        }

        // The Service component was not registered in the manifest
        throw new IllegalArgumentException("The \"" + mAccessibilityServiceClassName + "\" component is not registered in the AndroidManifest.xml file");
    }

    @NonNull
    public String getAccessibilityServiceClassName() {
        return mAccessibilityServiceClassName;
    }
}
