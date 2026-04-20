package com.hjq.permissions.fragment.impl.android;

import androidx.annotation.NonNull;
import com.hjq.permissions.core.PermissionChannelImpl;
import com.hjq.permissions.core.PermissionChannelImplByRequestPermissions;

/**
 * Permission fragment class( {@link android.app.Fragment} + {@link android.app.Activity#requestPermissions(String[], int)} ).
 */
public final class PermissionAndroidFragmentByRequestPermissions extends PermissionAndroidFragment {

    @NonNull
    private final PermissionChannelImpl mPermissionChannelImpl = new PermissionChannelImplByRequestPermissions(this);

    @NonNull
    @Override
    public PermissionChannelImpl getPermissionChannelImpl() {
        return mPermissionChannelImpl;
    }
}