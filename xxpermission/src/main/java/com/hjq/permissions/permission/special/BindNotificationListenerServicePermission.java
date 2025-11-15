package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
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
 *    time   : 2025/06/11
 *    desc   : Notification listener permission class
 */
public final class BindNotificationListenerServicePermission extends SpecialPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only;
     *  if you need the permission name string, please use {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.BIND_NOTIFICATION_LISTENER_SERVICE;

    public static final Parcelable.Creator<BindNotificationListenerServicePermission> CREATOR = new Parcelable.Creator<BindNotificationListenerServicePermission>() {

        @Override
        public BindNotificationListenerServicePermission createFromParcel(Parcel source) {
            return new BindNotificationListenerServicePermission(source);
        }

        @Override
        public BindNotificationListenerServicePermission[] newArray(int size) {
            return new BindNotificationListenerServicePermission[size];
        }
    };

    /** Settings.Secure.ENABLED_NOTIFICATION_LISTENERS */
    private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    /** Service class name of the notification listener */
    @NonNull
    private final String mNotificationListenerServiceClassName;

    public BindNotificationListenerServicePermission(@NonNull Class<? extends NotificationListenerService> notificationListenerServiceClass) {
        this(notificationListenerServiceClass.getName());
    }

    public BindNotificationListenerServicePermission(@NonNull String notificationListenerServiceClassName) {
        mNotificationListenerServiceClassName = notificationListenerServiceClassName;
    }

    private BindNotificationListenerServicePermission(Parcel in) {
        this(Objects.requireNonNull(in.readString()));
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mNotificationListenerServiceClassName);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_4_3;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        // Based on practice, the notification listener permission only appeared on Android 4.3,
        // so earlier versions always return true.
        if (!PermissionVersion.isAndroid4_3()) {
            return true;
        }
        NotificationManager notificationManager;
        if (PermissionVersion.isAndroid6()) {
            notificationManager = context.getSystemService(NotificationManager.class);
        } else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        String serviceClassName = PermissionUtils.isClassExist(mNotificationListenerServiceClassName) ?
                mNotificationListenerServiceClassName : null;
        // Even though this SystemService should never be null, we use defensive programming just in case.
        if (PermissionVersion.isAndroid8_1() && notificationManager != null && serviceClassName != null) {
            return notificationManager.isNotificationListenerAccessGranted(new ComponentName(context, serviceClassName));
        }
        final String enabledNotificationListeners = Settings.Secure.getString(context.getContentResolver(), SETTING_ENABLED_NOTIFICATION_LISTENERS);
        if (TextUtils.isEmpty(enabledNotificationListeners)) {
            return false;
        }
        // Example:
        // com.hjq.permissions.demo/com.hjq.permissions.demo.NotificationMonitorService:com.huawei.health/com.huawei.bone.ui.setting.NotificationPushListener
        final String[] allComponentNameArray = enabledNotificationListeners.split(":");
        for (String component : allComponentNameArray) {
            ComponentName componentName = ComponentName.unflattenFromString(component);
            if (componentName == null) {
                continue;
            }
            if (serviceClassName != null) {
                // Exact match: match both package name and Service class name
                if (context.getPackageName().equals(componentName.getPackageName()) &&
                        serviceClassName.equals(componentName.getClassName())) {
                    return true;
                }
            } else {
                // Fuzzy match: match by package name only
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
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        if (PermissionVersion.isAndroid11() && PermissionUtils.isClassExist(mNotificationListenerServiceClassName)) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS);
            intent.putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                    new ComponentName(context, mNotificationListenerServiceClassName).flattenToString());
            intentList.add(intent);
        }

        String action;
        if (PermissionVersion.isAndroid5_1()) {
            action = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
        } else {
            // android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
            action = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
        }
        intent = new Intent(action);
        intentList.add(intent);

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    public void checkCompliance(@NonNull Activity activity, @NonNull List<IPermission> requestList, @Nullable AndroidManifestInfo manifestInfo) {
        super.checkCompliance(activity, requestList, manifestInfo);
        if (TextUtils.isEmpty(mNotificationListenerServiceClassName)) {
            throw new IllegalArgumentException("Pass the ServiceClass parameter as empty");
        }
        if (!PermissionUtils.isClassExist(mNotificationListenerServiceClassName)) {
            throw new IllegalArgumentException("The passed-in " + mNotificationListenerServiceClassName + " is an invalid class");
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

            if (!PermissionUtils.reverseEqualsString(mNotificationListenerServiceClassName, serviceInfo.name)) {
                // Not the target Service, continue
                continue;
            }

            if (serviceInfo.permission == null || !PermissionUtils.equalsPermission(this, serviceInfo.permission)) {
                // The Service component’s permission node is missing or incorrect
                throw new IllegalArgumentException("Please register a permission node in the AndroidManifest.xml file, for example: "
                        + "<service android:name=\"" + mNotificationListenerServiceClassName + "\" android:permission=\"" + getPermissionName() + "\" />");
            }

            String action;
            if (PermissionVersion.isAndroid4_3()) {
                action = NotificationListenerService.SERVICE_INTERFACE;
            } else {
                action = "android.service.notification.NotificationListenerService";
            }
            // Whether the notification listener service intent is registered
            boolean registeredNotificationListenerServiceAction = false;
            List<IntentFilterManifestInfo> intentFilterInfoList = serviceInfo.intentFilterInfoList;
            if (intentFilterInfoList != null) {
                for (IntentFilterManifestInfo intentFilterInfo : intentFilterInfoList) {
                    if (intentFilterInfo.actionList.contains(action)) {
                        registeredNotificationListenerServiceAction = true;
                        break;
                    }
                }
            }

            if (registeredNotificationListenerServiceAction) {
                // All good, stop and return to avoid the exception below
                return;
            }

            String xmlCode = "\t\t<intent-filter>\n"
                    + "\t\t    <action android:name=\"" + action + "\" />\n"
                    + "\t\t</intent-filter>";
            throw new IllegalArgumentException("Please add an intent filter for \"" + mNotificationListenerServiceClassName +
                    "\" in the AndroidManifest.xml file.\n" + xmlCode);
        }

        // This Service component is not registered in the manifest
        throw new IllegalArgumentException("The \"" + mNotificationListenerServiceClassName + "\" component is not registered in the AndroidManifest.xml file");
    }

    @NonNull
    public String getNotificationListenerServiceClassName() {
        return mNotificationListenerServiceClassName;
    }
}
