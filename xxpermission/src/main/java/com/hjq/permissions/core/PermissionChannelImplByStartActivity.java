package com.hjq.permissions.core;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.AlreadyRequestPermissionsManager;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.start.StartActivityAgent;
import com.hjq.permissions.tools.PermissionApi;
import java.util.List;

/**
 * Permission request implementation(implemented through {@link android.app.Activity#startActivityForResult(Intent, int)}).
 */
public final class PermissionChannelImplByStartActivity extends PermissionChannelImpl {

    /** Total number of ignored onActivityResult callbacks */
    private int mIgnoreActivityResultCount = 0;

    public PermissionChannelImplByStartActivity(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        super(fragmentMethod);
    }

    @Override
    protected void startPermissionRequest(@NonNull Activity activity,
                                          @NonNull List<IPermission> permissions,
                                          @IntRange(from = 1, to = 65535) int requestCode) {
        StartActivityAgent.startActivityForResult(activity, getStartActivityDelegate(),
                                PermissionApi.getBestPermissionSettingIntent(activity, permissions, false),
                                requestCode, () -> mIgnoreActivityResultCount++);
        // Record the permissions that have already been requested
        AlreadyRequestPermissionsManager.addAlreadyRequestPermissions(permissions);
    }

    @Override
    public void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If the request code in the callback does not match the original request code, the callback is invalid, so stop here.
        if (requestCode != getPermissionRequestCode()) {
            return;
        }
        // If startActivityForResult fails to launch, the framework catches the resulting exception automatically.
        // This prevents the app from crashing and retries with the next Intent until one can be launched.
        // But each launch failure can still trigger an onActivityResult callback from the system.
        // That can result in multiple onActivityResult callbacks, which could notify completion before the permission flow has truly finished.
        // In this case, checking only the requestCode is not enough. A better workaround is needed.
        // The solution is to count how many startActivityForResult launches failed, then decrement that count in onActivityResult.
        // That filters out callbacks caused by failed launches. Only when the count drops to zero should the permission result callback be dispatched.
        if (mIgnoreActivityResultCount > 0) {
            mIgnoreActivityResultCount--;
            return;
        }
        // Release the reservation for this request code.
        PermissionRequestCodeManager.releaseRequestCode(requestCode);
        // Notify the permission request callback.
        notificationPermissionCallback();
    }
}