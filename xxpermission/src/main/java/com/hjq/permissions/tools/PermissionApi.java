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
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/12/31
 *    desc   : Permission helper utilities
 */
public final class PermissionApi {

    /**
     * Determine whether a permission belongs to the Health permissions namespace.
     */
    public static boolean isHealthPermission(@NonNull IPermission permission) {
        return permission.getPermissionName().startsWith("android.permission.health.");
    }

    /**
     * Check whether a permission list contains any permission that must be granted
     * via the startActivityForResult channel.
     */
    public static boolean containsPermissionByStartActivityForResult(@NonNull Context context, @Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (IPermission permission : permissions) {
            if (permission.getPermissionChannel(context) == PermissionChannel.START_ACTIVITY_FOR_RESULT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether all permissions in the list are granted.
     */
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

    /**
     * Return only the granted permissions from the list.
     */
    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<IPermission> grantedList = new ArrayList<>(permissions.size());
        for (IPermission permission : permissions) {
            if (permission.isGrantedPermission(context)) {
                grantedList.add(permission);
            }
        }
        return grantedList;
    }

    /**
     * Return only the denied permissions from the list.
     */
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
     * Check whether any permission in the group has been permanently denied
     * (“Don’t ask again”).
     *
     * @param permissions permissions being requested
     */
    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull List<IPermission> permissions) {
        for (IPermission permission : permissions) {
            if (permission.isDoNotAskAgainPermission(activity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Choose the most appropriate settings Intent(s) based on the given permissions.
     */
    @NonNull
    public static List<Intent> getBestPermissionSettingIntent(@NonNull Context context, @Nullable List<IPermission> permissions, boolean skipRequest) {
        // If there are no failing permissions, fall back to the common settings page.
        if (permissions == null || permissions.isEmpty()) {
            return PermissionSettingPage.getCommonPermissionSettingIntent(context);
        }

        // Create a new list to avoid mutating the caller's list.
        List<IPermission> realPermissions = new ArrayList<>(permissions);
        for (IPermission permission : permissions) {
            if (permission.getFromAndroidVersion(context) > PermissionVersion.getCurrentVersion()) {
                // If the permission only appears on higher Android versions, exclude it.
                realPermissions.remove(permission);
                continue;
            }

            List<IPermission> oldPermissions = permission.getOldPermissions(context);
            // 1) If the old-permissions list is not empty and the current permission uses
            //    START_ACTIVITY_FOR_RESULT, remove its corresponding old permissions.
            //    Example: MANAGE_EXTERNAL_STORAGE -> READ/WRITE_EXTERNAL_STORAGE
            // 2) If the old-permissions list is not empty and any of those old permissions
            //    require START_ACTIVITY_FOR_RESULT, remove those old permissions as well.
            //    Example: POST_NOTIFICATIONS -> NOTIFICATION_SERVICE
            if (oldPermissions != null && !oldPermissions.isEmpty() &&
                    (permission.getPermissionChannel(context) == PermissionChannel.START_ACTIVITY_FOR_RESULT ||
                            containsPermissionByStartActivityForResult(context, oldPermissions))) {
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
            // Compare whether these two Intent lists are identical.
            if (!PermissionUtils.equalsIntentList(currentPermissionIntentList, prePermissionIntentList)) {
                // Stop if they differ.
                break;
            }
            // Carry the current list forward to avoid recomputation and improve performance.
            prePermissionIntentList = currentPermissionIntentList;

            // If all lists are identical, just return the current one.
            if (i == realPermissions.size() - 1) {
                return currentPermissionIntentList;
            }
        }
        return PermissionSettingPage.getCommonPermissionSettingIntent(context);
    }

    /**
     * Add legacy (old) permissions based on any newer permissions present.
     */
    public static synchronized void addOldPermissionsByNewPermissions(@NonNull Context context, @NonNull List<IPermission> requestList) {
        // Start index at -1 so that ++index is 0 in the first loop iteration.
        int index = -1;
        // ++index is pre-increment (increment then use the value).
        // index++ is post-increment (use the value then increment).
        while (++index < requestList.size()) {
            IPermission permission = requestList.get(index);
            // If the current Android version is >= the permission’s introduction version,
            // we don’t need to add older permissions for this one.
            if (PermissionVersion.getCurrentVersion() >= permission.getFromAndroidVersion(context)) {
                continue;
            }
            // Lookup the legacy permissions corresponding to this new permission.
            List<IPermission> oldPermissions = permission.getOldPermissions(context);
            if (oldPermissions == null || oldPermissions.isEmpty()) {
                continue;
            }
            for (IPermission oldPermission : oldPermissions) {
                // Skip if it’s already in the request list.
                if (PermissionUtils.containsPermission(requestList, oldPermission)) {
                    continue;
                }
                // Insert right after the new permission to preserve the caller’s order.
                requestList.add(++index, oldPermission);
            }
        }
    }

    /**
     * Get the maximum request interval time from a collection of permissions.
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
     * Get the maximum result wait time from a collection of permissions.
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
