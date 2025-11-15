package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : Permission description interface
 *
 * Provides hook points to explain and wrap the permission request flow.
 */
public interface OnPermissionDescription {

    /**
     * Ask whether to initiate the permission request.
     *
     * @param activity                 the current Activity
     * @param requestList              list of permissions being requested
     * @param continueRequestRunnable  runnable to continue the request
     * @param breakRequestRunnable     runnable to cancel the request
     */
    void askWhetherRequestPermission(@NonNull Activity activity,
                                     @NonNull List<IPermission> requestList,
                                     @NonNull Runnable continueRequestRunnable,
                                     @NonNull Runnable breakRequestRunnable);

    /**
     * Called before starting the permission request.
     *
     * @param activity     the current Activity
     * @param requestList  list of permissions being requested
     */
    void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<IPermission> requestList);

    /**
     * Called after the permission request has finished.
     *
     * @param activity     the current Activity
     * @param requestList  list of permissions being requested
     */
    void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<IPermission> requestList);
}
