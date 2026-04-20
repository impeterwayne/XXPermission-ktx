package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.core.PermissionRequestMainLogic;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactory;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 * Intercepts permission requests before and after the framework runs them.
 */
public interface OnPermissionInterceptor {

    /**
     * Called before the framework starts requesting permissions.
     * You can show your own dialog here.
     * This method is not called when every requested permission is already granted.
     *
     * @param requestList the requested permissions
     * @param fragmentFactory the fragment factory used to perform the request
     * @param permissionDescription the active permission description handler
     * @param callback the final result callback
     */
    default void onRequestPermissionStart(@NonNull Activity activity,
                                          @NonNull List<IPermission> requestList,
                                          @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                          @NonNull OnPermissionDescription permissionDescription,
                                          @Nullable OnPermissionCallback callback) {
        dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback);
    }

    /**
     * Called after the permission request flow finishes.
     *
     * @param grantedList the permissions that were granted
     * @param deniedList the permissions that were denied
     * @param skipRequest whether the framework skipped the request flow
     * @param callback the final result callback
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
     * Dispatches the permission request through the main request logic.
     *
     * @param requestList the requested permissions
     * @param callback the final result callback
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
