package com.hjq.permissions.fragment.factory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.hjq.permissions.core.OnPermissionFragmentCallback;
import com.hjq.permissions.fragment.IFragmentMethod;
import com.hjq.permissions.fragment.impl.androidx.PermissionAndroidXFragmentByRequestPermissions;
import com.hjq.permissions.fragment.impl.androidx.PermissionAndroidXFragmentByStartActivity;
import com.hjq.permissions.manager.PermissionRequestCodeManager;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 * Permission fragment factory( {@link androidx.fragment.app.Fragment} ).
 */
public final class PermissionFragmentFactoryByAndroidX extends PermissionFragmentFactory<FragmentActivity, FragmentManager> {

    public PermissionFragmentFactoryByAndroidX(@NonNull FragmentActivity activity, @NonNull FragmentManager fragmentManager) {
        super(activity, fragmentManager);
    }

    @Override
    public void createAndCommitFragment(@NonNull List<IPermission> permissions,
                                        @NonNull PermissionChannel permissionChannel,
                                        @Nullable OnPermissionFragmentCallback callback) {
        IFragmentMethod<FragmentActivity, FragmentManager> fragment;
        if (permissionChannel == PermissionChannel.REQUEST_PERMISSIONS) {
            fragment = new PermissionAndroidXFragmentByRequestPermissions();
        } else {
            fragment = new PermissionAndroidXFragmentByStartActivity();
        }
        // Newer AndroidX versions require the request code to be less than 65536, so the valid range is 1 to 65535
        // java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
        // Older Support library versions require the request code to be less than 256, so the valid range is 1 to 255
        // java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
        // Related issue links:
        // 1. https://stackoverflow.com/questions/33331073/android-what-to-choose-for-requestcode-values
        // 2. https://github.com/domoticz/domoticz-android/issues/92
        // 3. https://github.com/journeyapps/zxing-android-embedded/issues/117
        int maxRequestCode;
        // Check whether the current permission request is using requestPermissions
        if (permissionChannel == PermissionChannel.REQUEST_PERMISSIONS) {
            try {
                FragmentActivity activity = getActivity();
                // Check whether a large requestCode exceeds the limit enforced by FragmentActivity
                // If , current Support version old, Ifnot, current Support versionnot
                // becausenewer Support versionalready corrected issue, requestCode raised the maximum limit from 255 already to 65535
                // Related commit links:
                // Github: https://github.com/androidx/androidx/commit/86f3b80ddf7f9aa5c5b7afe77217cb75632d62a2
                // Google Git: https://android.googlesource.com/platform/frameworks/support/+/86f3b80ddf7f9aa5c5b7afe77217cb75632d62a2
                activity.validateRequestPermissionsRequestCode(PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE);
                // If validateRequestPermissionsRequestCode completes successfully, passing 65535 is either within the allowed range or not restricted there
                maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
            } catch (IllegalArgumentException ignore) {
                // When requestCode value FragmentActivity , this exception is thrown and caught here
                // This proves thatthe incoming requestCode value has , a smaller value, The solution is to switch maxRequestCode to a smaller value
                // java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
                maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_LOW_VALUE;
            } catch (Exception ignore) {
                // If a different exception is thrown, it is likely that validateRequestPermissionsRequestCode API FragmentActivity was removed
                // This proves that FragmentActivity the incoming requestCode valueno , so maxRequestCode switch maxRequestCode to a larger value
                // no , does not really meanno , it means that FragmentActivity no , Activity still has its own limit
                // The requestCode passed to Activity.requestPermissions cannot exceed 65535, otherwise the permission request will fail
                maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
            }
        } else {
            // If special permission no , becausespecial permissionis implemented through startActivityForResult
            // new and old Support version FragmentActivity source code does not impose startActivityForResult passed in requestCode no limit on the value
            // no , does not really meanno , it means that FragmentActivity no , Activity still has its own limit
            // The requestCode passed to Activity.startActivityForResult cannot exceed 65535, otherwise page navigation will fail
            maxRequestCode = PermissionRequestCodeManager.REQUEST_CODE_LIMIT_HIGH_VALUE;
        }
        int requestCode = PermissionRequestCodeManager.generateRandomRequestCode(maxRequestCode);
        fragment.setArguments(generatePermissionArguments(permissions, requestCode));
        fragment.setNonSystemRestartMark(true);
        fragment.setPermissionFragmentCallback(callback);
        fragment.commitFragmentAttach(getFragmentManager());
    }
}