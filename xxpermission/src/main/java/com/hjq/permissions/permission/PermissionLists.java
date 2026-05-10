package com.hjq.permissions.permission;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.admin.DeviceAdminReceiver;
import android.service.notification.NotificationListenerService;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.dangerous.AccessBackgroundLocationPermission;
import com.hjq.permissions.permission.dangerous.AccessMediaLocationPermission;
import com.hjq.permissions.permission.dangerous.BluetoothAdvertisePermission;
import com.hjq.permissions.permission.dangerous.BluetoothConnectPermission;
import com.hjq.permissions.permission.dangerous.BluetoothScanPermission;
import com.hjq.permissions.permission.dangerous.BodySensorsBackgroundPermission;
import com.hjq.permissions.permission.dangerous.BodySensorsPermission;
import com.hjq.permissions.permission.dangerous.GetInstalledAppsPermission;
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
import com.hjq.permissions.permission.dangerous.StandardDangerousPermission;
import com.hjq.permissions.permission.dangerous.StandardFitnessAndWellnessDataPermission;
import com.hjq.permissions.permission.dangerous.StandardHealthRecordsPermission;
import com.hjq.permissions.permission.dangerous.WriteExternalStoragePermission;
import com.hjq.permissions.permission.special.AccessNotificationPolicyPermission;
import com.hjq.permissions.permission.special.BindAccessibilityServicePermission;
import com.hjq.permissions.permission.special.BindDeviceAdminPermission;
import com.hjq.permissions.permission.special.BindNotificationListenerServicePermission;
import com.hjq.permissions.permission.special.BindVpnServicePermission;
import com.hjq.permissions.permission.special.ManageExternalStoragePermission;
import com.hjq.permissions.permission.special.ManageMediaPermission;
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
 * Factory methods for dangerous and special permissions.
 * See {@link Manifest.permission} for the platform definitions.
 */
public final class PermissionLists {

    /** Private constructor */
    private PermissionLists() {
        // default implementation ignored
    }

    /** Permission count */
    private static final int PERMISSION_COUNT = 151;

    /**
     * Cache of reusable permission objects.
     * Static methods are used instead of static instances so unused permissions can still be
     * removed by shrinking, while frequently used parameterless permission objects are created
     * lazily and reused.
     */
    private static final LruCache<String, IPermission> PERMISSION_CACHE_MAP = new LruCache<>(PERMISSION_COUNT);

    /**
     * Returns the cached permission object.
     *
     * @param permissionName the permission name
     */
    @Nullable
    private static IPermission getCachePermission(@NonNull String permissionName) {
        return PERMISSION_CACHE_MAP.get(permissionName);
    }

    /**
     * Adds a permission object to the cache.
     *
     * @param permission the permission object to cache
     */
    private static IPermission putCachePermission(@NonNull IPermission permission) {
        PERMISSION_CACHE_MAP.put(permission.getPermissionName(), permission);
        return permission;
    }

    /**
     * Returns the read installed apps permission.
     * This vendor specific permission is used by some Chinese phone manufacturers.
     * Apps may still need to declare {@code QUERY_ALL_PACKAGES} or a {@code <queries>} block,
     * and Google Play policy may restrict its use.
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
     * Returns the full-screen intent permission.
     * Introduced in Android 14. Review Google Play policy carefully before requesting it.
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
     * Returns the exact alarm permission.
     * Introduced in Android 12. This permission is granted by default on some devices and can
     * still be revoked by the user. Apps should only request it when exact alarms are essential.
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
     * Returns the manage media permission.
     * Introduced in Android 12. It does not grant direct read or write access. Instead, it lets
     * trusted apps avoid repeated confirmation dialogs when modifying media files.
     */
    @NonNull
    public static IPermission getManageMediaPermission() {
        IPermission permission = getCachePermission(ManageMediaPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new ManageMediaPermission());
    }

    /**
     * Returns the manage external storage permission.
     * Introduced in Android 11. Apps that also support older Android versions should still declare
     * {@link PermissionNames#READ_EXTERNAL_STORAGE} and {@link PermissionNames#WRITE_EXTERNAL_STORAGE}.
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
     * Returns the installation unknown apps permission.
     * Introduced in Android 8. On some Android 11 devices, changing this permission may restart
     * the app. That behavior was improved on Android 12.
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
     * Returns the picture-in-picture permission.
     * Introduced in Android 8. This permission is often granted by default and can still be
     * revoked manually.
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
     * Returns the system alert window permission.
     * Introduced in Android 6, with some vendor specific support on older devices.
     */
    @NonNull
    public static IPermission getSystemAlertWindowPermission() {
        return getSystemAlertWindowPermission(false);
    }

    /**
     * Returns the system alert window permission.
     * @param forceXiaomi when true, on eligible Xiaomi devices, navigates to the Xiaomi permission page
     *                    and additionally checks Xiaomi-specific ops (background start, show when locked, popup window).
     */
    @NonNull
    public static IPermission getSystemAlertWindowPermission(boolean forceXiaomi) {
        if (!forceXiaomi) {
            IPermission permission = getCachePermission(SystemAlertWindowPermission.PERMISSION_NAME);
            if (permission != null) {
                return permission;
            }
        }
        return putCachePermission(new SystemAlertWindowPermission(forceXiaomi));
    }

    /** Returns the write system settings permission. */
    @SuppressWarnings("unused")
    @NonNull
    public static IPermission getWriteSettingsPermission() {
        IPermission permission = getCachePermission(WriteSettingsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new WriteSettingsPermission());
    }

    /** Returns the ignore battery optimizations permission. */
    @NonNull
    public static IPermission getRequestIgnoreBatteryOptimizationsPermission() {
        IPermission permission = getCachePermission(RequestIgnoreBatteryOptimizationsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new RequestIgnoreBatteryOptimizationsPermission());
    }

    /** Returns the Do Not Disturb access permission. */
    @NonNull
    public static IPermission getAccessNotificationPolicyPermission() {
        IPermission permission = getCachePermission(AccessNotificationPolicyPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new AccessNotificationPolicyPermission());
    }

    /** Returns the usage access permission. */
    @NonNull
    public static IPermission getPackageUsageStatsPermission() {
        IPermission permission = getCachePermission(PackageUsageStatsPermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new PackageUsageStatsPermission());
    }

    /**
     * Returns the notification listener permission.
     *
     * @param notificationListenerServiceClass the notification listener service class
     */
    @NonNull
    public static IPermission getBindNotificationListenerServicePermission(@NonNull Class<? extends NotificationListenerService> notificationListenerServiceClass) {
        // This object is not stored in the cache collection, because it carries specific parameters, only parameterless objects can be cached
        return new BindNotificationListenerServicePermission(notificationListenerServiceClass);
    }

    /** Returns the VPN permission. */
    @NonNull
    public static IPermission getBindVpnServicePermission() {
        IPermission permission = getCachePermission(BindVpnServicePermission.PERMISSION_NAME);
        if (permission != null) {
            return permission;
        }
        return putCachePermission(new BindVpnServicePermission());
    }

    /**
     * Returns the notification service permission for the given channel.
     *
     * @param channelId the notification channel id
     */
    @NonNull
    public static IPermission getNotificationServicePermission(@NonNull String channelId) {
        // This object is not stored in the cache collection, because it carries specific parameters, only parameterless objects can be cached
        return new NotificationServicePermission(channelId);
    }

    /**
     * Same as
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
     * Returns the accessibility service permission.
     *
     * @param accessibilityServiceClass the accessibility service class
     */
    @NonNull
    public static IPermission getBindAccessibilityServicePermission(@NonNull Class<? extends AccessibilityService> accessibilityServiceClass) {
        return new BindAccessibilityServicePermission(accessibilityServiceClass);
    }

    /**
     * Returns the device admin permission.
     *
     * @param deviceAdminReceiverClass the device admin receiver class
     * @param extraAddExplanation extra explanation shown when requesting device admin access
     */
    @NonNull
    public static IPermission getBindDeviceAdminPermission(@NonNull Class<? extends DeviceAdminReceiver> deviceAdminReceiverClass, @Nullable String extraAddExplanation) {
        return new BindDeviceAdminPermission(deviceAdminReceiverClass, extraAddExplanation);
    }

    /**
     * Same as
     */
    @NonNull
    public static IPermission getBindDeviceAdminPermission(@NonNull Class<? extends DeviceAdminReceiver> deviceAdminReceiverClass) {
        return new BindDeviceAdminPermission(deviceAdminReceiverClass, null);
    }

    /* ------------------------------------ Decorative separator ------------------------------------ */

    /**
     * Partial photo and video access permission, introduced in Android 14.0
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
     * Post notifications permission, introduced in Android 13.0
         * For backward compatibility, the framework automatically adds this on older Android devices {@link PermissionLists#getNotificationServicePermission()} permissionfor runtime requests, so you do not need to add it manually
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
     * Wi-Fi permission, introduced in Android 13.0
         * you need to add android:usesPermissionFlags="neverForLocation" attribute(which means not deriving the device location)
     * otherwisecauses nolocation permission case Wi-Fi device, , below manifest permissiondeclare , please below declare
     * <uses-permission android:name="android.permission.NEARBY_Wi-Fi_DEVICES" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
         * compatibility Android 13 belowversion, you need to declare in the manifest file {@link PermissionNames#ACCESS_FINE_LOCATION} permission
     * the framework automatically adds this on older Android devices {@link PermissionLists#getAccessFineLocationPermission()} permissionfor runtime requests, so you do not need to add it manually
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
     * Background sensors permission, introduced in Android 13.0
         * Please note the following:
     * 1. Once you request this permission, during authorization , you need to choose "Allow all the time", instead of "Allow only while using the app"
     * 2. If App foreground state sensorsfeature, please request permission(background sensors permission)
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
     * Read images permission, introduced in Android 13.0
         * compatibility Android 13 belowversion, you need to declare {@link PermissionNames#READ_EXTERNAL_STORAGE} permission
     * the framework automatically adds this on older Android devices {@link PermissionLists#getReadExternalStoragePermission()} permissionfor runtime requests, so you do not need to add it manually
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
     * Read videos permission, introduced in Android 13.0
         * compatibility Android 13 belowversion, you need to declare {@link PermissionNames#READ_EXTERNAL_STORAGE} permission
     * the framework automatically adds this on older Android devices {@link PermissionLists#getReadExternalStoragePermission()} permissionfor runtime requests, so you do not need to add it manually
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
     * Read audio permission, introduced in Android 13.0
         * compatibility Android 13 belowversion, you need to declare {@link PermissionNames#READ_EXTERNAL_STORAGE} permission
     * the framework automatically adds this on older Android devices {@link PermissionLists#getReadExternalStoragePermission()} permissionfor runtime requests, so you do not need to add it manually
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
     * Bluetooth scan permission, introduced in Android 12.0
         * you need to add android:usesPermissionFlags="neverForLocation" attribute(which means not deriving the device location)
     * otherwisecauses nolocation permission case device, , below manifest permissiondeclare , please below declare
     * <uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
         * compatibility Android 12 belowversion, you need to declare in the manifest file {@link Manifest.permission#BLUETOOTH_ADMIN} and {@link PermissionNames#ACCESS_FINE_LOCATION} permission
     * the framework automatically adds this on older Android devices {@link PermissionLists#getAccessFineLocationPermission()} permissionfor runtime requests, so you do not need to add it manually
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
     * Bluetooth connect permission, introduced in Android 12.0
         * compatibility Android 12 belowversion, you need to declare {@link Manifest.permission#BLUETOOTH} permission
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
     * Bluetooth advertise permission, introduced in Android 12.0
         * current device broadcast, device need to permission
     * compatibility Android 12 belowversion, you need to declare {@link Manifest.permission#BLUETOOTH_ADMIN} permission
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
     * Background location permission, introduced in Android 10.0
         * Please note the following:
     * 1. Once you request this permission, during authorization , you need to choose "Allow all the time", instead of "Allow only while using the app"
     * 2. If App foreground state feature, no background , please request permission
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
     * Returns the permission object (introduced in Android 10.0).
         * Please note the following: Android 10 below need tosensors(BODY_SENSORS)permission
     * GitHub issue: https://github.com/getActivity/XXPermissions/issues/150
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
     * Access media location permission, introduced in Android 10.0
         * Please note the following: If this permission request succeeds cannot readphotos , need to requeststoragepermission, below case:
         * 1. If storage case :
     * 1) Ifproject targetSdkVersion <= 32 need torequest {@link PermissionLists#getReadExternalStoragePermission()}
     * 2) Ifproject targetSdkVersion >= 33 need torequest {@link PermissionLists#getReadMediaImagesPermission()} or
     * {@link PermissionLists#getReadMediaVideoPermission()}, need togranted , cannot granted
         * 2. Ifno storage case :
     * 1) Ifproject targetSdkVersion <= 29 need torequest {@link PermissionLists#getReadExternalStoragePermission()}
     * 2) Ifproject targetSdkVersion >= 30 need torequest {@link PermissionLists#getManageExternalStoragePermission()}
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
     * Returns the accept handover permission.
     * Introduced in Android 9. On some devices that do not support phone calls, requests may
     * fail immediately, so callers should handle request failure normally.
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
     * Returns the read phone numbers permission.
     * Introduced in Android 8. On older Android versions, compatibility still depends on
     * {@link PermissionNames#READ_PHONE_STATE}, which the framework adds automatically when needed.
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
     * Returns the answer phone calls permission.
     * Introduced in Android 8. On devices without phone support, requests may fail immediately,
     * so callers should handle request failure normally.
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
     * read external storage permission
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
     * write external storage permission(note: permission targetSdk >= Android 11 Android 11 above devices effect, please storagefeature permission request)
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
     * camera permission
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
     * microphone permission
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
     * precise location permission
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
     * approximate location permission
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
     * read contacts permission
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
     * write contacts permission
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
     * get accounts permission
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
     * read calendar permission
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
     * write calendar permission
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
     * read phone state permission, Please note the following:
         * 1. permission devices , because system app permission
     * so request permissionlaterno authorization dialog, it means thatdirectlycallbackauthorization failed
     * please , not Bug, not Bug, not Bug, it means that
     * case : has iQOO devicesget permission, add to the manifest filebelow permission (here , has )
     * <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
     * GitHub issue: https://github.com/getActivity/XXPermissions/issues/98
         * 2. permission devicesrequest directlythrough , systemno authorization dialog, noauthorization
     * not Bug, it means thatsystem , ,
     * GitHub issue: https://github.com/getActivity/XXPermissions/issues/369
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
     * call phone permission
         * need tonote: permission phone calls device(For example: Xiaomi 5)request, systemwill directlycallbackfailure, has request, please permission requestfailure case
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
     * read call log permission
         * need tonote: permission phone calls device(For example: Xiaomi 5)request, systemwill directlycallbackfailure, has request, please permission requestfailure case
     */
    @NonNull
    public static IPermission getReadCallLogPermission() {
        String permissionName = PermissionNames.READ_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // note: Android 9.0 , call logrelated permissionalready permission group , Android 9.0 earlier, call logpermission phone permission group
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * write call log permission
         * need tonote: permission phone calls device(For example: Xiaomi 5)request, systemwill directlycallbackfailure, has request, please permission requestfailure case
     */
    @NonNull
    public static IPermission getWriteCallLogPermission() {
        String permissionName = PermissionNames.WRITE_CALL_LOG;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // note: Android 9.0 , call logrelated permissionalready permission group , Android 9.0 earlier, call logpermission phone permission group
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * add voicemail permission
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
     * use SIP permission
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
     * process outgoing calls permission
         * need tonote: permission phone calls device(For example: Xiaomi 5)request, systemwill directlycallbackfailure, has request, please permission requestfailure case
         * @deprecated Android 10 already , please : https://developer.android.google.cn/reference/android/Manifest.permission?hl=zh_cn#PROCESS_OUTGOING_CALLS
     */
    @NonNull
    public static IPermission getProcessOutgoingCallsPermission() {
        String permissionName = PermissionNames.PROCESS_OUTGOING_CALLS;
        IPermission permission = getCachePermission(permissionName);
        if (permission != null) {
            return permission;
        }
        // note: Android 9.0 , call logrelated permissionalready permission group , Android 9.0 earlier, call logpermission phone permission group
        String permissionGroup = PermissionVersion.isAndroid9() ? PermissionGroups.CALL_LOG : PermissionGroups.PHONE;
        return putCachePermission(new StandardDangerousPermission(permissionName, permissionGroup, PermissionVersion.ANDROID_6));
    }

    /**
     * body sensors permission
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
     * send SMS permission
         * need tonote: permission SMS device(For example: Xiaomi 5)request, systemwill directlycallbackfailure, has request, please permission requestfailure case
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
     * receive SMS permission
         * need tonote: permission SMS device(For example: Xiaomi 5)request, systemwill directlycallbackfailure, has request, please permission requestfailure case
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
     * read SMS permission
         * need tonote: permission SMS device(For example: Xiaomi 5)request, systemwill directlycallbackfailure, has request, please permission requestfailure case
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
     * receive WAP push permission
         * need tonote: permission SMS device(For example: Xiaomi 5)request, systemwill directlycallbacksuccess, , has request, also permission requestfailure case
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
     * receive MMS permission
         * need tonote: permission SMS device(For example: Xiaomi 5)request, systemwill directlycallbacksuccess, , has request, also permission requestfailure case
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

    /* ------------------------------------ Decorative separator ------------------------------------ */

    /**
     * Returns the permission object (introduced in Android 15.0).
         * compatibility Android 15 belowversion, you need to declare {@link PermissionNames#BODY_SENSORS_BACKGROUND} permission
     * the framework automatically adds this on older Android devices {@link PermissionLists#getReadHealthDataInBackgroundPermission()} ()} permissionfor runtime requests, so you do not need to add it manually
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
     * Returns the permission object (introduced in Android 15.0).
         * Health Connect readgrantedpermission 30 .If appread 30 earlier , please need torequest permission, relateddocumentation link:
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 15.0).
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
     * Returns the permission object (introduced in Android 15.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
         * compatibility Android 14 belowversion, you need to declare {@link PermissionNames#BODY_SENSORS} permission
     * the framework automatically adds this on older Android devices {@link PermissionLists#getBodySensorsPermission()} permissionfor runtime requests, so you do not need to add it manually
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 15.0).
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
     * Returns the permission object (introduced in Android 15.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 15.0).
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
     * Returns the permission object (introduced in Android 15.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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
     * Returns the permission object (introduced in Android 14.0).
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

    /* ------------------------------------ Decorative separator ------------------------------------ */

    /**
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
     * Returns the permission object (introduced in Android 16.0).
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
