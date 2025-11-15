package com.hjq.permissions.permission;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.admin.DeviceAdminReceiver;
import android.service.notification.NotificationListenerService;
import android.util.LruCache;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.dangerous.StandardDangerousPermission;
import com.hjq.permissions.permission.dangerous.AccessBackgroundLocationPermission;
import com.hjq.permissions.permission.dangerous.AccessMediaLocationPermission;
import com.hjq.permissions.permission.dangerous.BluetoothAdvertisePermission;
import com.hjq.permissions.permission.dangerous.BluetoothConnectPermission;
import com.hjq.permissions.permission.dangerous.BluetoothScanPermission;
import com.hjq.permissions.permission.dangerous.BodySensorsBackgroundPermission;
import com.hjq.permissions.permission.dangerous.BodySensorsPermission;
import com.hjq.permissions.permission.dangerous.GetInstalledAppsPermission;
import com.hjq.permissions.permission.dangerous.StandardFitnessAndWellnessDataPermission;
import com.hjq.permissions.permission.dangerous.NearbyWifiDevicesPermission;
import com.hjq.permissions.permission.dangerous.PostNotificationsPermission;
import com.hjq.permissions.permission.dangerous.ReadExternalStoragePermission;
import com.hjq.permissions.permission.dangerous.ReadHealthDataHistoryPermission;
import com.hjq.permissions.permission.dangerous.ReadHealthDataInBackgroundPermission;
import com.hjq.permissions.permission.dangerous.ReadHealthRatePermission;
import com.hjq.permissions.permission.dangerous.ReadMediaAudioPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaImagesPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaVideoPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaVisualUserSelectedPermission;
import com.hjq.permissions.permission.dangerous.ReadPhoneNumbersPermission;
import com.hjq.permissions.permission.dangerous.StandardHealthRecordsPermission;
import com.hjq.permissions.permission.dangerous.WriteExternalStoragePermission;
import com.hjq.permissions.permission.special.AccessNotificationPolicyPermission;
import com.hjq.permissions.permission.special.BindAccessibilityServicePermission;
import com.hjq.permissions.permission.special.BindDeviceAdminPermission;
import com.hjq.permissions.permission.special.BindNotificationListenerServicePermission;
import com.hjq.permissions.permission.special.BindVpnServicePermission;
import com.hjq.permissions.permission.special.ManageExternalStoragePermission;
import com.hjq.permissions.permission.special.NotificationServicePermission;
import com.hjq.permissions.permission.special.PackageUsageStatsPermission;
import com.hjq.permissions.permission.special.PictureInPicturePermission;
import com.hjq.permissions.permission.special.RequestIgnoreBatteryOptimizationsPermission;
import com.hjq.permissions.permission.special.RequestInstallPackagesPermission;
import com.hjq.permissions.permission.special.ScheduleExactAlarmPermission;
import com.hjq.permissions.permission.special.SystemAlertWindowPermission;
import com.hjq.permissions.permission.special.UseFullScreenIntentPermission;
import com.hjq.permissions.permission.special.WriteSettingsPermission;
import com.hjq.permissions.tools.PermissionVersion;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : List of dangerous and special permissions, refer to {@link Manifest.permission}
 *    doc    : https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn
 *             https://developer.android.google.cn/reference/android/health/connect/HealthPermissions
 *             https://developer.android.google.cn/guide/topics/permissions/overview?hl=zh-cn#normal-dangerous
 *             http://www.taf.org.cn/upload/AssociationStandard/TTAF%20004-2017%20Android%E6%9D%83%E9%99%90%E8%B0%83%E7%94%A8%E5%BC%80%E5%8F%91%E8%80%85%E6%8C%87%E5%8D%97.pdf
 */
public final class PermissionLists {

    /** Private constructor */
    private PermissionLists() {
        // default implementation ignored
    }

    /** Number of permissions */
    private static final int PERMISSION_COUNT = 151;

    /**
     * Permission object cache collection
     *
     * Here is an explanation of why IPermission objects are cached in a collection instead of being defined as static variables or constants. There are several reasons:
     *
     * 1. If you define them directly as constants or static variables, there is a problem: if the project enables obfuscation mode (minifyEnabled = true), unused constants or static variables will still be retained. I don't know why Android Studio does this, but it is a problem. The best solution I have found so far is to define them as static methods. If the static method is not called, the code will be removed during obfuscation.
     * 2. If you define them directly as constants or static variables, there is another problem: once someone accesses this class for the first time, many objects will be initialized, regardless of whether the permission is used or not. This is not good for performance, even though the performance impact is minimal. But in the spirit of saving where possible, a static collection is used to store these permission objects, and they are only created when needed.
     */
    private static final LruCache<String, IPermission> PERMISSION_CACHE_MAP = new LruCache<>(PERMISSION_COUNT);

    /**
     * Get the cached permission object
     *
     * @param permissionName            Permission name
     */
    @Nullable
    private static IPermission getCachePermission(@NonNull String permissionName) {
        return PERMISSION_CACHE_MAP.get(permissionName);
    }

    /**
     * Add a permission object to the cache
     *
     * @param permission                Permission object
     */
    private static IPermission putCachePermission(@NonNull IPermission permission) {
        PERMISSION_CACHE_MAP.put(permission.getPermissionName(), permission);
        return permission;
    }

    /**
     * Read app list permission (dangerous permission, a permission created by the Telecommunication Terminal Industry Association and major Chinese phone manufacturers)
     *
     * Github issue: https://github.com/getActivity/XXPermissions/issues/175
     * Implementation guide for mobile terminal application software list permission: http://www.taf.org.cn/StdDetail.aspx?uid=3A7D6656-43B8-4C46-8871-E379A3EA1D48&stdType=TAF
     *
     * Note:
     *   1. You need to register the QUERY_ALL_PACKAGES permission or the <queries> node in the manifest file, otherwise you will not be able to get the installed app list on Android 11 even if the permission is granted.
     *   2. This permission may be granted on some phones, not granted on others, and cannot be requested on some phones. It depends on whether the manufacturer supports it, and support does not mean it is granted by default.
     *   3. If you register the QUERY_ALL_PACKAGES permission in the manifest for convenience and your app needs to be published on Google Play, please check Google Play's policy:
     *      https://support.google.com/googleplay/android-developer/answer/9888170?hl=zh-Hans
     */
    @NonNull
    public static IPermission getGetInstalledAppsPermission() {
        IPermission permission = getCachePermission(GetInstalledAppsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new GetInstalledAppsPermission());
    }

    /**
     * Full-screen notification permission (special permission, newly added in Android 14)
     *
     * Note: If your app needs to be published on Google Play, please add this permission with caution. Relevant document introductions are as follows:
     * 1. Understand the requirements for foreground services and full-screen intents: https://support.google.com/googleplay/android-developer/answer/13392821?hl=zh-Hans
     * 2. Google Play's requirements for full-screen intents in Android 14: https://orangeoma.zendesk.com/hc/en-us/articles/14126775576988-Google-Play-requirements-on-Full-screen-intent-for-Android-14
     */
    @NonNull
    public static IPermission getUseFullScreenIntentPermission() {
        IPermission permission = getCachePermission(UseFullScreenIntentPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new UseFullScreenIntentPermission());
    }

    /**
     * Alarm permission (special permission, newly added in Android 12)
     *
     * Note: This permission is different from other special permissions in that it is granted by default, and the user can also manually revoke the authorization.
     * Official document introduction: https://developer.android.google.cn/about/versions/12/behavior-changes-12?hl=zh_cn#exact-alarm-permission
     * Apps can only declare this permission if their core functionality supports the precise alarm requirement. Apps requesting this restricted permission need to undergo review; if the app does not meet the acceptable use case standards, it is not allowed to be published on Google Play.
     * See Google Play's requirements for alarm permission: https://support.google.com/googleplay/android-developer/answer/9888170?hl=zh-Hans
     */
    @NonNull
    public static IPermission getScheduleExactAlarmPermission() {
        IPermission permission = getCachePermission(ScheduleExactAlarmPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ScheduleExactAlarmPermission());
    }

    /**
     * All files access permission (special permission, newly added in Android 11)
     *
     * In order to be compatible with Android 11 and below, you need to register
     * {@link PermissionNames#READ_EXTERNAL_STORAGE} and {@link PermissionNames#WRITE_EXTERNAL_STORAGE} permissions in the manifest file
     *
     * If your app needs to be published on Google Play, you need to read Google's Play Store policy in detail:
     * https://support.google.com/googleplay/android-developer/answer/9956427
     */
    @NonNull
    public static IPermission getManageExternalStoragePermission() {
        IPermission permission = getCachePermission(ManageExternalStoragePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ManageExternalStoragePermission());
    }

    /**
     * Install application permission (special permission, newly added in Android 8.0)
     *
     * Android 11 feature adjustment, installing applications from external sources requires restarting the app: https://cloud.tencent.com/developer/news/637591
     * Practice has shown that Android 12 has fixed this problem, and the application will not restart after authorization or deauthorization.
     */
    @NonNull
    public static IPermission getRequestInstallPackagesPermission() {
        IPermission permission = getCachePermission(RequestInstallPackagesPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new RequestInstallPackagesPermission());
    }

    /**
     * Picture-in-picture permission (special permission, newly added in Android 8.0, note that this permission can be requested without being registered in the manifest file)
     *
     * Note: This permission is different from other special permissions in that it is granted by default, and the user can also manually revoke the authorization.
     */
    @NonNull
    public static IPermission getPictureInPicturePermission() {
        IPermission permission = getCachePermission(PictureInPicturePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new PictureInPicturePermission());
    }

    /**
     * Floating window permission (special permission, newly added in Android 6.0, but some domestic manufacturers have compatible devices before Android 6.0)
     *
     * In Android 10 and previous versions, you can jump to the app's floating window settings page, while in Android 11 and later versions, you can only jump to the system settings floating window management list.
     * Official explanation: https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
     */
    @NonNull
    public static IPermission getSystemAlertWindowPermission() {
        IPermission permission = getCachePermission(SystemAlertWindowPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new SystemAlertWindowPermission());
    }

    /**
     * Write system settings permission (special permission, newly added in Android 6.0)
     */
    @SuppressWarnings("unused")
    @NonNull
    public static IPermission getWriteSettingsPermission() {
        IPermission permission = getCachePermission(WriteSettingsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new WriteSettingsPermission());
    }

    /**
     * Request to ignore battery optimization options permission (special permission, newly added in Android 6.0)
     */
    @NonNull
    public static IPermission getRequestIgnoreBatteryOptimizationsPermission() {
        IPermission permission = getCachePermission(RequestIgnoreBatteryOptimizationsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new RequestIgnoreBatteryOptimizationsPermission());
    }

    /**
     * Do Not Disturb permission, which can control the phone's ringing mode [Silent, Vibration] (special permission, newly added in Android 6.0)
     */
    @NonNull
    public static IPermission getAccessNotificationPolicyPermission() {
        IPermission permission = getCachePermission(AccessNotificationPolicyPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new AccessNotificationPolicyPermission());
    }

    /**
     * View application usage permission, referred to as usage statistics permission (special permission, newly added in Android 5.0)
     */
    @NonNull
    public static IPermission getPackageUsageStatsPermission() {
        IPermission permission = getCachePermission(PackageUsageStatsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new PackageUsageStatsPermission());
    }

    /**
     * Notification bar listening permission (special permission, newly added in Android 4.3, note that this permission can be requested without being registered in the manifest file)
     *
     * @param notificationListenerServiceClass             Notification listener's Service type
     */
    @NonNull
    public static IPermission getBindNotificationListenerServicePermission(@NonNull Class<? extends NotificationListenerService> notificationListenerServiceClass) {
        // This object will not be included in the cached collection because it carries specific parameters. Only those without parameters can be put into the cached collection.
        return new BindNotificationListenerServicePermission(notificationListenerServiceClass);
    }

    /**
     * VPN permission (special permission, newly added in Android 4.0, note that this permission can be requested without being registered in the manifest file)
     */
    @NonNull
    public static IPermission getBindVpnServicePermission() {
        IPermission permission = getCachePermission(BindVpnServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BindVpnServicePermission());
    }

    /**
     * Notification bar permission (special permission, only devices with Android 4.4 and above can determine the permission status, note that this permission can be requested without being registered in the manifest file)
     *
     * @param channelId         Notification channel id
     */
    @NonNull
    public static IPermission getNotificationServicePermission(@NonNull String channelId) {
        // This object will not be included in the cached collection because it carries specific parameters. Only those without parameters can be put into the cached collection.
        return new NotificationServicePermission(channelId);
    }

    /**
     * Same as above
     */
    @NonNull
    public static IPermission getNotificationServicePermission() {
        IPermission permission = getCachePermission(NotificationServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new NotificationServicePermission());
    }

    /**
     * Accessibility service permission (special permission, newly added in Android 4.1, note that this permission can be requested without being registered in the manifest file)
     *
     * @param accessibilityServiceClass                                 Accessibility Service class
     */
    @NonNull
    public static IPermission getBindAccessibilityServicePermission(@NonNull Class<? extends AccessibilityService> accessibilityServiceClass) {
        return new BindAccessibilityServicePermission(accessibilityServiceClass);
    }

    /**
     * Device management permission (special permission, newly added in Android 2.2, note that this permission can be requested without being registered in the manifest file)
     *
     * @param deviceAdminReceiverClass              Device manager's BroadcastReceiver class
     * @param extraAddExplanation                   Additional explanation for requesting device manager permission
     */
    @NonNull
    public static IPermission getBindDeviceAdminPermission(@NonNull Class<? extends DeviceAdminReceiver> deviceAdminReceiverClass, @Nullable String extraAddExplanation) {
        return new BindDeviceAdminPermission(deviceAdminReceiverClass, extraAddExplanation);
    }

    /**
     * Same as above
     */
    @NonNull
    public static IPermission getBindDeviceAdminPermission(@NonNull Class<? extends DeviceAdminReceiver> deviceAdminReceiverClass) {
        return new BindDeviceAdminPermission(deviceAdminReceiverClass, null);
    }

    /* ------------------------------------ This is a beautiful dividing line ------------------------------------ */

    /**
     * Permission to access part of the photos and videos (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadMediaVisualUserSelectedPermission() {
        IPermission permission = getCachePermission(ReadMediaVisualUserSelectedPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaVisualUserSelectedPermission());
    }

    /**
     * Permission to send notifications (newly added in Android 13.0)
     *
     * In order to be compatible, the framework will automatically add the {@link PermissionLists#getNotificationServicePermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getPostNotificationsPermission() {
        IPermission permission = getCachePermission(PostNotificationsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new PostNotificationsPermission());
    }

    /**
     * WIFI permission (newly added in Android 13.0)
     *
     * You need to add the android:usesPermissionFlags="neverForLocation" attribute in the manifest file (indicating not to infer the device's geographical location)
     * Otherwise, it will cause the inability to scan nearby WIFI devices without location permission. This has been tested. Below is the manifest permission registration example, please refer to the following for registration
     * <uses-permission android:name="android.permission.NEARBY_WIFI_DEVICES" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
     *
     * In order to be compatible with Android 13 and below, you need to register the {@link PermissionNames#ACCESS_FINE_LOCATION} permission in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getAccessFineLocationPermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getNearbyWifiDevicesPermission() {
        IPermission permission = getCachePermission(NearbyWifiDevicesPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new NearbyWifiDevicesPermission());
    }

    /**
     * Background sensor permission (newly added in Android 13.0)
     *
     * Note:
     * 1. Once you apply for this permission, you need to select "Always allow" during authorization, and you cannot choose "Allow only while in use".
     * 2. If your App only uses sensor functions in the foreground and does not have scenarios for use in the background, please do not apply for this permission (background sensor permission)
     */
    @NonNull
    public static IPermission getBodySensorsBackgroundPermission() {
        IPermission permission = getCachePermission(BodySensorsBackgroundPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BodySensorsBackgroundPermission());
    }

    /**
     * Permission to read pictures (newly added in Android 13.0)
     *
     * In order to be compatible with Android 13 and below, you need to register the {@link PermissionNames#READ_EXTERNAL_STORAGE} permission in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getReadExternalStoragePermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getReadMediaImagesPermission() {
        IPermission permission = getCachePermission(ReadMediaImagesPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaImagesPermission());
    }

    /**
     * Permission to read videos (newly added in Android 13.0)
     *
     * In order to be compatible with Android 13 and below, you need to register the {@link PermissionNames#READ_EXTERNAL_STORAGE} permission in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getReadExternalStoragePermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getReadMediaVideoPermission() {
        IPermission permission = getCachePermission(ReadMediaVideoPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaVideoPermission());
    }

    /**
     * Permission to read audio (newly added in Android 13.0)
     *
     * In order to be compatible with Android 13 and below, you need to register the {@link PermissionNames#READ_EXTERNAL_STORAGE} permission in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getReadExternalStoragePermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getReadMediaAudioPermission() {
        IPermission permission = getCachePermission(ReadMediaAudioPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadMediaAudioPermission());
    }

    /**
     * Bluetooth scanning permission (newly added in Android 12.0)
     *
     * You need to add the android:usesPermissionFlags="neverForLocation" attribute in the manifest file (indicating not to infer the device's geographical location)
     * Otherwise, it will cause the inability to scan nearby Bluetooth devices without location permission. This has been tested. Below is the manifest permission registration example, please refer to the following for registration
     * <uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
     *
     * In order to be compatible with Android 12 and below, you need to register the {@link Manifest.permission#BLUETOOTH_ADMIN} and {@link PermissionNames#ACCESS_FINE_LOCATION} permissions in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getAccessFineLocationPermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getBluetoothScanPermission() {
        IPermission permission = getCachePermission(BluetoothScanPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BluetoothScanPermission());
    }

    /**
     * Bluetooth connection permission (newly added in Android 12.0)
     *
     * In order to be compatible with Android 12 and below, you need to register the {@link Manifest.permission#BLUETOOTH} permission in the manifest file.
     */
    @NonNull
    public static IPermission getBluetoothConnectPermission() {
        IPermission permission = getCachePermission(BluetoothConnectPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BluetoothConnectPermission());
    }

    /**
     * Bluetooth broadcast permission (newly added in Android 12.0)
     *
     * To broadcast the current device's Bluetooth for other devices to scan, this permission is required.
     * In order to be compatible with Android 12 and below, you need to register the {@link Manifest.permission#BLUETOOTH_ADMIN} permission in the manifest file.
     */
    @NonNull
    public static IPermission getBluetoothAdvertisePermission() {
        IPermission permission = getCachePermission(BluetoothAdvertisePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BluetoothAdvertisePermission());
    }

    /**
     * Permission to obtain location in the background (newly added in Android 10.0)
     *
     * Note:
     * 1. Once you apply for this permission, you need to select "Always allow" during authorization, and you cannot choose "Allow only while in use".
     * 2. If your App only uses location functions in the foreground and does not have scenarios for use in the background, please do not apply for this permission.
     */
    @NonNull
    public static IPermission getAccessBackgroundLocationPermission() {
        IPermission permission = getCachePermission(AccessBackgroundLocationPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new AccessBackgroundLocationPermission());
    }

    /**
     * Permission to obtain activity recognition (newly added in Android 10.0)
     *
     * Note: Android 10 and below do not require the sensor (BODY_SENSORS) permission to obtain step count.
     * Github issue: https://github.com/getActivity/XXPermissions/issues/150
     */
    @NonNull
    public static IPermission getActivityRecognitionPermission() {
        String permissionName = PermissionNames.ACTIVITY_RECOGNITION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionVersion.ANDROID_10));
    }

    /**
     * Permission to access the location information of media (newly added in Android 10.0)
     *
     * Note: If this permission is successfully applied for but the geographical information of the photo cannot be read normally, you need to apply for storage permission. The specific situation can be divided into the following two cases:
     *
     * 1. In the case of adapting to scoped storage:
     *     1) If the project targetSdkVersion <= 32, you need to apply for {@link PermissionLists#getReadExternalStoragePermission()}
     *     2) If the project targetSdkVersion >= 33, you need to apply for {@link PermissionLists#getReadMediaImagesPermission()} or
     *        {@link PermissionLists#getReadMediaVideoPermission()}, and you need to grant all, partial granting is not allowed.
     *
     * 2. In the case of not adapting to scoped storage:
     *     1) If the project targetSdkVersion <= 29, you need to apply for {@link PermissionLists#getReadExternalStoragePermission()}
     *     2) If the project targetSdkVersion >= 30, you need to apply for {@link PermissionLists#getManageExternalStoragePermission()}
     */
    @NonNull
    public static IPermission getAccessMediaLocationPermission() {
        IPermission permission = getCachePermission(AccessMediaLocationPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new AccessMediaLocationPermission());
    }

    /**
     * Permission to allow the calling application to continue the call in another application (newly added in Android 9.0)
     *
     * Note: This permission, when requested on some devices that cannot make calls (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getAcceptHandoverPermission() {
        String permissionName = PermissionNames.ACCEPT_HANDOVER;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_9));
    }

    /**
     * Permission to read phone number (newly added in Android 8.0)
     *
     * Note: This permission, when requested on some devices that cannot make calls (e.g., Xiaomi Tablet 5), the system will directly callback success. However, this is not guaranteed. If you apply for it, please pay attention to handle the permission application failure.
     *
     * In order to be compatible with Android 8.0 and below, you need to register the {@link PermissionNames#READ_PHONE_STATE} permission in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getReadPhoneStatePermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getReadPhoneNumbersPermission() {
        IPermission permission = getCachePermission(ReadPhoneNumbersPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadPhoneNumbersPermission());
    }

    /**
     * Permission to answer phone calls (newly added in Android 8.0, below Android 8.0 can use simulated headset button events to answer calls, this method does not require permission)
     *
     * Note: This permission, when requested on some devices that cannot make calls (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getAnswerPhoneCallsPermission() {
        String permissionName = PermissionNames.ANSWER_PHONE_CALLS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_8));
    }

    /**
     * Permission to read external storage
     */
    @NonNull
    public static IPermission getReadExternalStoragePermission() {
        IPermission permission = getCachePermission(ReadExternalStoragePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadExternalStoragePermission());
    }

    /**
     * Permission to write external storage (Note: This permission does not work on devices with targetSdk >= Android 11 and Android 11 and above, please adapt to the scoped storage feature instead of permission application)
     */
    @NonNull
    public static IPermission getWriteExternalStoragePermission() {
        IPermission permission = getCachePermission(WriteExternalStoragePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new WriteExternalStoragePermission());
    }

    /**
     * Camera permission
     */
    @NonNull
    public static IPermission getCameraPermission() {
        String permissionName = PermissionNames.CAMERA;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionVersion.ANDROID_6));
    }

    /**
     * Microphone permission
     */
    @NonNull
    public static IPermission getRecordAudioPermission() {
        String permissionName = PermissionNames.RECORD_AUDIO;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to obtain precise location
     */
    @NonNull
    public static IPermission getAccessFineLocationPermission() {
        String permissionName = PermissionNames.ACCESS_FINE_LOCATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.LOCATION, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to obtain coarse location
     */
    @NonNull
    public static IPermission getAccessCoarseLocationPermission() {
        String permissionName = PermissionNames.ACCESS_COARSE_LOCATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.LOCATION, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to read contacts
     */
    @NonNull
    public static IPermission getReadContactsPermission() {
        String permissionName = PermissionNames.READ_CONTACTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CONTACTS, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to modify contacts
     */
    @NonNull
    public static IPermission getWriteContactsPermission() {
        String permissionName = PermissionNames.WRITE_CONTACTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CONTACTS, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to access the account list
     */
    @NonNull
    public static IPermission getGetAccountsPermission() {
        String permissionName = PermissionNames.GET_ACCOUNTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CONTACTS, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to read calendar
     */
    @NonNull
    public static IPermission getReadCalendarPermission() {
        String permissionName = PermissionNames.READ_CALENDAR;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CALENDAR, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to modify calendar
     */
    @NonNull
    public static IPermission getWriteCalendarPermission() {
        String permissionName = PermissionNames.WRITE_CALENDAR;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.CALENDAR, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to read phone state. Note that:
     *
     * 1. This permission cannot be obtained on some phones because some systems prohibit applications from obtaining this permission.
     *    So if you apply for this permission and there is no authorization box popping up, but the authorization failure callback is directly returned,
     *    please do not panic, this is not a Bug, not a Bug, not a Bug, but a normal phenomenon.
     *    Follow-up situation report: Some users reported that they could not obtain this permission on iQOO phones, and adding the following permission in the manifest file can solve the problem (this is just a record and does not mean that this method will definitely work).
     *    <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
     *    Github issue: https://github.com/getActivity/XXPermissions/issues/98
     *
     * 2. This permission is directly passed when requested on some phones, but the system does not pop up the authorization dialog, and in fact, it is not authorized.
     *    This is also not a Bug, but the system deliberately does this. If you ask me what to do, I can only say that the arm cannot twist the thigh.
     *    Github issue: https://github.com/getActivity/XXPermissions/issues/369
     */
    @NonNull
    public static IPermission getReadPhoneStatePermission() {
        String permissionName = PermissionNames.READ_PHONE_STATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to make phone calls
     *
     * Note: This permission, when requested on some devices that cannot make calls (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getCallPhonePermission() {
        String permissionName = PermissionNames.CALL_PHONE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to read call logs
     *
     * Note: This permission, when requested on some devices that cannot make calls (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getReadCallLogPermission() {
        String permissionName = PermissionNames.READ_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // Note: In Android 9.0, the permissions related to call logs have been moved to a separate permission group. However, before Android 9.0, the read and write call log permissions belong to the phone permission group.
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to modify call logs
     *
     * Note: This permission, when requested on some devices that cannot make calls (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getWriteCallLogPermission() {
        String permissionName = PermissionNames.WRITE_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // Note: In Android 9.0, the permissions related to call logs have been moved to a separate permission group. However, before Android 9.0, the read and write call log permissions belong to the phone permission group.
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to add voicemail
     */
    @NonNull
    public static IPermission getAddVoicemailPermission() {
        String permissionName = PermissionNames.ADD_VOICEMAIL;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to use SIP video
     */
    @NonNull
    public static IPermission getUseSipPermission() {
        String permissionName = PermissionNames.USE_SIP;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.PHONE, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to process outgoing calls
     *
     * Note: This permission, when requested on some devices that cannot make calls (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     *
     * @deprecated         Deprecated in Android 10, see: https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn#PROCESS_OUTGOING_CALLS
     */
    @NonNull
    public static IPermission getProcessOutgoingCallsPermission() {
        String permissionName = PermissionNames.PROCESS_OUTGOING_CALLS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // Note: In Android 9.0, the permissions related to call logs have been moved to a separate permission group. However, before Android 9.0, the read and write call log permissions belong to the phone permission group.
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to use sensors
     */
    @NonNull
    public static IPermission getBodySensorsPermission() {
        IPermission permission = getCachePermission(BodySensorsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BodySensorsPermission());
    }

    /**
     * Permission to send SMS
     *
     * Note: This permission, when requested on some devices that cannot send SMS (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getSendSmsPermission() {
        String permissionName = PermissionNames.SEND_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to receive SMS
     *
     * Note: This permission, when requested on some devices that cannot send SMS (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getReceiveSmsPermission() {
        String permissionName = PermissionNames.RECEIVE_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to read SMS
     *
     * Note: This permission, when requested on some devices that cannot send SMS (e.g., Xiaomi Tablet 5), the system will directly callback failure. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getReadSmsPermission() {
        String permissionName = PermissionNames.READ_SMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to receive WAP push messages
     *
     * Note: This permission, when requested on some devices that cannot send SMS (e.g., Xiaomi Tablet 5), the system will directly callback success. However, this is not guaranteed. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getReceiveWapPushPermission() {
        String permissionName = PermissionNames.RECEIVE_WAP_PUSH;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /**
     * Permission to receive MMS
     *
     * Note: This permission, when requested on some devices that cannot send SMS (e.g., Xiaomi Tablet 5), the system will directly callback success. However, this is not guaranteed. If you apply for it, please pay attention to handle the permission application failure.
     */
    @NonNull
    public static IPermission getReceiveMmsPermission() {
        String permissionName = PermissionNames.RECEIVE_MMS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardDangerousPermission(permissionName, PermissionGroups.SMS, PermissionVersion.ANDROID_6));
    }

    /* ------------------------------------ This is a beautiful dividing line ------------------------------------ */

    /**
     * Permission to read health data in the background (newly added in Android 15.0)
     *
     * In order to be compatible with Android 15 and below, you need to register the {@link PermissionNames#BODY_SENSORS_BACKGROUND} permission in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getReadHealthDataInBackgroundPermission()} ()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getReadHealthDataInBackgroundPermission() {
        String permissionName = ReadHealthDataInBackgroundPermission.PERMISSION_NAME;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadHealthDataInBackgroundPermission());
    }

    /**
     * Permission to read historical health data (newly added in Android 15.0)
     *
     * Health Connect can read data granted permission for up to 30 days. If you want the app to read records older than 30 days, you need to apply for this permission. Relevant document address:
     * https://developer.android.google.cn/health-and-fitness/guides/health-connect/develop/read-data?hl=zh-cn#read-older-data
     */
    @NonNull
    public static IPermission getReadHealthDataHistoryPermission() {
        String permissionName = ReadHealthDataHistoryPermission.PERMISSION_NAME;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadHealthDataHistoryPermission());
    }

    /**
     * Permission to read the calories burned during exercise (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadActiveCaloriesBurnedPermission() {
        String permissionName = PermissionNames.READ_ACTIVE_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write the calories burned during exercise (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteActiveCaloriesBurnedPermission() {
        String permissionName = PermissionNames.WRITE_ACTIVE_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read activity intensity data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadActivityIntensityPermission() {
        String permissionName = PermissionNames.READ_ACTIVITY_INTENSITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to write activity intensity data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getWriteActivityIntensityPermission() {
        String permissionName = PermissionNames.WRITE_ACTIVITY_INTENSITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read basal body temperature data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBasalBodyTemperaturePermission() {
        String permissionName = PermissionNames.READ_BASAL_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write basal body temperature data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBasalBodyTemperaturePermission() {
        String permissionName = PermissionNames.WRITE_BASAL_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read basal metabolic rate data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBasalMetabolicRatePermission() {
        String permissionName = PermissionNames.READ_BASAL_METABOLIC_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write basal metabolic rate data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBasalMetabolicRatePermission() {
        String permissionName = PermissionNames.WRITE_BASAL_METABOLIC_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read blood glucose data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBloodGlucosePermission() {
        String permissionName = PermissionNames.READ_BLOOD_GLUCOSE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write blood glucose data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBloodGlucosePermission() {
        String permissionName = PermissionNames.WRITE_BLOOD_GLUCOSE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read blood pressure data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBloodPressurePermission() {
        String permissionName = PermissionNames.READ_BLOOD_PRESSURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write blood pressure data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBloodPressurePermission() {
        String permissionName = PermissionNames.WRITE_BLOOD_PRESSURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read body fat data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBodyFatPermission() {
        String permissionName = PermissionNames.READ_BODY_FAT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write body fat data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBodyFatPermission() {
        String permissionName = PermissionNames.WRITE_BODY_FAT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read body temperature data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBodyTemperaturePermission() {
        String permissionName = PermissionNames.READ_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write body temperature data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBodyTemperaturePermission() {
        String permissionName = PermissionNames.WRITE_BODY_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read body water mass data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBodyWaterMassPermission() {
        String permissionName = PermissionNames.READ_BODY_WATER_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write body water mass data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBodyWaterMassPermission() {
        String permissionName = PermissionNames.WRITE_BODY_WATER_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read bone mass data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadBoneMassPermission() {
        String permissionName = PermissionNames.READ_BONE_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write bone mass data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteBoneMassPermission() {
        String permissionName = PermissionNames.WRITE_BONE_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read cervical mucus data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadCervicalMucusPermission() {
        String permissionName = PermissionNames.READ_CERVICAL_MUCUS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write cervical mucus data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteCervicalMucusPermission() {
        String permissionName = PermissionNames.WRITE_CERVICAL_MUCUS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read distance data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadDistancePermission() {
        String permissionName = PermissionNames.READ_DISTANCE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write distance data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteDistancePermission() {
        String permissionName = PermissionNames.WRITE_DISTANCE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read elevation gained data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadElevationGainedPermission() {
        String permissionName = PermissionNames.READ_ELEVATION_GAINED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write elevation gained data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteElevationGainedPermission() {
        String permissionName = PermissionNames.WRITE_ELEVATION_GAINED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read exercise data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadExercisePermission() {
        String permissionName = PermissionNames.READ_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write exercise data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteExercisePermission() {
        String permissionName = PermissionNames.WRITE_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read exercise routes data (newly added in Android 15.0)
     */
    @NonNull
    public static IPermission getReadExerciseRoutesPermission() {
        String permissionName = PermissionNames.READ_EXERCISE_ROUTES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * Permission to write exercise route data (newly added in Android 15.0)
     */
    @NonNull
    public static IPermission getWriteExerciseRoutePermission() {
        String permissionName = PermissionNames.WRITE_EXERCISE_ROUTE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * Permission to read floors climbed data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadFloorsClimbedPermission() {
        String permissionName = PermissionNames.READ_FLOORS_CLIMBED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write floors climbed data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteFloorsClimbedPermission() {
        String permissionName = PermissionNames.WRITE_FLOORS_CLIMBED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read heart rate data (newly added in Android 14.0)
     *
     * In order to be compatible with Android 14 and below, you need to register the {@link PermissionNames#BODY_SENSORS} permission in the manifest file.
     * In addition, the framework will automatically add the {@link PermissionLists#getBodySensorsPermission()} permission for dynamic application on older Android devices, no manual addition is required.
     */
    @NonNull
    public static IPermission getReadHeartRatePermission() {
        IPermission permission = getCachePermission(ReadHealthRatePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ReadHealthRatePermission());
    }

    /**
     * Permission to write heart rate data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteHeartRatePermission() {
        String permissionName = PermissionNames.WRITE_HEART_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read heart rate variability data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadHeartRateVariabilityPermission() {
        String permissionName = PermissionNames.READ_HEART_RATE_VARIABILITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write heart rate variability data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteHeartRateVariabilityPermission() {
        String permissionName = PermissionNames.WRITE_HEART_RATE_VARIABILITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read height data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadHeightPermission() {
        String permissionName = PermissionNames.READ_HEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write height data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteHeightPermission() {
        String permissionName = PermissionNames.WRITE_HEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read hydration data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadHydrationPermission() {
        String permissionName = PermissionNames.READ_HYDRATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write hydration data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteHydrationPermission() {
        String permissionName = PermissionNames.WRITE_HYDRATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read intermenstrual bleeding data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadIntermenstrualBleedingPermission() {
        String permissionName = PermissionNames.READ_INTERMENSTRUAL_BLEEDING;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write intermenstrual bleeding data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteIntermenstrualBleedingPermission() {
        String permissionName = PermissionNames.WRITE_INTERMENSTRUAL_BLEEDING;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read lean body mass data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadLeanBodyMassPermission() {
        String permissionName = PermissionNames.READ_LEAN_BODY_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write lean body mass data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteLeanBodyMassPermission() {
        String permissionName = PermissionNames.WRITE_LEAN_BODY_MASS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read menstruation data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadMenstruationPermission() {
        String permissionName = PermissionNames.READ_MENSTRUATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write menstruation data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteMenstruationPermission() {
        String permissionName = PermissionNames.WRITE_MENSTRUATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read mindfulness data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMindfulnessPermission() {
        String permissionName = PermissionNames.READ_MINDFULNESS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to write mindfulness data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getWriteMindfulnessPermission() {
        String permissionName = PermissionNames.WRITE_MINDFULNESS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read nutrition data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadNutritionPermission() {
        String permissionName = PermissionNames.READ_NUTRITION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write nutrition data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteNutritionPermission() {
        String permissionName = PermissionNames.WRITE_NUTRITION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read ovulation test data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadOvulationTestPermission() {
        String permissionName = PermissionNames.READ_OVULATION_TEST;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write ovulation test data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteOvulationTestPermission() {
        String permissionName = PermissionNames.WRITE_OVULATION_TEST;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read oxygen saturation data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadOxygenSaturationPermission() {
        String permissionName = PermissionNames.READ_OXYGEN_SATURATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write oxygen saturation data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteOxygenSaturationPermission() {
        String permissionName = PermissionNames.WRITE_OXYGEN_SATURATION;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read planned exercise data (newly added in Android 15.0)
     */
    @NonNull
    public static IPermission getReadPlannedExercisePermission() {
        String permissionName = PermissionNames.READ_PLANNED_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * Permission to write planned exercise data (newly added in Android 15.0)
     */
    @NonNull
    public static IPermission getWritePlannedExercisePermission() {
        String permissionName = PermissionNames.WRITE_PLANNED_EXERCISE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * Permission to read power data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadPowerPermission() {
        String permissionName = PermissionNames.READ_POWER;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write power data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWritePowerPermission() {
        String permissionName = PermissionNames.WRITE_POWER;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read respiratory rate data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadRespiratoryRatePermission() {
        String permissionName = PermissionNames.READ_RESPIRATORY_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write respiratory rate data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteRespiratoryRatePermission() {
        String permissionName = PermissionNames.WRITE_RESPIRATORY_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read resting heart rate data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadRestingHeartRatePermission() {
        String permissionName = PermissionNames.READ_RESTING_HEART_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write resting heart rate data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteRestingHeartRatePermission() {
        String permissionName = PermissionNames.WRITE_RESTING_HEART_RATE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read sexual activity data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadSexualActivityPermission() {
        String permissionName = PermissionNames.READ_SEXUAL_ACTIVITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write sexual activity data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteSexualActivityPermission() {
        String permissionName = PermissionNames.WRITE_SEXUAL_ACTIVITY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read skin temperature data (newly added in Android 15.0)
     */
    @NonNull
    public static IPermission getReadSkinTemperaturePermission() {
        String permissionName = PermissionNames.READ_SKIN_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * Permission to write skin temperature data (newly added in Android 15.0)
     */
    @NonNull
    public static IPermission getWriteSkinTemperaturePermission() {
        String permissionName = PermissionNames.WRITE_SKIN_TEMPERATURE;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_15));
    }

    /**
     * Permission to read sleep data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadSleepPermission() {
        String permissionName = PermissionNames.READ_SLEEP;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write sleep data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteSleepPermission() {
        String permissionName = PermissionNames.WRITE_SLEEP;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read speed data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadSpeedPermission() {
        String permissionName = PermissionNames.READ_SPEED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write speed data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteSpeedPermission() {
        String permissionName = PermissionNames.WRITE_SPEED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read step count data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadStepsPermission() {
        String permissionName = PermissionNames.READ_STEPS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write step count data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteStepsPermission() {
        String permissionName = PermissionNames.WRITE_STEPS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read the total number of calories burned (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadTotalCaloriesBurnedPermission() {
        String permissionName = PermissionNames.READ_TOTAL_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write the total number of calories burned (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteTotalCaloriesBurnedPermission() {
        String permissionName = PermissionNames.WRITE_TOTAL_CALORIES_BURNED;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read VO2 max data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadVo2MaxPermission() {
        String permissionName = PermissionNames.READ_VO2_MAX;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write VO2 max data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteVo2MaxPermission() {
        String permissionName = PermissionNames.WRITE_VO2_MAX;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read weight data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadWeightPermission() {
        String permissionName = PermissionNames.READ_WEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write weight data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteWeightPermission() {
        String permissionName = PermissionNames.WRITE_WEIGHT;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to read wheelchair pushes data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getReadWheelchairPushesPermission() {
        String permissionName = PermissionNames.READ_WHEELCHAIR_PUSHES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /**
     * Permission to write wheelchair pushes data (newly added in Android 14.0)
     */
    @NonNull
    public static IPermission getWriteWheelchairPushesPermission() {
        String permissionName = PermissionNames.WRITE_WHEELCHAIR_PUSHES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardFitnessAndWellnessDataPermission(permissionName, PermissionVersion.ANDROID_14));
    }

    /* ------------------------------------ This is a beautiful dividing line ------------------------------------ */

    /**
     * Permission to read allergic reaction data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataAllergiesIntolerancesPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_ALLERGIES_INTOLERANCES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read condition data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataConditionsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_CONDITIONS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read laboratory results data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataLaboratoryResultsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_LABORATORY_RESULTS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read medication data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataMedicationsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_MEDICATIONS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read personal details data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataPersonalDetailsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PERSONAL_DETAILS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read practitioner details data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataPractitionerDetailsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PRACTITIONER_DETAILS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read pregnancy data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataPregnancyPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PREGNANCY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read procedures data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataProceduresPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_PROCEDURES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read social history data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataSocialHistoryPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_SOCIAL_HISTORY;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read vaccine data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataVaccinesPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_VACCINES;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read visit data, including location, appointment time, and medical organization name (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataVisitsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_VISITS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to read vital signs data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getReadMedicalDataVitalSignsPermission() {
        String permissionName = PermissionNames.READ_MEDICAL_DATA_VITAL_SIGNS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }

    /**
     * Permission to write all health records data (newly added in Android 16.0)
     */
    @NonNull
    public static IPermission getWriteMedicalDataPermission() {
        String permissionName = PermissionNames.WRITE_MEDICAL_DATA;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new StandardHealthRecordsPermission(permissionName, PermissionVersion.ANDROID_16));
    }
}

