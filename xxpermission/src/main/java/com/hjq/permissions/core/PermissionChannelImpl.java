package com.hjq.permissions.core;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import com.hjq.permissions.fragment.IFragmentCallback;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.manager.ActivityOrientationManager;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.start.IStartActivityDelegate;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionTaskHandler;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Base implementation for fragment driven permission requests.
 */
public abstract class PermissionChannelImpl implements IFragmentCallback {

    /** Requested permissions */
    public static final String REQUEST_PERMISSIONS = "request_permissions";

    /** Request code, generated automatically*/
    public static final String REQUEST_CODE = "request_code";

    /** Task token */
    @NonNull
    private final Object mTaskToken = new Object();

    /** Non-system restart flag */
    private boolean mNonSystemRestartMark;

    /** Whether the permission request has started */
    private boolean mAlreadyRequest;

    /** Whether the current Fragment was detached manually */
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
        // If the user stays away too long, the Activity may be reclaimed.
        // So this checks whether the current Fragment is still attached to the Activity.
        // You can reproduce this bug by enabling Do not keep activities in developer options.
        return !mFragmentMethod.isAdded() || mFragmentMethod.isRemoving();
    }

    @RequiresApi(PermissionVersion.ANDROID_6)
    protected void requestPermissions(@NonNull String[] permissions, @IntRange(from = 1, to = 65535) int requestCode) {
        try {
            mFragmentMethod.requestPermissions(permissions, requestCode);
        } catch (Exception e1) {
            // In some extreme cases, calling the system requestPermissions method can crash. At first I thought this only happened on devices below Android 6.0,
            // but it turns out Android 6.0 and can hit this too. After analysis, the possible causes are:
            // 1. The vendor changed the package name of the com.android.packageinstaller system app and shipped it without enough testing, low probability.
            // 2. The vendor removed the com.android.packageinstaller system app and shipped it without enough testing, low probability.
            // 3. The vendor changed Android system source in a way that affected the permission module and shipped it without enough testing, low probability.
            // 4. The vendor removed permission request support, for example on some TV devices, which can make apps crash as soon as they request dangerous permissions, low probability.
            // 5. The user has root access and accidentally removed the com.android.packageinstaller system app while trimming system apps, higher probability.
            // After reviewing Activity.requestPermissions, it ultimately still relies on startActivityForResult, except the target Activity cannot be found.
            // The best workaround so far is to wrap it in try/catch to avoid a crash.
            // Could this prevent onRequestPermissionsResult from being called and stall the flow? It is hard to test directly, but in theory it should not.
            // I tested this by calling startActivityForResult with an invalid Intent inside try/catch, and onActivityResult was still delivered normally.
            // That shows wrapping startActivityForResult in try/catch does not block onActivityResult. I also reviewed the Activity callback source.
            // Both onRequestPermissionsResult and onActivityResult are dispatched through dispatchActivityResult.
            // So in such extreme cases, if onActivityResult is still called, dispatchActivityResult must also still be running normally.
            // By the same logic, onRequestPermissionsResult should also still be dispatched, which keeps the request flow complete.
            // Additional test result: while debugging Activity.requestPermissions, I intentionally changed the permission request Intent action to an invalid one, and the permission callback still returned normally.
            // If this extreme case really happens, dangerous permissions will inevitably fail, but the framework should still keep the app from crashing and complete the permission flow.
            // Related GitHub issues:
            // 1. https://github.com/getActivity/XXPermissions/issues/153
            // 2. https://github.com/getActivity/XXPermissions/issues/126
            // 3. https://github.com/getActivity/XXPermissions/issues/327
            // 4. https://github.com/getActivity/XXPermissions/issues/339
            // 5. https://github.com/guolindev/PermissionX/issues/92
            // 6. https://github.com/yanzhenjie/AndPermission/issues/72
            // 7. https://github.com/yanzhenjie/AndPermission/issues/28
            // 8. https://github.com/permissions-dispatcher/PermissionsDispatcher/issues/288
            // 9. https://github.com/googlesamples/easypermissions/issues/342
            // 10. https://github.com/HuanTanSheng/EasyPhotos/issues/256
            // 11. https://github.com/oasisfeng/island/issues/67
            // 12. https://github.com/Rakashazi/emu-ex-plus-alpha/issues/137
            // 13. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/1792
            // 14. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/1794
            // 15. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/1795
            // 16. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/2012
            // 17. https://github.com/hyb1996-guest/AutoJsIssueReport/issues/18264
            // android.content.ActivityNotFoundException: No Activity found to handle Intent
            // { act=android.content.pm.action.REQUEST_PERMISSIONS pkg=com.android.packageinstaller (has extras) }
            e1.printStackTrace();

            Activity activity = mFragmentMethod.getActivity();
            // If this Activity is a FragmentActivity, do not retry with activity.requestPermissions.
            // That is because when the caller passes a FragmentActivity, an AndroidX Fragment is created.
            // The AndroidX Fragment requestPermissions method still ends up calling activity.requestPermissions.
            // So when the caller passes a FragmentActivity, there is no retry to avoid calling activity.requestPermissions twice.
            // This may look redundant, but real users have reported this strange behavior.
            // On a very small number of devices, calling requestPermissions through android.app.Fragment can crash.
            // Switching to ActivityCompat.requestPermissions or activity.requestPermissions avoids the issue.
            // The current guess is that some vendors modified android.app.Fragment and introduced a bug without thorough testing.
            // So the retry uses activity.requestPermissions. This is not perfect and can desynchronize the request and callback.
            // If fragment.requestPermissions already failed and triggered a callback, calling activity.requestPermissions here may not produce a matching callback.
            // But edge-case handling like this cannot be fully perfect. The best fix is still for the vendor to correct the bug.
            // Related issue:https://github.com/getActivity/XXPermissions/issues/339
            if (activity instanceof FragmentActivity) {
                return;
            }
            if (PermissionUtils.isActivityUnavailable(activity)) {
                return;
            }
            try {
                activity.requestPermissions(permissions, requestCode);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
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

    /** Starts the permission request. */
    protected abstract void startPermissionRequest(@NonNull Activity activity, @NonNull List<IPermission> permissions,
                                         @IntRange(from = 1, to = 65535) int requestCode);

    @Override
    public void onFragmentResume() {
        // If the current Fragment was triggered by a system restart of the app, do not request permissions.
        // This prevents the system from restarting the app and triggering permission requests again.
        if (!mNonSystemRestartMark) {
            mFragmentMethod.commitFragmentDetach();
            return;
        }

        // If a Fragment is added and requests permissions while the Activity is not visible, the system dialog may not appear.
        // So permissions must be requested in Fragment.onResume, which ensures the app is back in the foreground first.
        if (mAlreadyRequest) {
            return;
        }

        mAlreadyRequest = true;
        Activity activity = getActivity();
        // Check whether the Activity is unavailable
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
        // Cancel the pending task
        cancelTask();
        OnPermissionFragmentCallback callback = getPermissionFragmentCallback();
        // If the callback is still not null, the permission completion callback did not finish earlier.
        if (callback != null) {
            // Notify the caller that this permission callback was abnormal.
            callback.onRequestPermissionAnomaly();
            // Release the callback object to avoid leaks
            setPermissionFragmentCallback(null);
        }
        if (mManualDetach) {
            return;
        }
        Activity activity = getActivity();
        // Check whether the Activity is unavailable
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        // If this was not a manual detach, the system detached it, so the Activity orientation needs to be restored here.
        // If it was detached manually, the Activity orientation will be restored after all permissions finish.
        ActivityOrientationManager.unlockActivityOrientation(activity);
    }

    /** Schedules delivery of the permission callback. */
    protected void notificationPermissionCallback() {
        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        // Cancel the previous task first to avoid duplicate callbacks
        cancelTask();
        // Handle the permission request result with a delay
        sendTask(this::handlerPermissionCallback, PermissionApi.getMaxWaitTimeByPermissions(activity, getPermissionRequestList()));
    }

    /** Delivers the permission callback and removes the request fragment. */
    protected void handlerPermissionCallback() {
        if (isFragmentUnavailable()) {
            return;
        }

        Activity activity = getActivity();
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        OnPermissionFragmentCallback callback = getPermissionFragmentCallback();
        // Release the listener reference
        setPermissionFragmentCallback(null);

        if (callback != null) {
            callback.onRequestPermissionFinish();
        }

        // Remove the Fragment
        commitFragmentDetach();
    }
}
