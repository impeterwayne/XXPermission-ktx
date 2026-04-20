package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 * Describes how permission requests are presented to the user.
 */
public interface OnPermissionDescription {

    /**
     * Asks whether the permission request should continue.
     *
     * @param requestList the requested permissions
     * @param continueRequestRunnable continues the request flow
     * @param breakRequestRunnable stops the request flow
     */
    void askWhetherRequestPermission(@NonNull Activity activity,
                                     @NonNull List<IPermission> requestList,
                                     @NonNull Runnable continueRequestRunnable,
                                     @NonNull Runnable breakRequestRunnable);

    /**
     * Called when a permission request starts.
     *
     * @param requestList the requested permissions
     */
    void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<IPermission> requestList);

    /**
     * Called when a permission request finishes.
     *
     * @param requestList the requested permissions
     */
    void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<IPermission> requestList);
}
