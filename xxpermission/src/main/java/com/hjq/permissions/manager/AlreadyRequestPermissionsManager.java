package com.hjq.permissions.manager;

import androidx.annotation.Nullable;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/13
 *    desc   : Manager class for tracking already requested permissions
 */
public final class AlreadyRequestPermissionsManager {

    /** List of permissions that have already been requested */
    private static final List<String> ALREADY_REQUEST_PERMISSIONS_LIST = new ArrayList<>();

    /** Private constructor to prevent instantiation */
    private AlreadyRequestPermissionsManager() {
        // default implementation ignored
    }

    /**
     * Add permissions to the list of already requested permissions
     *
     * @param permissions list of permissions to mark as already requested
     */
    public static void addAlreadyRequestPermissions(@Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        for (IPermission permission : permissions) {
            String permissionName = permission.getPermissionName();
            if (PermissionUtils.containsPermission(ALREADY_REQUEST_PERMISSIONS_LIST, permissionName)) {
                continue;
            }
            ALREADY_REQUEST_PERMISSIONS_LIST.add(permissionName);
        }
    }

    /**
     * Check whether a permission has already been requested
     *
     * @param permission the permission to check
     * @return true if the permission has already been requested, false otherwise
     */
    public static boolean isAlreadyRequestPermissions(@Nullable IPermission permission) {
        if (permission == null) {
            return false;
        }
        return PermissionUtils.containsPermission(ALREADY_REQUEST_PERMISSIONS_LIST, permission.getPermissionName());
    }
}
