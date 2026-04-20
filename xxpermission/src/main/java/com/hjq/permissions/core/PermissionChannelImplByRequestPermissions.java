package com.hjq.permissions.core;

import android.app.Activity;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.AlreadyRequestPermissionsManager;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Permission request implementation(implemented through {@link android.app.Activity#requestPermissions(String[], int)}).
 */
public final class PermissionChannelImplByRequestPermissions extends PermissionChannelImpl {

    public PermissionChannelImplByRequestPermissions(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        super(fragmentMethod);
    }

    @Override
    protected void startPermissionRequest(@NonNull Activity activity,
                                          @NonNull List<IPermission> permissions,
                                          @IntRange(from = 1, to = 65535) int requestCode) {
        if (!PermissionVersion.isAndroid6()) {
            // If the current system is below Android 6.0, dangerous permissions do not apply, so call back immediately.
            sendTask(this::handlerPermissionCallback, 0);
            return;
        }

        // If no special handling is needed, request all dangerous permissions directly
        requestPermissions(PermissionUtils.convertPermissionArray(permissions), requestCode);
        // Record the permissions that have already been requested(for more accurate detection of whether the user selected Do not ask again)
        AlreadyRequestPermissionsManager.addAlreadyRequestPermissions(permissions);
    }

    @Override
    public void onFragmentRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        // If the request code in the callback does not match the original request code, the callback is invalid, so stop here.
        if (requestCode != getPermissionRequestCode()) {
            return;
        }
        // Release the reservation for this request code.
        PermissionRequestCodeManager.releaseRequestCode(requestCode);
        // Notify the permission request callback.
        notificationPermissionCallback();
    }
}