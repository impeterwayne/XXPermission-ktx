package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactory;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactoryByAndroid;
import com.hjq.permissions.fragment.factory.PermissionFragmentFactoryByAndroidX;
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
 * Entry point for building and running permission requests.
 */
@SuppressWarnings({"unused", "deprecation"})
public final class XXPermissions {

    /** Request code for the permission settings page */
    public static final int REQUEST_CODE = 1024 + 1;

    /** Global permission interceptor type. */
    private static Class<? extends OnPermissionInterceptor> sPermissionInterceptorClass;

    /** Global permission description type. */
    private static Class<? extends OnPermissionDescription> sPermissionDescriptionClass;

    /** Global validation mode flag. */
    private static Boolean sCheckMode;

    /**
     * Creates a request builder from a context.
     *
     * @param context the current activity context
     */
    public static XXPermissions with(@NonNull Context context) {
        return new XXPermissions(context);
    }

    public static XXPermissions with(@NonNull Fragment fragment) {
        return new XXPermissions(fragment);
    }

    public static XXPermissions with(@NonNull androidx.fragment.app.Fragment xFragment) {
        return new XXPermissions(xFragment);
    }

    /** Sets whether validation mode is enabled globally. */
    public static void setCheckMode(boolean checkMode) {
        sCheckMode = checkMode;
    }

    /** Sets the global permission interceptor. */
    public static void setPermissionInterceptor(Class<? extends OnPermissionInterceptor> clazz) {
        sPermissionInterceptorClass = clazz;
    }

    /** Returns the global permission interceptor. */
    @NonNull
    public static OnPermissionInterceptor getPermissionInterceptor() {
        if (sPermissionInterceptorClass != null) {
            try {
                return sPermissionInterceptorClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DefaultPermissionInterceptor();
    }

    /**
     * Sets the global permission description type.
     *
     * <p>A class is stored instead of an instance so each request can create a fresh object.
     * That avoids state leaking across requests.</p>
     */
    public static void setPermissionDescription(Class<? extends OnPermissionDescription> clazz) {
        sPermissionDescriptionClass = clazz;
    }

    /** Returns the global permission description. */
    @NonNull
    public static OnPermissionDescription getPermissionDescription() {
        if (sPermissionDescriptionClass != null) {
            try {
                return sPermissionDescriptionClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DefaultPermissionDescription();
    }

    /** Requested permission list */
    @NonNull
    private final List<IPermission> mRequestList = new ArrayList<>();

    /** Context object */
    @Nullable
    private final Context mContext;

    /** Framework Fragment instance. */
    @Nullable
    private Fragment mFragment;

    /** AndroidX Fragment instance. */
    @Nullable
    private androidx.fragment.app.Fragment mXFragment;

    /** Permission request interceptor */
    @Nullable
    private OnPermissionInterceptor mPermissionInterceptor;

    /** Permission request description */
    @Nullable
    private OnPermissionDescription mPermissionDescription;

    /** Disable validation */
    @Nullable
    private Boolean mCheckMode;

    private XXPermissions(@NonNull Context context) {
        mContext = context;
    }

    private XXPermissions(@NonNull Fragment fragment) {
        mFragment = fragment;
        mContext = fragment.getActivity();
    }

    private XXPermissions(@NonNull androidx.fragment.app.Fragment xFragment) {
        mXFragment = xFragment;
        mContext = xFragment.getActivity();
    }

    /**
     * Add a single permission
     */
    public XXPermissions permission(@NonNull IPermission permission) {
        // This keeps the last added permission when the same permission is added repeatedly
        mRequestList.remove(permission);
        mRequestList.add(permission);
        return this;
    }

    /**
     * Add multiple permissions
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
     * Sets the permission request interceptor.
     */
    public XXPermissions interceptor(@Nullable OnPermissionInterceptor permissionInterceptor) {
        mPermissionInterceptor = permissionInterceptor;
        return this;
    }

    /**
     * Sets the permission request description.
     */
    public XXPermissions description(@Nullable OnPermissionDescription permissionDescription) {
        mPermissionDescription = permissionDescription;
        return this;
    }

    /**
     * Disable the validation mechanism
     */
    public XXPermissions unchecked() {
        mCheckMode = false;
        return this;
    }

    /**
     * Request permissions
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

        final Fragment fragment = mFragment;

        final androidx.fragment.app.Fragment xFragment = mXFragment;

        final OnPermissionInterceptor permissionInterceptor = mPermissionInterceptor;

        final OnPermissionDescription permissionDescription = mPermissionDescription;

        // Permission request list. A local variable is used instead of the field because the framework adds legacy permissions automatically on older versions, which avoids duplicate entries.
        List<IPermission> requestList = new ArrayList<>(mRequestList);

        // Get the Activity from the Context object
        Activity activity = PermissionUtils.findActivity(context);

        if (isCheckMode(context)) {
            // Check whether the passed Activity or Fragment state is valid
            PermissionChecker.checkActivityStatus(activity);
            if (fragment != null) {
                PermissionChecker.checkAndroidFragmentStatus(fragment);
            } else if (xFragment != null) {
                PermissionChecker.checkAndroidXFragmentStatus(xFragment);
            }
            // Check whether the passed permissions are valid
            PermissionChecker.checkPermissionList(activity, requestList, AndroidManifestParser.getAndroidManifestInfo(context));
        }

        // Check whether the Activity is unavailable
        if (PermissionUtils.isActivityUnavailable(activity)) {
            return;
        }

        // Optimize the requested permission list
        PermissionApi.addOldPermissionsByNewPermissions(activity, requestList);

        // Check whether all requested permissions have already been granted
        if (PermissionApi.isGrantedPermissions(context, requestList)) {
            // If so, skip the request and notify success directly
            permissionInterceptor.onRequestPermissionEnd(activity, true, requestList, requestList, new ArrayList<>(), callback);
            return;
        }

        final PermissionFragmentFactory<?, ?> fragmentFactory;
        if (xFragment != null) {
            if (PermissionUtils.isFragmentUnavailable(xFragment)) {
                return;
            }
            fragmentFactory = generatePermissionFragmentFactory(activity, xFragment);
        } else if (fragment != null) {
            if (PermissionUtils.isFragmentUnavailable(fragment)) {
                return;
            }
            fragmentFactory = generatePermissionFragmentFactory(activity, fragment);
        } else {
            fragmentFactory = generatePermissionFragmentFactory(activity);
        }

        // Request permissions that have not been granted
        permissionInterceptor.onRequestPermissionStart(activity, requestList, fragmentFactory, permissionDescription, callback);
    }

    /** Returns whether validation mode is enabled. */
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
     * Check whether one or more permissions are all granted
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
     * Get granted permissions from the permission list
     */
    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return getGrantedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<IPermission> getGrantedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.getGrantedPermissions(context, permissions);
    }

    /**
     * Get ungranted permissions from the permission list
     */
    public static List<IPermission> getDeniedPermissions(@NonNull Context context, @NonNull IPermission[] permissions) {
        return getDeniedPermissions(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<IPermission> getDeniedPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        return PermissionApi.getDeniedPermissions(context, permissions);
    }

    /**
     * Check whether two permissions are equal
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
     * Check whether the permission list contains a permission
     */
    public static boolean containsPermission(@NonNull List<IPermission> permissions, @NonNull IPermission permission) {
        return PermissionUtils.containsPermission(permissions, permission);
    }

    public static boolean containsPermission(@NonNull List<IPermission> permissions, @NonNull String permissionName) {
        return PermissionUtils.containsPermission(permissions, permissionName);
    }

    /**
     * Check whether a permission is a health permission
     */
    public static boolean isHealthPermission(@NonNull IPermission permission) {
        return PermissionApi.isHealthPermission(permission);
    }

    /**
     * Returns whether one or more permissions have been marked as Do not ask again.
     * For dangerous permissions, this result is most reliable when checked from the
     * permission callback after the user has interacted with the system dialog.
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
     * Opens the app permission settings page.
     *
     * @param permissions the denied or ungranted permissions
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
        fragmentFactory.createAndCommitFragment(permissions, PermissionChannel.START_ACTIVITY, () -> {
            if (PermissionUtils.isActivityUnavailable(activity)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(@NonNull Fragment fragment) {
        startPermissionActivity(fragment, new ArrayList<>(0));
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull IPermission... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull List<IPermission> permissions) {
        startPermissionActivity(fragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull List<IPermission> permissions,
                                               @IntRange(from = 1, to = 65535) int requestCode) {
        if (PermissionUtils.isFragmentUnavailable(fragment)) {
            return;
        }
        Activity activity = fragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(fragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            StartActivityAgent.startActivity(fragment, PermissionSettingPage.getCommonPermissionSettingIntent(activity));
            return;
        }
        StartActivityAgent.startActivityForResult(fragment,
            PermissionApi.getBestPermissionSettingIntent(activity, permissions, true), requestCode);
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                                @NonNull IPermission permission,
                                                @Nullable OnPermissionCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull Fragment fragment,
                                               @NonNull List<IPermission> permissions,
                                               @Nullable OnPermissionCallback callback) {
        if (PermissionUtils.isFragmentUnavailable(fragment)) {
            return;
        }
        Activity activity = fragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(fragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            StartActivityAgent.startActivity(fragment, PermissionSettingPage.getCommonPermissionSettingIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, fragment);
        fragmentFactory.createAndCommitFragment(permissions, PermissionChannel.START_ACTIVITY, () -> {
            if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(fragment)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
    }

    /* androidx.fragment.app.Fragment */

    public static void startPermissionActivity(@NonNull androidx.fragment.app.Fragment xFragment) {
        startPermissionActivity(xFragment, new ArrayList<>());
    }

    public static void startPermissionActivity(@NonNull androidx.fragment.app.Fragment xFragment,
                                               @NonNull IPermission... permissions) {
        startPermissionActivity(xFragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(@NonNull androidx.fragment.app.Fragment xFragment,
                                               @NonNull List<IPermission> permissions) {
        startPermissionActivity(xFragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(@NonNull androidx.fragment.app.Fragment xFragment,
                                               @NonNull List<IPermission> permissions,
                                               @IntRange(from = 1, to = 65535) int requestCode) {
        if (PermissionUtils.isFragmentUnavailable(xFragment)) {
            return;
        }
        Activity activity = xFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(xFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            StartActivityAgent.startActivity(xFragment, PermissionSettingPage.getCommonPermissionSettingIntent(activity));
            return;
        }
        StartActivityAgent.startActivityForResult(xFragment,
            PermissionApi.getBestPermissionSettingIntent(activity, permissions, true), requestCode);
    }

    public static void startPermissionActivity(@NonNull androidx.fragment.app.Fragment xFragment,
                                               @NonNull IPermission permission,
                                               @Nullable OnPermissionCallback callback) {
        startPermissionActivity(xFragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(@NonNull androidx.fragment.app.Fragment xFragment,
                                               @NonNull List<IPermission> permissions,
                                               @Nullable OnPermissionCallback callback) {
        if (PermissionUtils.isFragmentUnavailable(xFragment)) {
            return;
        }
        Activity activity = xFragment.getActivity();
        if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(xFragment)) {
            return;
        }
        if (permissions.isEmpty()) {
            StartActivityAgent.startActivity(xFragment, PermissionSettingPage.getCommonPermissionSettingIntent(activity));
            return;
        }
        PermissionFragmentFactory<?, ?> fragmentFactory = generatePermissionFragmentFactory(activity, xFragment);
        fragmentFactory.createAndCommitFragment(permissions, PermissionChannel.START_ACTIVITY, () -> {
            if (PermissionUtils.isActivityUnavailable(activity) || PermissionUtils.isFragmentUnavailable(xFragment)) {
                return;
            }
            dispatchPermissionPageCallback(activity, permissions, callback);
        });
    }

    /**
     * Create the Fragment factory
     */
    @NonNull
    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity) {
        return generatePermissionFragmentFactory(activity, null, null);
    }

    @NonNull
    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity,
                                                                                     @Nullable androidx.fragment.app.Fragment xFragment) {
        return generatePermissionFragmentFactory(activity, xFragment, null);
    }

    @NonNull
    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity,
                                                                                     @Nullable Fragment fragment) {
        return generatePermissionFragmentFactory(activity, null, fragment);
    }

    private static PermissionFragmentFactory<?, ?> generatePermissionFragmentFactory(@NonNull Activity activity,
                                                                                     @Nullable androidx.fragment.app.Fragment xFragment,
                                                                                     @Nullable Fragment fragment) {
        final PermissionFragmentFactory<?, ?> fragmentFactory;
        if (xFragment != null) {
            fragmentFactory = new PermissionFragmentFactoryByAndroidX(xFragment.getActivity(), xFragment.getChildFragmentManager());
        } else if (fragment != null) {
            fragmentFactory = new PermissionFragmentFactoryByAndroid(fragment.getActivity(), fragment.getChildFragmentManager());
        } else if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = ((FragmentActivity) activity);
            fragmentFactory = new PermissionFragmentFactoryByAndroidX(fragmentActivity, fragmentActivity.getSupportFragmentManager());
        } else {
            fragmentFactory = new PermissionFragmentFactoryByAndroid(activity, activity.getFragmentManager());
        }
        return fragmentFactory;
    }

    /**
     * Dispatch the permission settings page callback
     */
    private static void dispatchPermissionPageCallback(@NonNull Context context,
                                                       @NonNull List<IPermission> permissions,
                                                       @Nullable OnPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        List<IPermission> grantedList = new ArrayList<>(permissions.size());
        List<IPermission> deniedList = new ArrayList<>(permissions.size());
        // Traverse the requested permissions and classify them by grant state
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
