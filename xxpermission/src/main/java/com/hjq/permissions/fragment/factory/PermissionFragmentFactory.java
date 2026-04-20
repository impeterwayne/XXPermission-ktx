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
 * Factory for creating permission request fragments.
 */
public abstract class PermissionFragmentFactory<A extends Activity, M> {

    /*
     * A fragment factory is used here instead of always requesting permissions from one
     * fragment implementation.
     *
     * First, a few vendor ROMs crash when requestPermissions is called from a framework
     * fragment. In those cases, using the host activity or an AndroidX fragment is more
     * reliable.
     *
     * Second, the callback should stay in the same lifecycle tree as the original caller.
     * If the request starts from an AndroidX fragment, the request fragment should also live
     * in that AndroidX fragment hierarchy. The same rule applies to framework fragments.
     * This avoids callbacks reaching a fragment that is no longer attached.
     *
     * Related issues:
     * 1. https://github.com/getActivity/XXPermissions/issues/339
     * 2. https://github.com/getActivity/XXPermissions/issues/126
     * 3. https://github.com/getActivity/XXPermissions/issues/357
     * 4. https://github.com/getActivity/XXPermissions/issues/365
     */

    @NonNull
    private final A mActivity;

    @NonNull
    private final M mFragmentManager;

    protected PermissionFragmentFactory(@NonNull A activity, @NonNull M fragmentManager) {
        mActivity = activity;
        mFragmentManager = fragmentManager;
    }

    /** Returns the host activity. */
    @NonNull
    protected A getActivity() {
        return mActivity;
    }

    /** Returns the fragment manager used by this factory. */
    @NonNull
    protected M getFragmentManager() {
        return mFragmentManager;
    }

    /** Creates and commits the request fragment. */
    public abstract void createAndCommitFragment(@NonNull List<IPermission> permissions,
                                                 @NonNull PermissionChannel permissionChannel,
                                                 @Nullable OnPermissionFragmentCallback callback);

    /** Creates the fragment arguments for a permission request. */
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
