package com.hjq.permissions.fragment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.core.PermissionChannelImpl;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment extension interface methods
 */
public interface IFragmentMethodExtension<M> {

    /**
     * Get the implementation logic for the permission request channel
     */
    @NonNull
    PermissionChannelImpl getPermissionChannelImpl();

    /**
     * Commit fragment attachment
     */
    void commitFragmentAttach(@Nullable M fragmentManager);

    /**
     * Commit fragment detachment
     */
    void commitFragmentDetach();

    /**
     * Set the callback for the permission request process
     */
    void setPermissionFragmentCallback(@Nullable OnPermissionFragmentCallback callback);

    /**
     * Set a non-system restart mark
     */
    void setNonSystemRestartMark(boolean nonSystemRestartMark);
}
