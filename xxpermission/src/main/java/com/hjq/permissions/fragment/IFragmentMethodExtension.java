package com.hjq.permissions.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.core.PermissionChannelImpl;

/**
 * Fragment extension interface methods.
 */
public interface IFragmentMethodExtension<M> {

    /**
     * Get the implementation logic for the permission request channel
     */
    @NonNull
    PermissionChannelImpl getPermissionChannelImpl();

    /**
     * Commit Fragment attach
     */
    void commitFragmentAttach(@Nullable M fragmentManager);

    /**
     * Commit Fragment detach
     */
    void commitFragmentDetach();

    /**
     * Set the permission request flow callback
     */
    void setPermissionFragmentCallback(@Nullable OnPermissionFragmentCallback callback);

    /**
     * Sets whether the fragment was restored by a non-system restart.
     */
    void setNonSystemRestartMark(boolean nonSystemRestartMark);
}
