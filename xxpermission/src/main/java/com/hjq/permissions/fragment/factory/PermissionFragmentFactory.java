package com.hjq.permissions.fragment.factory;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.core.PermissionChannelImpl;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Permission Fragment Factory
 */
public abstract class PermissionFragmentFactory<A extends Activity, M> {

    /*
     * Explanation of why the abstract factory pattern is used here to create Fragments,
     * instead of using the old approach where the App package's Fragment directly requested permissions.
     *
     * Problem 1: If you directly use the App package’s Fragment and call fragment.requestPermissions,
     *            on a very small number of devices it may crash. After multiple rounds of debugging,
     *            the cause was found: the crash is due to the manufacturer modifying the Android source code.
     *            It was verified that ActivityCompat.requestPermissions (which internally calls activity.requestPermissions)
     *            does not have this problem. If even activity.requestPermissions has issues, then there’s nothing
     *            I can do—the OEM developers/testers responsible for that change should probably be "sacrificed" by their company.
     *
     *            The best solution:
     *            - If XXPermissions.with is called with a FragmentActivity or Support library Fragment,
     *              then use the Support library Fragment to request permissions.
     *            - In other cases, use the App package Fragment.
     *            This minimizes the risk of such crashes.
     *
     * Related GitHub issues:
     * 1. https://github.com/getActivity/XXPermissions/issues/339
     * 2. https://github.com/getActivity/XXPermissions/issues/126
     * 3. https://github.com/getActivity/XXPermissions/issues/357
     *
     * Problem 2: If you directly use the App package’s Fragment to get permission callbacks,
     *            and the request was started from a Support library Fragment:
     *            - If the Support library Fragment is destroyed during the permission request,
     *              the callback is still triggered to the outer layer.
     *            - This happens because lifecycles of different Fragment classes are not synchronized.
     *            - As a result, the callback could be delivered to a destroyed Fragment. If the outer code
     *              doesn’t check the Fragment’s state before proceeding, this may cause a crash like:
     *              java.lang.IllegalStateException: Fragment XxxFragment not attached to a context
     *
     *            Best solution: Create a Fragment that is attached to the same host as the caller,
     *            so the lifecycle is properly bound.
     *            1. If the host is a FragmentActivity → create a Support library Fragment and bind it to the activity.
     *            2. If the host is an Activity → create an App package Fragment and bind it to the activity.
     *            3. If the host is a Support library Fragment → create a Support library Fragment and bind it as a child Fragment.
     *            4. If the host is an App package Fragment → create an App package Fragment and bind it as a child Fragment.
     *
     * Related GitHub issue: https://github.com/getActivity/XXPermissions/issues/365
     */

    @NonNull
    private final A mActivity;

    @NonNull
    private final M mFragmentManager;

    protected PermissionFragmentFactory(@NonNull A activity, @NonNull M fragmentManager) {
        mActivity = activity;
        mFragmentManager = fragmentManager;
    }

    /**
     * Get the Activity object
     */
    @NonNull
    protected A getActivity() {
        return mActivity;
    }

    /**
     * Get the FragmentManager object
     */
    @NonNull
    protected M getFragmentManager() {
        return mFragmentManager;
    }

    /**
     * Create and commit the Fragment
     */
    public abstract void createAndCommitFragment(@NonNull List<IPermission> permissions,
                                                 @NonNull PermissionChannel permissionChannel,
                                                 @Nullable OnPermissionFragmentCallback callback);

    /**
     * Generate arguments for the permission request
     */
    @NonNull
    protected Bundle generatePermissionArguments(@NonNull List<IPermission> permissions, @IntRange(from = 1, to = 65535) int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(PermissionChannelImpl.REQUEST_CODE, requestCode);
        if (permissions instanceof ArrayList) {
            bundle.putParcelableArrayList(PermissionChannelImpl.REQUEST_PERMISSIONS, (ArrayList<IPermission>) permissions);
        } else {
            bundle.putParcelableArrayList(PermissionChannelImpl.REQUEST_PERMISSIONS, new ArrayList<>(permissions));
        }
        return bundle;
    }
}
