package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactory;
import com.hjq.permissions.core.PermissionRequestMainLogic;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2020/12/26
 *    desc   : Permission request interceptor
 *
 * Acts as a customizable interception point in the permission request flow.
 * You may hook into the process to show dialogs, log events, or otherwise
 * bend the permission gods to your will before Android pops up its own dialogs.
 */
public interface OnPermissionInterceptor {

    /**
     * Triggered when starting a permission request.
     * <p>
     * Developers can intercept here to show a dialog or explanation to the user
     * before the actual system permission request is fired.
     * (If the user has already granted the permissions, this will not be invoked.)
     *
     * @param activity              the current Activity
     * @param requestList           list of requested permissions
     * @param fragmentFactory       factory for creating permission fragments
     * @param permissionDescription permission description handler
     * @param callback              result callback (nullable)
     */
    default void onRequestPermissionStart(@NonNull Activity activity,
                                          @NonNull List<IPermission> requestList,
                                          @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                          @NonNull OnPermissionDescription permissionDescription,
                                          @Nullable OnPermissionCallback callback) {
        dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback);
    }

    /**
     * Called when the permission request flow has finished.
     *
     * @param activity     the current Activity
     * @param skipRequest  true if the request flow was skipped (for example, all permissions already granted)
     * @param requestList  list of requested permissions
     * @param grantedList  list of granted permissions
     * @param deniedList   list of denied permissions
     * @param callback     result callback (nullable)
     */
    default void onRequestPermissionEnd(@NonNull Activity activity, boolean skipRequest,
                                        @NonNull List<IPermission> requestList,
                                        @NonNull List<IPermission> grantedList,
                                        @NonNull List<IPermission> deniedList,
                                        @Nullable OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onResult(grantedList, deniedList);
    }

    /**
     * Dispatches the actual permission request logic.
     * <p>
     * Think of this as releasing the hounds: the request engine runs from here.
     *
     * @param activity              the current Activity
     * @param requestList           list of requested permissions
     * @param fragmentFactory       factory for creating permission fragments
     * @param permissionDescription permission description handler
     * @param callback              result callback (nullable)
     */
    default void dispatchPermissionRequest(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                           @NonNull OnPermissionDescription permissionDescription,
                                           @Nullable OnPermissionCallback callback) {
        new PermissionRequestMainLogic(activity, requestList, fragmentFactory, this, permissionDescription, callback)
                .request();
    }
}
