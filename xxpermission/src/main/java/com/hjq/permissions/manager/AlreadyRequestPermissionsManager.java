package com.hjq.permissions.manager;

import androidx.annotation.Nullable;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for already requested permissions.
 */
public final class AlreadyRequestPermissionsManager {

    /** Permissions that have already been requested */
    private static final List<String> ALREADY_REQUEST_PERMISSIONS_LIST = new ArrayList<>();

    /** Private constructor */
    private AlreadyRequestPermissionsManager() {
        // default implementation ignored
    }

    /**
     * Add already requested permissions
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
     */
    public static boolean isAlreadyRequestPermissions(@Nullable IPermission permission) {
        if (permission == null) {
            return false;
        }
        return PermissionUtils.containsPermission(ALREADY_REQUEST_PERMISSIONS_LIST, permission.getPermissionName());
    }
}