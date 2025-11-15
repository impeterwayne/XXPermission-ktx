package com.hjq.permissions.core;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.start.StartActivityAgent;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Permission request implementation class (implemented through {@link android.app.Activity#startActivityForResult(Intent, int)})
 */
public final class PermissionChannelImplByStartActivityForResult extends PermissionChannelImpl {

    /** The total number of times to ignore the onActivityResult callback */
    private int mIgnoreActivityResultCount = 0;

    public PermissionChannelImplByStartActivityForResult(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        super(fragmentMethod);
    }

    @Override
    protected void startPermissionRequest(@NonNull Activity activity,
                                          @NonNull List<IPermission> permissions,
                                          @IntRange(from = 1, to = 65535) int requestCode) {
        StartActivityAgent.startActivityForResult(activity, getStartActivityDelegate(),
                PermissionApi.getBestPermissionSettingIntent(activity, permissions, false),
                requestCode, () -> mIgnoreActivityResultCount++);
    }

    @Override
    public void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If the requestCode in the callback does not match the one set when requesting,
        // it means the callback is incorrect, so do not proceed further
        if (requestCode != getPermissionRequestCode()) {
            return;
        }
        // If calling startActivityForResult fails to jump, the framework will automatically
        // catch the Exception caused by the failure. This is to prevent the application from crashing,
        // and it will automatically try the next Intent until it finds one that can jump successfully.
        // However, this introduces a problem: each failed startActivityForResult jump will trigger
        // an onActivityResult callback from the system. This may result in multiple onActivityResult
        // callbacks being triggered, which could lead to the awkward situation where the permission
        // request has not actually finished, but the callback has already been notified.
        // In this case, just checking whether requestCode matches is no longer sufficient to avoid the problem.
        // After much thought, a good solution is to record the number of failed startActivityForResult attempts,
        // and then decrement it in the onActivityResult callback. In other words, filter out those callbacks
        // caused by failed startActivityForResult attempts, and only when this counter is reduced to 0
        // should the permission request result be notified.
        if (mIgnoreActivityResultCount > 0) {
            mIgnoreActivityResultCount--;
            return;
        }
        // Release the occupancy of this request code
        PermissionRequestCodeManager.releaseRequestCode(requestCode);
        // Notify the permission request callback
        notificationPermissionCallback();
    }
}
