package com.hjq.permissions.core;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.permissions.fragment.IFragmentCallback;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.ActivityOrientationManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.start.IStartActivityDelegate;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionTaskHandler;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Permission request implementation class
 */
public abstract class PermissionChannelImpl implements IFragmentCallback {

    /** Requested permissions */
    public static final String REQUEST_PERMISSIONS = "request_permissions";

    /** Request code (auto-generated) */
    public static final String REQUEST_CODE = "request_code";

    /** Task token */
    @NonNull
    private final Object mTaskToken = new Object();

    /** Non-system restart flag */
    private boolean mNonSystemRestartMark;

    /** Whether the permission request has already been initiated */
    private boolean mAlreadyRequest;

    /** Whether the current Fragment is manually detached */
    private boolean mManualDetach;

    /** Fragment method object */
    @NonNull
    private final IFragmentMethod<?, ?> mFragmentMethod;

    /** Permission callback object */
    @Nullable
    private OnPermissionFragmentCallback mPermissionFragmentCallback;

    protected PermissionChannelImpl(@NonNull IFragmentMethod<?, ?> fragmentMethod) {
        mFragmentMethod = fragmentMethod;
    }

    public void setNonSystemRestartMark(boolean nonSystemRestartMark) {
        mNonSystemRestartMark = nonSystemRestartMark;
    }

    public void setPermissionFragmentCallback(@Nullable OnPermissionFragmentCallback callback) {
        mPermissionFragmentCallback = callback;
    }

    @Nullable
    private OnPermissionFragmentCallback getPermissionFragmentCallback() {
        return mPermissionFragmentCallback;
    }

    @Nullable
    private Activity getActivity() {
        return mFragmentMethod.getActivity();
    }

    private void commitFragmentDetach() {
        mManualDetach = true;
        mFragmentMethod.commitFragmentDetach();
    }

    private boolean isFragmentUnavailable() {
        // If the user leaves for too long, the Activity might be destroyed
        // So here we must check whether the current Fragment has been added to the Activity
        // This bug can be reproduced by enabling "Don't keep activities" in developer options
        return !mFragmentMethod.isAdded() || mFragmentMethod.isRemoving();
    }

    protected void requestPermissions(@NonNull String[] permissions, @IntRange(from = 1, to = 65535) int requestCode) {
        try {
            mFragmentMethod.requestPermissions(permissions, requestCode);
        } catch (Exception e) {
            // In some extreme cases, calling the system requestPermissions method may crash. At first,
            // I thought it only happened on devices below Android 6.0, but it also happens on 6.0 and above.
            // Possible reasons include:
            //   1. Manufacturer modified the com.android.packageinstaller system app package name but didn’t test properly.
            //   2. Manufacturer removed com.android.packageinstaller without proper testing.
            //   3. Manufacturer modified Android system source code, breaking the permission module.
            //   4. Manufacturer intentionally removed permission request support (e.g., some TV devices).
            //   5. User with root access deleted com.android.packageinstaller by mistake (most likely).
            //
            // After analyzing Activity.requestPermissions (which internally still uses startActivityForResult),
            // the best solution is to wrap it with try-catch to prevent crashes.
            //
            // Will this cause onRequestPermissionsResult not to be called? Theoretically, no.
            // I tested with a wrong Intent for startActivityForResult, and onActivityResult was still called normally.
            // Since both onRequestPermissionsResult and onActivityResult are triggered by dispatchActivityResult,
            // as long as one is called, the other should also be called.
            //
            // Test conclusion: even with a wrong Intent action, the permission callback was triggered correctly.
            //
            // So in these extreme cases, all dangerous permission requests will fail, but the framework’s goal
            // is to prevent crashes and still complete the request flow.
            //
            // Related GitHub issues:
            //   1. https://github.com/getActivity/XXPermissions/issues/153
            //   2. https://github.com/getActivity/XXPermissions/issues/126
            //   3. https://github.com/getActivity/XXPermissions/issues/327
            //   4. https://github.com/getActivity/XXPermissions/issues/339
            //   5. https://github.com/guolindev/PermissionX/issues/92
            //   6. https://github.com/yanzhenjie/AndPermission/issues/72
            //   7. https://github.com/yanzhenjie/AndPermission/issues/28
            //   8. https://github.com/permissions-dispatcher/PermissionsDispatcher/issues/288
            //   9. https://github.com/googlesamples/easypermissions/issues/342
            //   10. https://github.com/HuanTanSheng/EasyPhotos/issues/256
            //   11. https://github.com/oasisfeng/island/issues/67
            //   12. https://github.com/Rakashazi/emu-ex-plus-alpha/issues/137
            //   13. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/1792
            //   14. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/1794
            //   15. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/1795
            //   16. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/2012
            //   17. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/18264
            //
            // Example exception:
            // android.content.ActivityNotFoundException: No Activity found to handle Intent
            // { act=android.content.pm.action.REQUEST_PERMISSIONS pkg=com.android.packageinstaller (has extras) }
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    protected List<IPermission> getPermissionRequestList() {
        Bundle arguments = mFragmentMethod.getArguments();
        if (arguments == null) {
            return null;
        }
        if (PermissionVersion.isAndroid13()) {
            return arguments.getParcelableArrayList(REQUEST_PERMISSIONS, IPermission.class);
        } else {
            return arguments.getParcelableArrayList(REQUEST_PERMISSIONS);
        }
    }

    protected int getPermissionRequestCode() {
        Bundle arguments = mFragmentMethod.getArguments();
        if (arguments == null) {
            return 0;
        }
        return arguments.getInt(REQUEST_CODE);
    }

    protected void sendTask(@NonNull Runnable runnable, long delayMillis) {
        PermissionTaskHandler.sendTask(runnable, mTaskToken, delayMillis);
    }

    protected void cancelTask() {
        PermissionTaskHandler.cancelTask(mTaskToken);
    }

    protected IStartActivityDelegate getStartActivityDelegate() {
        return mFragmentMethod;
    }

    /**
     * Start permission request
     */
    protected abstract void startPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> permissions,
                                                   @IntRange(from = 1, to = 65535) int requestCode);

    @Override
    public void onFragmentResume() {
        // If this Fragment is triggered due to system app restart, do not request permissions
        // Prevents re-triggering permission requests after the system kills and restarts the app
        if (!mNonSystemRestartMark) {
            mFragmentMethod.commitFragmentDetach();
            return;
        }

        // If the Fragment is added while the Activity is not visible, requesting permission will fail
        // So request permissions only in Fragment.onResume, ensuring the app is in the foreground
        if (mAlreadyRequest) {
            return;
        }

        mAlreadyRequest = true;
        Activity activity = getActivity();
        // Check if Activity is unavailable
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        final int requestCode = getPermissionRequestCode();
        if (requestCode <= 0) {
            return;
        }
        List<IPermission> permissions = getPermissionRequestList();
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        startPermissionRequest(activity, permissions, requestCode);
        OnPermissionFragmentCallback callback = getPermissionFragmentCallback();
        if (callback == null) {
            return;
        }
        callback.onRequestPermissionNow();
    }

    @Override
    public void onFragmentDestroy() {
        // Cancel pending tasks
        cancelTask();
        OnPermissionFragmentCallback callback = getPermissionFragmentCallback();
        // If callback is not null here, then permission completion callback was never triggered
        if (callback != null) {
            // Notify that an exception occurred during permission request
            callback.onRequestPermissionAnomaly();
            // Release callback reference to avoid memory leaks
            setPermissionFragmentCallback(null);
        }
        if (mManualDetach) {
            return;
        }
        Activity activity = getActivity();
        // Check if Activity is unavailable
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        // If not manually detached, then it was system-triggered, so restore Activity orientation
        // If manually detached, orientation will be restored after all permissions are handled
        ActivityOrientationManager.unlockActivityOrientation(activity);
    }

    /**
     * Notify permission callback
     */
    protected void notificationPermissionCallback() {
        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        // Delay handling of permission request results
        sendTask(this::handlerPermissionCallback, PermissionApi.getMaxWaitTimeByPermissions(activity, getPermissionRequestList()));
    }

    /**
     * Handle permission callback
     */
    protected void handlerPermissionCallback() {
        if (isFragmentUnavailable()) {
            return;
        }

        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        OnPermissionFragmentCallback callback = getPermissionFragmentCallback();
        // Release the reference to the listener
        setPermissionFragmentCallback(null);

        if (callback != null) {
            callback.onRequestPermissionFinish();
        }

        // Remove the Fragment
        commitFragmentDetach();
    }
}
