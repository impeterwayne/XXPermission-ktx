package com.hjq.permissions.fragment.factory;

import android.app.Activity;
import android.app.FragmentManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.fragment.impl.android.PermissionAndroidFragmentByRequestPermissions;
import com.hjq.permissions.fragment.impl.android.PermissionAndroidFragmentByStartActivity;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 * Permission fragment factory( {@link android.app.Fragment} ).
 */
@SuppressWarnings("deprecation")
public final class PermissionFragmentFactoryByAndroid extends PermissionFragmentFactory<Activity, FragmentManager> {

    public PermissionFragmentFactoryByAndroid(@NonNull Activity activity, @NonNull FragmentManager fragmentManager) {
        super(activity, fragmentManager);
    }

    @Override
    public void createAndCommitFragment(@NonNull List<IPermission> permissions,
                                        @NonNull PermissionChannel permissionChannel,
                                        @Nullable OnPermissionFragmentCallback callback) {
        IFragmentMethod<Activity, FragmentManager> fragment;
        if (permissionChannel == PermissionChannel.REQUEST_PERMISSIONS) {
            fragment = new PermissionAndroidFragmentByRequestPermissions();
        } else {
            fragment = new PermissionAndroidFragmentByStartActivity();
        }
        int maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
        int requestCode = PermissionRequestCodeManager.generateRandomRequestCode(maxRequestCode);
        fragment.setArguments(generatePermissionArguments(permissions, requestCode));
        fragment.setNonSystemRestartMark(true);
        fragment.setPermissionFragmentCallback(callback);
        fragment.commitFragmentAttach(getFragmentManager());
    }
}