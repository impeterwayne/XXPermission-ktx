package com.hjq.permissions.core;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionDescription;
import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactory;
import com.hjq.permissions.manager.ActivityOrientationManager;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionTaskHandler;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Main logic implementation class for permission requests
 */
public final class PermissionRequestMainLogic {

    @NonNull
    private final Activity mActivity;

    @NonNull
    private final List<IPermission> mRequestList;

    @NonNull
    private final PermissionFragmentFactory<?, ?> mFragmentFactory;

    @NonNull
    private final OnPermissionInterceptor mPermissionInterceptor;

    @NonNull
    private final OnPermissionDescription mPermissionDescription;

    @Nullable
    private final OnPermissionCallback mCallBack;

    public PermissionRequestMainLogic(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                           @NonNull OnPermissionInterceptor permissionInterceptor,
                                           @NonNull OnPermissionDescription permissionDescription,
                                           @Nullable OnPermissionCallback callback) {
        mActivity = activity;
        mRequestList = requestList;
        mFragmentFactory = fragmentFactory;
        mPermissionInterceptor = permissionInterceptor;
        mPermissionDescription = permissionDescription;
        mCallBack = callback;
    }

    /**
     * Start permission request
     */
    public void request() {
        if (mRequestList.isEmpty()) {
            return;
        }

        List<List<IPermission>> unauthorizedList = getUnauthorizedList(mActivity, mRequestList);
        if (unauthorizedList.isEmpty()) {
            // Indicates that there are no permissions to request, directly handle the permission request result
            handlePermissionRequestResult();
            return;
        }

        Iterator<List<IPermission>> iterator = unauthorizedList.iterator();
        List<IPermission> firstPermissions = null;
        while (iterator.hasNext() && (firstPermissions == null || firstPermissions.isEmpty())) {
            firstPermissions = iterator.next();
        }
        if (firstPermissions == null || firstPermissions.isEmpty()) {
            // Indicates that there are no permissions to request, directly handle the permission request result
            handlePermissionRequestResult();
            return;
        }

        final Activity activity = mActivity;
        final PermissionFragmentFactory<?, ?> fragmentFactory = mFragmentFactory;
        final OnPermissionDescription permissionDescription = mPermissionDescription;

        // Lock the Activity screen orientation
        ActivityOrientationManager.lockActivityOrientation(activity);

        // Initiate authorization
        requestPermissionsByFragment(activity, firstPermissions, fragmentFactory, permissionDescription, new Runnable() {

            @Override
            public void run() {
                List<IPermission> nextPermissions = null;
                while (iterator.hasNext()) {
                    nextPermissions = iterator.next();

                    if (nextPermissions == null || nextPermissions.isEmpty()) {
                        // The obtained permission list does not meet the requirements, continue to get the next one. Although it has been filtered before, theoretically it should not reach here, but for code robustness, this check is still added.
                        continue;
                    }

                    // Here is an explanation of why we need to check again whether the permission is granted, even though it was checked before. Isn't this redundant? Mainly to adapt to several extreme scenarios:
                    // 1. The user initiates a request for camera permission and floating window permission. When the system pops up the camera permission dialog, the user does not grant it, but instead goes to the system settings, finds the floating window permission option for the current app, and grants it. Then returns to the app, where the system is still waiting for the camera permission. After granting the camera permission, the next permission to be requested is the floating window permission. However, since the user has already granted it, if we do not check again, the framework will still jump to the floating window settings page.
                    // 2. In a test on an Android 12 emulator, requesting foreground location permission (including coarse and fine location) and background location permission, if the user selects "Approximate location" (the system defaults to "Precise location"), the foreground location permission is not considered granted because fine location is not granted. If the user selects "Precise location", both are granted. If the next permission is background location, and the user selects "Always allow" but does not select "Use precise location", then returns to the app and requests permission again, the system will prompt to change from "Approximate location" to "Precise location". After changing, the foreground location is granted, and the next is background location. If we do not check again, the framework will request again, possibly triggering a dialog, but the permission is already granted, so the system will not show any dialog but will report success.
                    // Summary: The issue arises because there is no delay between the first permission requests, so we can trust the permissions are still not granted. But for the second batch, the situation is more complex because we cannot know what the user did during the first request.
                    if (PermissionApi.isGrantedPermissions(activity, nextPermissions)) {
                        // Set the next permission list to null, indicating it will not be requested
                        nextPermissions = null;
                        // The above permission list does not meet the requirements, continue to get the next one
                        continue;
                    }

                    // If the code reaches here, it means the next permission list is valid. Use break to exit the loop and proceed to the next step (permission request)
                    break;
                }

                if (nextPermissions == null || nextPermissions.isEmpty()) {
                    // Indicates that all requests are complete, delay sending the permission handling result
                    postDelayedHandlerRequestPermissionsResult();
                    return;
                }

                // Get the first permission in the next batch to be requested
                IPermission firstNextPermission = nextPermissions.get(0);
                // If the next permission is a background permission
                if (firstNextPermission.isBackgroundPermission(activity)) {
                    List<IPermission> foregroundPermissions = firstNextPermission.getForegroundPermissions(activity);
                    boolean grantedForegroundPermission = false;
                    // If the corresponding foreground permission for this background permission has not been granted, do not request the background permission, as the system will not approve it anyway
                    // If you still request in this case, it may trigger a permission explanation dialog, but no actual permission request will occur
                    if (foregroundPermissions != null && !foregroundPermissions.isEmpty()) {
                        for (IPermission foregroundPermission : foregroundPermissions) {
                            if (!foregroundPermission.isGrantedPermission(activity)) {
                                continue;
                            }
                            // As long as any of the foreground permissions are granted, it is considered granted
                            grantedForegroundPermission = true;
                        }
                    } else {
                        // If a permission is a background permission but does not return its corresponding foreground permission, assume the foreground permission is already granted, then request the background permission
                        grantedForegroundPermission = true;
                    }

                    if (!grantedForegroundPermission) {
                        // If the foreground permission is not granted, do not request the background permission, proceed to the next round
                        this.run();
                        return;
                    }
                }

                final List<IPermission> finalPermissions = nextPermissions;
                int maxWaitTime = PermissionApi.getMaxIntervalTimeByPermissions(activity, nextPermissions);
                if (maxWaitTime == 0) {
                    requestPermissionsByFragment(activity, finalPermissions, fragmentFactory, permissionDescription, this);
                } else {
                    PermissionTaskHandler.sendTask(() ->
                        requestPermissionsByFragment(activity, finalPermissions, fragmentFactory, permissionDescription, this), maxWaitTime);
                }
            }
        });
    }

    /**
     * Get the list of unauthorized permissions
     */
    @NonNull
    private static List<List<IPermission>> getUnauthorizedList(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // List of permissions to request
        List<List<IPermission>> unauthorizedList = new ArrayList<>(requestList.size());
        // List of already processed permissions
        List<IPermission> alreadyDoneList = new ArrayList<>(requestList.size());

        // Traverse the list of permissions to request
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);

            // If this permission has already been processed, skip it
            if (PermissionUtils.containsPermission(alreadyDoneList, permission)) {
                continue;
            }
            alreadyDoneList.add(permission);

            // If this permission does not support requests, do not include it
            if (!permission.isSupportRequestPermission(activity)) {
                continue;
            }

            // If this permission is already granted, do not include it
            if (permission.isGrantedPermission(activity)) {
                continue;
            }

            // ------------ The following is the logic for permissions that require startActivityForResult to authorize (usually special permissions) ------------------ //

            if (permission.getPermissionChannel(activity) == PermissionChannel.START_ACTIVITY_FOR_RESULT) {
                // If this is a permission that requires a page jump to authorize, treat it as a separate permission request
                unauthorizedList.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            // ------------ The following is the logic for permissions that require requestPermissions to authorize (usually dangerous permissions) ------------------ //

            // Query the permission group type for dangerous permissions
            String permissionGroup = permission.getPermissionGroup(activity);
            if (TextUtils.isEmpty(permissionGroup)) {
                // If the permission group is empty, it means this permission is not defined in a group, so treat it as a separate request
                unauthorizedList.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            List<IPermission> todoPermissions = null;
            for (int j = i; j < requestList.size(); j++) {
                IPermission todoPermission = requestList.get(j);
                // If the traversed permission is not in the same group, continue searching
                if (!PermissionUtils.equalsString(todoPermission.getPermissionGroup(activity), permissionGroup)) {
                    continue;
                }

                // Check if the current permission supports requests
                if (!todoPermission.isSupportRequestPermission(activity)) {
                    // If this permission does not support requests, skip it
                    continue;
                }

                // Check if the permission to be requested is already granted
                if (todoPermission.isGrantedPermission(activity)) {
                    // If this permission is already granted, skip it
                    // Github issue: https://github.com/getActivity/XXPermissions/issues/369
                    continue;
                }

                // If the list of permissions to process has not been initialized, initialize it
                if (todoPermissions == null) {
                    todoPermissions = new ArrayList<>();
                }
                // Add to the list of permissions to process
                todoPermissions.add(todoPermission);

                // If this dangerous permission has already been processed, do not add it again
                if (PermissionUtils.containsPermission(alreadyDoneList, todoPermission)) {
                    continue;
                }
                // Add to the list of already processed permissions
                alreadyDoneList.add(todoPermission);
            }

            // If the list of permissions to process is empty, it means the remaining permissions only appear on higher system versions, so no need to request again
            if (todoPermissions == null || todoPermissions.isEmpty()) {
                continue;
            }

            // If all permissions in the list are already granted, do not include them
            if (PermissionApi.isGrantedPermissions(activity, todoPermissions)) {
                continue;
            }

            // Check if the permission group to be requested contains background permissions (e.g., background location, background sensors). If so, they cannot be requested together and need to be split.
            List<IPermission> backgroundPermissions = null;
            Iterator<IPermission> iterator = todoPermissions.iterator();
            while (iterator.hasNext()) {
                IPermission todoPermission = iterator.next();
                // First check if this permission is a background permission, if not, continue searching
                if (!todoPermission.isBackgroundPermission(activity)) {
                    continue;
                }
                // Take out the background permission and put it in another collection, then treat it as a separate request
                iterator.remove();
                backgroundPermissions = new ArrayList<>();
                backgroundPermissions.add(todoPermission);
                // Task complete, break the loop
                break;
            }

            List<IPermission> foregroundPermissions = todoPermissions;

            // Add foreground permissions (if not already granted)
            if (!foregroundPermissions.isEmpty()) {
                unauthorizedList.add(foregroundPermissions);
            }
            // Add background permissions (if not already granted)
            if (backgroundPermissions != null && !backgroundPermissions.isEmpty()) {
                unauthorizedList.add(backgroundPermissions);
            }
        }

        return unauthorizedList;
    }

    /**
     * Initiate authorization via Fragment
     */
    private static void requestPermissionsByFragment(@NonNull Activity activity,
                                                     @NonNull List<IPermission> permissions,
                                                     @NonNull PermissionFragmentFactory<?, ?> fragmentFactory,
                                                     @NonNull OnPermissionDescription permissionDescription,
                                                     @NonNull Runnable finishRunnable) {
        if (permissions.isEmpty()) {
            finishRunnable.run();
            return;
        }

        PermissionChannel permissionChannel = PermissionChannel.REQUEST_PERMISSIONS;
        for (IPermission permission : permissions) {
            if (permission.getPermissionChannel(activity) == PermissionChannel.REQUEST_PERMISSIONS) {
                continue;
            }
            permissionChannel = PermissionChannel.START_ACTIVITY_FOR_RESULT;
            break;
        }

        if (!PermissionVersion.isAndroid6() && permissionChannel == PermissionChannel.REQUEST_PERMISSIONS) {
            // If it is below Android 6.0, requestPermissions cannot be used, so skip this request and continue to the next
            finishRunnable.run();
            return;
        }

        PermissionChannel finalPermissionChannel = permissionChannel;
        Runnable continueRequestRunnable = () ->
            fragmentFactory.createAndCommitFragment(permissions, finalPermissionChannel, new OnPermissionFragmentCallback() {

            @Override
            public void onRequestPermissionNow() {
                permissionDescription.onRequestPermissionStart(activity, permissions);
            }

            @Override
            public void onRequestPermissionFinish() {
                permissionDescription.onRequestPermissionEnd(activity, permissions);
                finishRunnable.run();
            }

            @Override
            public void onRequestPermissionAnomaly() {
                permissionDescription.onRequestPermissionEnd(activity, permissions);
            }
        });

        permissionDescription.askWhetherRequestPermission(activity, permissions, continueRequestRunnable, finishRunnable);
    }

    /**
     * Delay handling of permission request result
     */
    private void postDelayedHandlerRequestPermissionsResult() {
        PermissionTaskHandler.sendTask(this::handlePermissionRequestResult, 100);
    }

    /**
     * Delay unlocking Activity orientation
     */
    private void postDelayedUnlockActivityOrientation(@NonNull Activity activity) {
        // Delayed execution is to allow the code in the outer callback to execute in order
        PermissionTaskHandler.sendTask(() -> ActivityOrientationManager.unlockActivityOrientation(activity), 100);
    }

    /**
     * Handle permission request result
     */
    private void handlePermissionRequestResult() {
        final Activity activity = mActivity;

        final List<IPermission> requestList = mRequestList;

        // If the current Activity is unavailable, do not continue
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        List<IPermission> grantedList = new ArrayList<>(requestList.size());
        List<IPermission> deniedList = new ArrayList<>(requestList.size());
        // Traverse the requested permissions and classify them according to their grant status
        for (IPermission permission : requestList) {
            if (permission.isGrantedPermission(activity, false)) {
                grantedList.add(permission);
            } else {
                deniedList.add(permission);
            }
        }

        // Permission request finished
        mPermissionInterceptor.onRequestPermissionEnd(activity, false, requestList, grantedList, deniedList, mCallBack);

        // Delay unlocking Activity screen orientation
        postDelayedUnlockActivityOrientation(activity);
    }
}