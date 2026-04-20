package com.hjq.permissions.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared helper methods for permission requests.
 */
public final class PermissionApi {

    /** Returns whether the permission belongs to Health Connect. */
    public static boolean isHealthPermission(@NonNull IPermission permission) {
        return permission.getPermissionName().startsWith("android.permission.health.");
    }

    /**
     * Returns whether the permission list contains a permission that must be granted
     * through an activity based flow.
     */
    public static boolean containsPermissionByStartActivity(@NonNull Context context, @Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (IPermission permission : permissions) {
            if (permission.getPermissionChannel(context) == PermissionChannel.START_ACTIVITY) {
                return true;
            }
        }
        return false;
    }

    /** Returns whether every permission in the list is granted. */
    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        if (permissions.isEmpty()) {
            return false;
        }

        for (IPermission permission : permissions) {
            if (!permission.isGrantedPermission(context)) {
                return false;
            }
        }

        return true;
    }

    /** Returns the permissions that are already granted. */
    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<IPermission> grantedList = new ArrayList<>(permissions.size());
        for (IPermission permission : permissions) {
            if (permission.isGrantedPermission(context)) {
                grantedList.add(permission);
            }
        }
        return grantedList;
    }

    /** Returns the permissions that are still denied. */
    public static List<IPermission> getDeniedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<IPermission> deniedList = new ArrayList<>(permissions.size());
        for (IPermission permission : permissions) {
            if (!permission.isGrantedPermission(context)) {
                deniedList.add(permission);
            }
        }
        return deniedList;
    }

    /**
     * Returns whether any permission in the list has been permanently denied.
     *
     * @param permissions the requested permissions
     */
    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull List<IPermission> permissions) {
        for (IPermission permission : permissions) {
            if (permission.isDoNotAskAgainPermission(activity)) {
                return true;
            }
        }
        return false;
    }

    /** Chooses the most suitable settings page intents for the given permissions. */
    @NonNull
    public static List<Intent> getBestPermissionSettingIntent(@NonNull Context context, @Nullable List<IPermission> permissions, boolean skipRequest) {
        // If the failed permissions do not contain a special permission
        if (permissions == null || permissions.isEmpty()) {
            return PermissionSettingPage.getCommonPermissionSettingIntent(context);
        }

        // Create a new collection object to avoid conflicts caused by reusing the same object
        List<IPermission> realPermissions = new ArrayList<>(permissions);
        for (IPermission permission : permissions) {
            if (permission.getFromAndroidVersion(context) > PermissionVersion.getSdkVersion()) {
                // If the current permission only exists on higher Android versions, remove it
                realPermissions.remove(permission);
                continue;
            }

            List<IPermission> oldPermissions = permission.getOldPermissions(context);
            // 1. If the legacy permission list is not empty and the current permission must be granted through startActivityForResult, remove its corresponding legacy permissions
            // For example: MANAGE_EXTERNAL_STORAGE -> READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE
            // 2. If the legacy permission list is not empty and the legacy permissions mapped from the current permission includes one that must be granted through startActivityForResult, remove those legacy permissions
            // For example: POST_NOTIFICATIONS -> NOTIFICATION_SERVICE
            if (oldPermissions != null && !oldPermissions.isEmpty() &&
                (permission.getPermissionChannel(context) == PermissionChannel.START_ACTIVITY ||
                    containsPermissionByStartActivity(context, oldPermissions))) {
                realPermissions.removeAll(oldPermissions);
            }
        }

        if (realPermissions.isEmpty()) {
            return PermissionSettingPage.getCommonPermissionSettingIntent(context);
        }

        if (realPermissions.size() == 1) {
            return realPermissions.get(0).getPermissionSettingIntents(context, skipRequest);
        }

        List<Intent> prePermissionIntentList = realPermissions.get(0).getPermissionSettingIntents(context, skipRequest);
        for (int i = 1; i < realPermissions.size(); i++) {
            List<Intent> currentPermissionIntentList = realPermissions.get(i).getPermissionSettingIntents(context, skipRequest);
            // Compare whether the contents of these two Intent lists are the same.
            if (!PermissionUtils.equalsIntentList(currentPermissionIntentList, prePermissionIntentList)) {
                // If they are different, stop the loop.
                break;
            }
            // Record the current permission list so it becomes the previous one in the next loop, which avoids repeated lookups and saves work.
            prePermissionIntentList = currentPermissionIntentList;

            // If all Intent lists in the collection are the same, navigate directly with the current Intent list.
            if (i == realPermissions.size() - 1) {
                return currentPermissionIntentList;
            }
        }
        return PermissionSettingPage.getCommonPermissionSettingIntent(context);
    }

    /**
     * Add legacy permissions based on the new permission.
     */
    public static synchronized void addOldPermissionsByNewPermissions(@NonNull Context context, @NonNull List<IPermission> requestList) {
        // Set index to -1 here so that when the loop below runs, ++index becomes 0 on the first iteration.
        int index = -1;
        // ++index is pre-increment, which adds 1 to index before returning the new value.
        // index++ is post-increment, which returns the current value first and then adds 1.
        while (++index < requestList.size()) {
            IPermission permission = requestList.get(index);
            // If the current Android version is higher than the version where the permission was introduced, no legacy permission needs to be added on this device.
            if (PermissionVersion.getSdkVersion() >= permission.getFromAndroidVersion(context)) {
                continue;
            }
            // Look up the corresponding legacy permissions from the new permission.
            List<IPermission> oldPermissions = permission.getOldPermissions(context);
            if (oldPermissions == null || oldPermissions.isEmpty()) {
                continue;
            }
            for (IPermission oldPermission : oldPermissions) {
                // If the request list already contains this permission, skip it instead of adding it again.
                if (PermissionUtils.containsPermission(requestList, oldPermission)) {
                    continue;
                }
                // index + 1 appends the legacy permission after the new-version permission, which preserves the original request order
                requestList.add(++index, oldPermission);
            }
        }
    }

    /**
     * Get the maximum interval time from the permission collection.
     */
    public static int getMaxIntervalTimeByPermissions(@NonNull Context context, @Nullable List<IPermission> permissions) {
        if (permissions == null) {
            return 0;
        }
        int maxWaitTime = 0;
        for (IPermission permission : permissions) {
            int time = permission.getRequestIntervalTime(context);
            if (time == 0) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }

    /**
     * Get the maximum callback wait time from the permission collection.
     */
    public static int getMaxWaitTimeByPermissions(@NonNull Context context, @Nullable List<IPermission> permissions) {
        if (permissions == null) {
            return 0;
        }
        int maxWaitTime = 0;
        for (IPermission permission : permissions) {
            int time = permission.getResultWaitTime(context);
            if (time == 0) {
                continue;
            }
            maxWaitTime = Math.max(maxWaitTime, time);
        }
        return maxWaitTime;
    }
}
