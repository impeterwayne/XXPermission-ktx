package com.hjq.permissions.core;

import android.app.Activity;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * Main implementation of the permission request flow.
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

    /** Starts the permission request flow. */
    public void request() {
        if (mRequestList.isEmpty()) {
            return;
        }

        List<List<IPermission>> unauthorizedList = getUnauthorizedList(mActivity, mRequestList);
        if (unauthorizedList.isEmpty()) {
            // This means there are no permissions left to request, so handle the result directly.
            handlePermissionRequestResult();
            return;
        }

        Iterator<List<IPermission>> iterator = unauthorizedList.iterator();
        List<IPermission> firstPermissions = null;
        while (iterator.hasNext() && (firstPermissions == null || firstPermissions.isEmpty())) {
            firstPermissions = iterator.next();
        }
        if (firstPermissions == null || firstPermissions.isEmpty()) {
            // This means there are no permissions left to request, so handle the result directly.
            handlePermissionRequestResult();
            return;
        }

        final Activity activity = mActivity;
        final PermissionFragmentFactory<?, ?> fragmentFactory = mFragmentFactory;
        final OnPermissionDescription permissionDescription = mPermissionDescription;

        // Lock the Activity orientation
        ActivityOrientationManager.lockActivityOrientation(activity);

        // start authorization
        requestPermissionsByFragment(activity, firstPermissions, fragmentFactory, permissionDescription, new Runnable() {

            @Override
            public void run() {
                List<IPermission> nextPermissions = null;
                while (iterator.hasNext()) {
                    nextPermissions = iterator.next();

                    if (nextPermissions == null || nextPermissions.isEmpty()) {
                        // This permission list is not valid, so continue scanning. It should not happen after earlier filtering, but the extra check keeps the code safer.
                        continue;
                    }

                    // Here is why the code checks grant state again even after checking earlier. It looks redundant, but it handles several edge cases:
                    // 1. the user startscamera permission and system alert window permission request, while the system is showingcamera permissionauthorization dialog, the user does not grant it, and instead does something unusual,
                    // directlygoes into system settings, finds the current app details page and itssystem alert window permissionoption, then directly grants thesystem alert window permission, and finally returns to the app,
                    // the system is still waiting for the user to answer theusergrantedcamera permission, after after the user grants the camera permission, the next permission requested by the framework becomes the system alert window permission,
                    // but the user had already granted thesystem alert window permission, If the framework does not check again here whether the permission is granted, a problem occurs, the framework still navigates to thesystem alert windowsettings page.
                    // 2. During one accidental test, I found that on an Android 12 emulatorrequestforeground location permission (includesapproximate location and precise location) and background location permissionthere is an issue,
                    // The issue is that when the user grants the location permission, intentionally selects "Approximate location"(the system defaults to "Precise location"), this means the foreground location permission request does not count as successful,
                    // This is because selecting "Approximate location" causesthe precise location permission not being granted, while the opposite does not, If the user selects "Precise location", both the precise and approximate location permissions being granted,
                    // At this point, the next permission is the background location permission, the framework guides the user to the permission settings page to grantlocation permission, , the user finds the location permission option, and enters it,
                    // At this point, the user selects the "Allow all the time" option, but intentionally does not enable "Use precise location", then returns to the app, the user returns to the app and starts the permission request again,
                    // at this point the system shows a dialog asking the user to upgrade from "Approximate location" to "Precise location", and the user chooses to switch to "Precise location", the foreground location permission request is finally complete,
                    // At that point, the next permission is the background location permission, but as noted earlier, the user had already selected"Allow all the time"option, If the framework does not check again here whether the permission is granted, a problem occurs,
                    // the framework still starts a new permission request, If the background location permission permission description, asks with a dialog whether to start the permission request, a strange result appears,
                    // The app asks the user with a dialog whether to start the permission request, the user chooses "Yes", but the background location permission had already been granted, so the system shows no authorization dialog and directly reports success to the user.
                    // In summary: this issue happens because there is no delay during the first requested permission batch, so the user has no chance to change anything else, so it is still reasonable to trust that the permission remains ungranted,
                    // but by the second requested permission batch the situation becomes much more complicated, because you can never predict what the user may have done while the first permission list was being requested, during that time.
                    if (PermissionApi.isGrantedPermissions(activity, nextPermissions)) {
                        // Clear the next permission batch so it will not be requested
                        nextPermissions = null;
                        // The permission batch does not meet request requirements, so keep scanning
                        continue;
                    }

                    // If execution reaches here, the next permission batch is valid. Break the loop and continue with the next request step.
                    break;
                }

                if (nextPermissions == null || nextPermissions.isEmpty()) {
                    // This means all requests are complete, so post the result handling with a delay
                    postDelayedHandlerRequestPermissionsResult();
                    return;
                }

                // Get the first permission in the next batch to request
                IPermission firstNextPermission = nextPermissions.get(0);
                // If the next requested permission is a background permission
                if (firstNextPermission.isBackgroundPermission(activity)) {
                    List<IPermission> foregroundPermissions = firstNextPermission.getForegroundPermissions(activity);
                    boolean grantedForegroundPermission = false;
                    // If the corresponding foreground permission was not granted, do not request the background permission because the system will not approve it.
                    // If you force the request anyway, a rationale dialog may appear even though no real permission request will follow.
                    if (foregroundPermissions != null && !foregroundPermissions.isEmpty()) {
                        for (IPermission foregroundPermission : foregroundPermissions) {
                            if (!foregroundPermission.isGrantedPermission(activity)) {
                                continue;
                            }
                            // If any one of the foreground permissions is granted, treat the foreground requirement as satisfied.
                            grantedForegroundPermission = true;
                        }
                    } else {
                        // If a permission is a background permission but does not return corresponding foreground permissions, assume the foreground requirement is already satisfied and continue.
                        grantedForegroundPermission = true;
                    }

                    if (!grantedForegroundPermission) {
                        // If the foreground permission is not granted, skip the background request and move to the next round.
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
     * Get the list of ungranted permissions
     */
    @NonNull
    private static List<List<IPermission>> getUnauthorizedList(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // pending permission request list
        List<List<IPermission>> unauthorizedList = new ArrayList<>(requestList.size());
        // Processed permission list
        List<IPermission> alreadyDoneList = new ArrayList<>(requestList.size());

        // Traverse the pending permission request list
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);

            // If this permissionwas already processed earlier, so skip it
            if (PermissionUtils.containsPermission(alreadyDoneList, permission)) {
                continue;
            }
            alreadyDoneList.add(permission);

            // If this permissiondoes not support requests, so do not includes it in the request range
            if (!permission.isSupportRequestPermission(activity)) {
                continue;
            }

            // If this permissionis already granted, so do not includes it in the request range
            if (permission.isGrantedPermission(activity)) {
                continue;
            }

            // ------------ The following logic handles permissions that can only be granted through startActivityForResult, usually special permissions ------------------ //

            if (permission.getPermissionChannel(activity) == PermissionChannel.START_ACTIVITY) {
                // If this permission requires navigating to another page to grant it, handle it as its own request batch
                unauthorizedList.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            // ------------ The following logic handles permissions that can only be granted through requestPermissions, usually dangerous permissions ------------------ //

            // Look up the permission group for the dangerous permission
            String permissionGroup = permission.getPermissionGroup(activity);
            if (TextUtils.isEmpty(permissionGroup)) {
                // If the permission group is empty, this permission has no defined group, so request it separately
                unauthorizedList.add(PermissionUtils.asArrayList(permission));
                continue;
            }

            List<IPermission> todoPermissions = null;
            for (int j = i; j < requestList.size(); j++) {
                IPermission todoPermission = requestList.get(j);
                // If the iterated permission does not belong to the same group, keep looking
                if (!PermissionUtils.equalsString(todoPermission.getPermissionGroup(activity), permissionGroup)) {
                    continue;
                }

                // Check whether the current permission supports requesting
                if (!todoPermission.isSupportRequestPermission(activity)) {
                    // If this permissiondoes not support requests, do not continue
                    continue;
                }

                // Check whether the permission to request is already granted
                if (todoPermission.isGrantedPermission(activity)) {
                    // If this permissionis is already granted, do not continue
                    // GitHub issue: https://github.com/getActivity/XXPermissions/issues/369
                    continue;
                }

                // Initialize the pending permission list if needed
                if (todoPermissions == null) {
                    todoPermissions = new ArrayList<>();
                }
                // Add it to the pending permission list
                todoPermissions.add(todoPermission);

                // If this dangerous permission was already handled earlier, do not add it again
                if (PermissionUtils.containsPermission(alreadyDoneList, todoPermission)) {
                    continue;
                }
                // Add it to the processed permission list
                alreadyDoneList.add(todoPermission);
            }

            // If the pending permission list is empty, the remaining permissions only exist on higher system versions, so no new request is needed
            if (todoPermissions == null || todoPermissions.isEmpty()) {
                continue;
            }

            // If the pending permission list is already fully granted, exclude it from requests
            if (PermissionApi.isGrantedPermissions(activity, todoPermissions)) {
                continue;
            }

            // Check whether the permission group includess background permissions, such as background location or background sensors. If so, split them into separate requests.
            List<IPermission> backgroundPermissions = null;
            Iterator<IPermission> iterator = todoPermissions.iterator();
            while (iterator.hasNext()) {
                IPermission todoPermission = iterator.next();
                // First check whether this permission is a background permission. If not, keep searching.
                if (!todoPermission.isBackgroundPermission(activity)) {
                    continue;
                }
                // Move the background permission into another list and request it separately
                iterator.remove();
                backgroundPermissions = new ArrayList<>();
                backgroundPermissions.add(todoPermission);
                // Done with this step, skip the rest of the loop
                break;
            }

            List<IPermission> foregroundPermissions = todoPermissions;

            // Add foreground permissions, if they are not already granted
            if (!foregroundPermissions.isEmpty()) {
                unauthorizedList.add(foregroundPermissions);
            }
            // Add background permissions, if they are not already granted
            if (backgroundPermissions != null && !backgroundPermissions.isEmpty()) {
                unauthorizedList.add(backgroundPermissions);
            }
        }

        return unauthorizedList;
    }

    /**
     * Start authorization through a Fragment
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
            permissionChannel = PermissionChannel.START_ACTIVITY;
            break;
        }

        if (!PermissionVersion.isAndroid6() && permissionChannel == PermissionChannel.REQUEST_PERMISSIONS) {
            // On Android versions below 6.0, requestPermissions cannot be used, so skip this request batch and continue with the next one.
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
     * Handle the permission request result with a delay
     */
    private void postDelayedHandlerRequestPermissionsResult() {
        PermissionTaskHandler.sendTask(this::handlePermissionRequestResult, 100);
    }

    /**
     * Unlock the Activity orientation with a delay
     */
    private void postDelayedUnlockActivityOrientation(@NonNull Activity activity) {
        // The delay lets caller callback code finish in order
        PermissionTaskHandler.sendTask(() -> ActivityOrientationManager.unlockActivityOrientation(activity), 100);
    }

    /**
     * Handle the permission request result
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
        // Traverse the requested permissions and classify them by grant state
        for (IPermission permission : requestList) {
            if (permission.isGrantedPermission(activity, false)) {
                grantedList.add(permission);
            } else {
                deniedList.add(permission);
            }
        }

        // Permission request finished
        mPermissionInterceptor.onRequestPermissionEnd(activity, false, requestList, grantedList, deniedList, mCallBack);

        // Unlock the Activity orientation with a delay
        postDelayedUnlockActivityOrientation(activity);
    }
}
