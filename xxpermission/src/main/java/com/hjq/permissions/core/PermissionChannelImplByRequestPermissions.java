package com.hjq.permissions.core;

import android.app.Activity;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.AlreadyRequestPermissionsManager;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Permission request implementation class (implemented through {@link android.app.Activity#requestPermissions(String[], int)})
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
            // If the current system is below Android 6.0, then the concept of dangerous permissions does not exist,
            // so directly trigger the permission callback
            sendTask(this::handlerPermissionCallback, 0);
            return;
        }

        // If necessary, directly request all dangerous permissions
        requestPermissions(PermissionUtils.convertPermissionArray(activity, permissions), requestCode);
        // Record the already requested permissions (used to more accurately determine whether the user has checked "Don't ask again")
        AlreadyRequestPermissionsManager.addAlreadyRequestPermissions(permissions);
    }

    @Override
    public void onFragmentRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        // If the request code in the callback does not match the request code set during the request,
        // then the callback is invalid, so stop execution
        if (requestCode != getPermissionRequestCode()) {
            return;
        }
        // Release the occupancy of this request code
        PermissionRequestCodeManager.releaseRequestCode(requestCode);
        // Notify the permission request callback
        notificationPermissionCallback();
    }
}
