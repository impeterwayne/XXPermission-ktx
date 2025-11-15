package com.hjq.permissions;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/01
 *    desc   : Default implementation of {@link OnPermissionDescription}.
 */
final class DefaultPermissionDescription implements OnPermissionDescription {

    @Override
    public void askWhetherRequestPermission(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull Runnable continueRequestRunnable,
                                            @NonNull Runnable breakRequestRunnable) {
        // Continue executing the permission request task immediately
        continueRequestRunnable.run();
    }

    @Override
    public void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // Default implementation ignored
    }

    @Override
    public void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // Default implementation ignored
    }
}
