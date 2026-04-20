package com.hjq.permissions.fragment.impl.androidx;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.fragment.IFragmentMethod;

/**
 * Base permission fragment built on {@link androidx.fragment.app.Fragment}.
 */
public abstract class PermissionAndroidXFragment extends Fragment implements IFragmentMethod<FragmentActivity, FragmentManager> {

    /**
     * Sets the permission callback.
     */
    @Override
    public void setPermissionFragmentCallback(@Nullable OnPermissionFragmentCallback callback) {
        getPermissionChannelImpl().setPermissionFragmentCallback(callback);
    }

    /**
     * Sets whether this fragment was recreated by a non-system restart.
     */
    @Override
    public void setNonSystemRestartMark(boolean nonSystemRestartMark) {
        getPermissionChannelImpl().setNonSystemRestartMark(nonSystemRestartMark);
    }

    /**
     * Attaches the fragment.
     */
    @Override
    public void commitFragmentAttach(@Nullable FragmentManager fragmentManager) {
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss();
    }

    /**
     * Detaches the fragment.
     */
    @Override
    public void commitFragmentDetach() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPermissionChannelImpl().onFragmentResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPermissionChannelImpl().onFragmentDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPermissionChannelImpl().onFragmentRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPermissionChannelImpl().onFragmentActivityResult(requestCode, resultCode, data);
    }
}
