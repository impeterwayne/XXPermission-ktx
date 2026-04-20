package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 * Default implementation of permission description.
 */
final class DefaultPermissionDescription implements OnPermissionDescription {

    @Override
    public void askWhetherRequestPermission(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull Runnable continueRequestRunnable,
                                            @NonNull Runnable breakRequestRunnable) {
        // Continue executing the request task
        continueRequestRunnable.run();
    }

    @Override
    public void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // default implementation ignored
    }

    @Override
    public void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // default implementation ignored
    }
}