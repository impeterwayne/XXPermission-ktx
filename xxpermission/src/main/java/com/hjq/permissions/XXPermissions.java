package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.hjq.permissions.fragment.factory.PermissionFragmentFactory;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactoryByApp;
import com.hjq.permissions.manifest.AndroidManifestParser;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.start.StartActivityAgent;
import com.hjq.permissions.tools.PermissionApi;
import com.hjq.permissions.tools.PermissionChecker;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Android permission request entry class.
 *
 * Provides a fluent API to build and request permissions.
 */
@SuppressWarnings({"unused", "deprecation"})
public final class XXPermissions {

    /** Request code for jumping to the permission settings page */
    public static final int REQUEST_CODE = 1024 + 1;

    /** Type of the permission request interceptor (applies globally) */
    private static Class<? extends OnPermissionInterceptor> sPermissionInterceptorClass;

    /** Type of the permission description handler (applies globally) */
    private static Class<? extends OnPermissionDescription> sPermissionDescriptionClass;

    /** Whether error-checking mode is enabled (applies globally) */
    private static Boolean sCheckMode;

    /**
     * Create a builder bound to the given context.
     *
     * @param context the current Activity; you may pass the top Activity on the stack
     */
    public static XXPermissions with(@NonNull Context context) {
        return new XXPermissions(context);
    }

    public static XXPermissions with(@NonNull Fragment appFragment) {
        return new XXPermissions(appFragment);
    }
    /**
     * Enable or disable error-checking mode (global setting).
     */
    public static void setCheckMode(boolean checkMode) {
        sCheckMode = checkMode;
    }

    /**
     * Set the permission request interceptor (global setting).
     */
    public static void setPermissionInterceptor(Class<? extends OnPermissionInterceptor> clazz) {
        sPermissionInterceptorClass = clazz;
    }

    /**
     * Get the permission request interceptor (global).
     */
    @NonNull
    public static OnPermissionInterceptor getPermissionInterceptor() {
        if (sPermissionInterceptorClass != null) {
            try {
                return sPermissionInterceptorClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DefaultPermissionInterceptor();
    }

    /**
     * Set the permission description handler (global setting).
     *
     * Explanation: Why only expose a Class instead of a normal object?
     * If a normal object were used, that single instance would be reused globally.
     * That can lead to conflicts when fields inside the handler are used concurrently.
     * To avoid such issues, the best solution is to NOT reuse the same instance.
     */
    public static void setPermissionDescription(Class<? extends OnPermissionDescription> clazz) {
        sPermissionDescriptionClass = clazz;
    }

    /**
     * Get the permission description handler (global).
     */
    @NonNull
    public static OnPermissionDescription getPermissionDescription() {
        if (sPermissionDescriptionClass != null) {
            try {
                return sPermissionDescriptionClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DefaultPermissionDescription();
    }

    /** List of permissions to request */
    @NonNull
    private final List<IPermission> mRequestList = new ArrayList<>();

    /** Context object */
    @Nullable
    private final Context mContext;

    /** Fragment from the android.app package */
    @Nullable
    private Fragment mAppFragment;


    /** Permission request interceptor */
    @Nullable
    private OnPermissionInterceptor mPermissionInterceptor;

    /** Permission request description handler */
    @Nullable
    private OnPermissionDescription mPermissionDescription;

    /** Whether to skip error checking */
    @Nullable
    private Boolean mCheckMode;

    private XXPermissions(@NonNull Context context) {
        mContext = context;
    }

    private XXPermissions(@NonNull Fragment appFragment) {
        mAppFragment = appFragment;
        mContext = appFragment.getActivity();
    }

    /**
     * Add a single permission.
     */
    public XXPermissions permission(@NonNull IPermission permission) {
        // Purpose of this approach: if a duplicate permission is added,
        // the last added entry takes precedence.
        mRequestList.remove(permission);
        mRequestList.add(permission);
        return this;
    }

    /**
     * Add multiple permissions.
     */
    public XXPermissions permissions(@NonNull List<IPermission> permissions) {
        if (permissions.isEmpty()) {
            return this;
        }

        for (int i = 0; i < permissions.size(); i++) {
            permission(permissions.get(i));
        }
        return this;
    }

    public XXPermissions permissions(@NonNull IPermission[] permissions) {
        return permissions(PermissionUtils.asArrayList(permissions));
    }

    /**
     * Set the permission request interceptor.
     */
    public XXPermissions interceptor(@Nullable OnPermissionInterceptor permissionInterceptor) {
        mPermissionInterceptor = permissionInterceptor;
        return this;
    }

    /**
     * Set the permission request description handler.
     */
    public XXPermissions description(@Nullable OnPermissionDescription permissionDescription) {
        mPermissionDescription = permissionDescription;
        return this;
    }

    /**
     * Disable the error-checking mechanism for this request.
     */
    public XXPermissions unchecked() {
        mCheckMode = false;
        return this;
    }

    /**
     * Request permissions.
     */
    public void request(@Nullable OnPermissionCallback callback) {
        if (mContext == null) {
            return;
        }

        if (mPermissionInterceptor == null) {
            mPermissionInterceptor = getPermissionInterceptor();
        }

        if (mPermissionDescription == null) {
            mPermissionDescription = getPermissionDescription();
        }

        final Context context = mContext;

        final Fragment appFragment = mAppFragment;

        final OnPermissionInterceptor permissionInterceptor = mPermissionInterceptor;

        final OnPermissionDescription permissionDescription = mPermissionDescription;

        // Permission request list (why not use the field directly?
        // To support both new and old permissions on lower Android versions the framework
        // may auto-add legacy permissions; copying avoids duplicates).
        List<IPermission> requestList = new ArrayList<>(mRequestList);

        // Obtain the Activity from the Context
        Activity activity = PermissionUtils.findActivity(context);

        if (isCheckMode(context)) {
            // Verify that the provided Activity or Fragment state is valid
            PermissionChecker.checkActivityStatus(activity);
            if (appFragment != null) {
                PermissionChecker.checkAppFragmentStatus(appFragment);
            }
            // Validate the requested permissions
            PermissionChecker.checkPermissionList(activity, requestList, AndroidManifestParser.getAndroidManifestInfo(context));
        }

        // Check whether the Activity is unavailable
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        // Optimize the list of requested permissions (e.g., add legacy counterparts for newer ones)
        PermissionApi.addOldPermissionsByNewPermissions(activity, requestList);

        // If all requested permissions are already granted
        if (PermissionApi.isGrantedPermissions(context, requestList)) {
            // Do not request again; notify success directly
            permissionInterceptor.onRequestPermissionEnd(activity, true, requestList, requestList, new ArrayList<>(), callback);
            return;
        }

        final PermissionFragmentFactory<?, ?> fragmentFactory;
        if (appFragment != null) {
            if (PermissionUtils.isFragmentUnavailable(appFragment)) {
                return;
            }
            fragmentFactory = generatePermissionFragmentFactory(activity, appFragment);
        } else {
            fragmentFactory = generatePermissionFragmentFactory(activity);
        }

        // Request the permissions that have not yet been granted
        permissionInterceptor.onRequestPermissionStart(activity, requestList, fragmentFactory, permissionDescription, callback);
    }

    /**
     * Whether we are currently in check mode.
     */
    private boolean isCheckMode(@NonNull Context context) {
        if (mCheckMode == null) {
            if (sCheckMode == null) {
                sCheckMode = PermissionUtils.isDebugMode(context);
            }
            mCheckMode = sCheckMode;
        }
        return mCheckMode;
    }

    /**
     * Determine whether a permission is granted.
     */
    public static boolean isGrantedPermission(@NonNull Context context, @NonNull IPermission permission) {
        return permission.isGrantedPermission(context);
    }

    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return isGrantedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.isGrantedPermissions(context, permissions);
    }

    /**
     * Get granted permissions from a list.
     */
    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return getGrantedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.getGrantedPermissions(context, permissions);
    }

    /**
     * Get denied (not granted) permissions from a list.
     */
    public static List<IPermission> getDeniedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return getDeniedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<IPermission> getDeniedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.getDeniedPermissions(context, permissions);
    }

    /**
     * Check whether two permissions are equal.
     */
    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull IPermission permission2) {
        return PermissionUtils.equalsPermission(permission1, permission2);
    }

    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull String permission2) {
        return PermissionUtils.equalsPermission(permission1, permission2);
    }

    public static boolean equalsPermission(@NonNull String permissionName1, @NonNull String permission2) {
        return PermissionUtils.equalsPermission(permissionName1, permission2);
    }

    /**
     * Check whether a list contains a specific permission.
     */
    public static boolean containsPermission(@NonNull List<IPermission> permissions, @NonNull IPermission permission) {
        return PermissionUtils.containsPermission(permissions, permission);
    }

    public static boolean containsPermission(@NonNull List<IPermission> permissions, @NonNull String permissionName) {
        return PermissionUtils.containsPermission(permissions, permissionName);
    }

    /**
     * Check whether a permission is a health-related permission.
     */
    public static boolean isHealthPermission(@NonNull IPermission permission) {
        return PermissionApi.isHealthPermission(permission);
    }

    /**
     * Determine whether one or more permissions have been marked "Don't ask again".
     *
     * If the checked permissions include dangerous permissions, pay special attention:
     * 2) If the app has not requested a dangerous permission since launch, and you directly
     *    check whether "Don't ask again" is set, the result will be inaccurate. It is
     *    recommended to perform this check in the permission callback; there is no better approach.
     * 3) During a dangerous permission request, if the user cancels by pressing the back button
     *    or tapping outside the system dialog (instead of tapping "Deny"), the result will be
     *    inaccurate. This has no perfect solution.
     */
    public static boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull IPermission permission) {
        return permission.isDoNotAskAgainPermission(activity);
    }

    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull IPermission[] permissions) {
        return isDoNotAskAgainPermissions(activity, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull List<IPermission> permissions) {
        return PermissionApi.isDoNotAskAgainPermissions(activity, permissions);
    }

    /* android.content.Context */

    public static void startPermissionActivity(@NonNull Context context) {
        startPermissionActivity(context, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Context context, @NonNull IPermission... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permissions));
    }

    /**
     * Navigate to the app's permission settings page.
     *
     * @param permissions permission groups that are not granted or were denied
     */
    public static void startPermissionActivity(@NonNull Context context, @NonNull List<IPermission> permissions) {
        Activity activity = PermissionUtils.findActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, permissions);
            return;
        }
        StartActivityAgent.startActivity(context, PermissionApi.getBestPermissionSettingIntent(context, permissions, true));
    }

    /* android.app.Activity */

    public static void startPermissionActivity(@NonNull Activity activity) {
        startPermissionActivity(activity, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull IPermission... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<IPermission> permissions) {
        startPermissionActivity(activity, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<IPermission> permissions,
                                               @IntRange(from = 1, to = 65535) int requestCode) {
        StartActivityAgent.startActivityForResult(activity,
                PermissionApi.getBestPermissionSettingIntent(activity, permissions, true), requestCode);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull IPermission permission,
                                               @Nullable OnPermissionCallback callback) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Activity activity,
                                               @NonNull List<IPermission> permissions,
                                               @Nullable OnPermissionCallback callback) {
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }
        if (permissions.isEmpty()) {
            StartActivityAgent.startActivity(activity, PermissionSettingPage.getCommonPermissionSettingIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity);
        fragmentFactory.createAndCommitFragment(permissions, PermissionChannel.START_ACTIVITY_FOR_RESULT, () -> {
            if (PermissionUtils.isActivityUnavailable(activity)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(@NonNull Fragment appFragment) {
        startPermissionActivity(appFragment, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull IPermission... permissions) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<IPermission> permissions) {
        startPermissionActivity(appFragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<IPermission> permissions,
                                               @IntRange(from = 1, to = 65535) int requestCode) {
        if (PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        Activity activity = appFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            StartActivityAgent.startActivity(appFragment, PermissionSettingPage.getCommonPermissionSettingIntent(activity));
            return;
        }
        StartActivityAgent.startActivityForResult(appFragment,
                PermissionApi.getBestPermissionSettingIntent(activity, permissions, true), requestCode);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull IPermission permission,
                                               @Nullable OnPermissionCallback callback) {
        startPermissionActivity(appFragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Fragment appFragment,
                                               @NonNull List<IPermission> permissions,
                                               @Nullable OnPermissionCallback callback) {
        if (PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        Activity activity = appFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(appFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            StartActivityAgent.startActivity(appFragment, PermissionSettingPage.getCommonPermissionSettingIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, appFragment);
        fragmentFactory.createAndCommitFragment(permissions, PermissionChannel.START_ACTIVITY_FOR_RESULT, () -> {
            if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(appFragment)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
    }

    /**
     * Create a Fragment factory.
     */
    @NonNull
    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity) {
        return generatePermissionFragmentFactory(activity, null);
    }


    @NonNull
    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity,
                                                                                     @Nullable Fragment appFragment) {
        final PermissionFragmentFactory<?, ?> fragmentFactory;
        if (appFragment != null) {
            fragmentFactory = new PermissionFragmentFactoryByApp(appFragment.getActivity(), appFragment.getChildFragmentManager());
        } else {
            fragmentFactory = new PermissionFragmentFactoryByApp(activity, activity.getFragmentManager());
        }
        return fragmentFactory;
    }

    /**
     * Dispatch the callback for returning from the permission settings page.
     */
    private static void dispatchPermissionPageCallback(@NonNull Context context,
                                                       @NonNull List<IPermission> permissions,
                                                       @Nullable OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        List<IPermission> grantedList = new ArrayList<>(permissions.size());
        List<IPermission> deniedList = new ArrayList<>(permissions.size());
        // Iterate through the requested permissions and classify them by grant state
        for (IPermission permission : permissions) {
            if (permission.isGrantedPermission(context, false)) {
                grantedList.add(permission);
            } else {
                deniedList.add(permission);
            }
        }
        callback.onResult(grantedList, deniedList);
    }
}
