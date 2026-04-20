package com.hjq.permissions;

import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 * Callback for permission request results.
 */
public interface OnPermissionCallback {

    /**
     * Called when the permission request finishes.
     *
     * @param grantedList the permissions that were granted
     * @param deniedList the permissions that were denied
     */
    void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList);
}
