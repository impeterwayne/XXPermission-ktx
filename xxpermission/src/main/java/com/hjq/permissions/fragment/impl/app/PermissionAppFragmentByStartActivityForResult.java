package com.hjq.permissions.fragment.impl.app;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.hjq.permissions.core.PermissionChannelImpl;
import com.hjq.permissions.core.PermissionChannelImplByStartActivityForResult;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Permission Fragment class ({@link android.app.Fragment} + {@link android.app.Activity#startActivityForResult(Intent, int)})
 */
public final class PermissionAppFragmentByStartActivityForResult extends PermissionAppFragment {

    @NonNull
    private final PermissionChannelImpl mPermissionChannelImpl = new PermissionChannelImplByStartActivityForResult(this);

    @NonNull
    @Override
    public PermissionChannelImpl getPermissionChannelImpl() {
        return mPermissionChannelImpl;
    }
}
