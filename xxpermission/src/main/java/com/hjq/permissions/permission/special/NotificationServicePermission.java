package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Notification permission class
 */
public final class NotificationServicePermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.NOTIFICATION_SERVICE;

    private static final String OP_POST_NOTIFICATION_FIELD_NAME = "OP_POST_NOTIFICATION";
    private static final int OP_POST_NOTIFICATION_DEFAULT_VALUE = 11;

    public static final Parcelable.Creator<NotificationServicePermission> CREATOR = new Parcelable.Creator<NotificationServicePermission>() {

        @Override
        public NotificationServicePermission createFromParcel(Parcel source) {
            return new NotificationServicePermission(source);
        }

        @Override
        public NotificationServicePermission[] newArray(int size) {
            return new NotificationServicePermission[size];
        }
    };

    @Nullable
    private final String mChannelId;

    public NotificationServicePermission() {
        this((String) null);
    }

    public NotificationServicePermission(@Nullable String channelId) {
        mChannelId = channelId;
    }

    private NotificationServicePermission(Parcel in) {
        this(in.readString());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mChannelId);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_4_4;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid4_4()) {
            return true;
        }
        if (!PermissionVersion.isAndroid7()) {
            return checkOpPermission(context, OP_POST_NOTIFICATION_FIELD_NAME, OP_POST_NOTIFICATION_DEFAULT_VALUE, true);
        }

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        // Although this SystemService is never null, we still do defensive programming just in case
        if (notificationManager == null) {
            return checkOpPermission(context, OP_POST_NOTIFICATION_FIELD_NAME, OP_POST_NOTIFICATION_DEFAULT_VALUE, true);
        }
        if (!notificationManager.areNotificationsEnabled()) {
            return false;
        }
        if (TextUtils.isEmpty(mChannelId) || !PermissionVersion.isAndroid8()) {
            return true;
        }
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(mChannelId);
        return notificationChannel != null && notificationChannel.getImportance() != NotificationManager.IMPORTANCE_NONE;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(8);
        Intent intent;

        if (PermissionVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            // Add the app’s package name parameter
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = null;
            // Although this SystemService is never null, we still do defensive programming just in case
            if (notificationManager != null && !TextUtils.isEmpty(mChannelId)) {
                notificationChannel = notificationManager.getNotificationChannel(mChannelId);
            }
            // Preconditions for setting the notification channel id parameter:
            // 1. The notification channel still exists
            // 2. Notification permission is currently granted
            if (notificationChannel != null && notificationManager.areNotificationsEnabled()) {
                // Modify the action to point to the specific notification channel’s page
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                // Specify the notification channel id
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannel.getId());
                if (PermissionVersion.isAndroid11()) {
                    // In higher versions, the system first tries to find the notification channel using conversation id,
                    // if not found, then falls back to the channel id
                    intent.putExtra(Settings.EXTRA_CONVERSATION_ID, notificationChannel.getConversationId());
                }
                intentList.add(intent);
            }

            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intentList.add(intent);
        }

        if (PermissionVersion.isAndroid5()) {
            intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            intentList.add(intent);
        }

        if (PermissionVersion.isAndroid13()) {
            intent = new Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS);
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

    @Nullable
    public String getChannelId() {
        return mChannelId;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            // If the project already targets Android 13, then the POST_NOTIFICATIONS permission must be added in the manifest,
            // otherwise it will not be possible to request notification permissions
            PermissionManifestInfo postNotificationsPermission = findPermissionInfoByList(permissionInfoList, PermissionNames.POST_NOTIFICATIONS);
            checkPermissionRegistrationStatus(postNotificationsPermission, PermissionNames.POST_NOTIFICATIONS, PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION);
        }
    }
}
