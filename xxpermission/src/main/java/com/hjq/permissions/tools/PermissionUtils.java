package com.hjq.permissions.tools;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.ResolveInfoFlags;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods used by the permission framework.
 */
public final class PermissionUtils {

    /** Returns whether the app is running in debug mode. */
    public static boolean isDebugMode(@NonNull Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * Converts an array to a mutable {@link ArrayList}.
     *
     * <p>{@code Arrays.asList} is not used here because it returns a fixed-size list,
     * and the framework sometimes needs to append more items later.</p>
     */
    @SuppressWarnings("all")
    @NonNull
    public static <T> ArrayList<T> asArrayList(@Nullable T... array) {
        int initialCapacity = 0;
        if (array != null) {
            initialCapacity = array.length;
        }
        ArrayList<T> list = new ArrayList<>(initialCapacity);
        if (array == null || array.length == 0) {
            return list;
        }
        for (T t : array) {
            list.add(t);
        }
        return list;
    }

    /** Finds the {@link Activity} wrapped inside a {@link Context}. */
    @Nullable
    public static Activity findActivity(@Nullable Context context) {
        do {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper) {
                // android.content.ContextWrapper
                // android.content.MutableContextWrapper
                // androidx.appcompat.view.ContextThemeWrapper
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }

    /** Returns whether the activity is unavailable. */
    public static boolean isActivityUnavailable(@Nullable Activity activity) {
        return activity == null || activity.isDestroyed()  || activity.isFinishing();
    }

    /**
     * Returns whether the AndroidX fragment is unavailable.
     */
    @SuppressWarnings("deprecation")
    public static boolean isFragmentUnavailable(@Nullable androidx.fragment.app.Fragment xFragment) {
        return xFragment == null || !xFragment.isAdded() || xFragment.isRemoving();
    }

    /**
     * Returns whether the framework fragment is unavailable.
     */
    @SuppressWarnings("deprecation")
    public static boolean isFragmentUnavailable(@Nullable Fragment fragment) {
        return fragment == null || !fragment.isAdded() || fragment.isRemoving();
    }

    /**
     * Returns whether this intent can resolve to an activity.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean areActivityIntent(@NonNull Context context, @Nullable Intent intent) {
        if (intent == null) {
            return false;
        }
        // Why not use Intent.resolveActivity(intent) != null here?
        // Because on the OPPO R7 Plus, Android 5.0, it can misreport and return a ComponentName even when the Activity does not exist
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }
        if (PermissionVersion.isAndroid13()) {
            return !packageManager.queryIntentActivities(intent,
                    ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY)).isEmpty();
        }
        return !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * Compares two strings from the first character onward.
     */
    public static boolean equalsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        int length = s1.length();
        if (length != s2.length()) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare whether strings are equal, starting from the last character
     */
    public static boolean reverseEqualsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        int length = s1.length();
        if (length != s2.length()) {
            return false;
        }

        for (int i = length - 1; i >= 0; i--) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether two permissions are the same
     */
    public static boolean equalsPermission(@NonNull String permission1, @NonNull String permission2) {
        // Because most permission strings start with android.permission
        // So comparing from the last character can improve equals performance significantly
        return reverseEqualsString(permission1, permission2);
    }

    /**
     * Check whether two permissions are the same
     */
    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull String permission2) {
        // Because most permission strings start with android.permission
        // So comparing from the last character can improve equals performance significantly
        return reverseEqualsString(permission1.getPermissionName(), permission2);
    }

    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull IPermission permission2) {
        // Because most permission strings start with android.permission
        // So comparing from the last character can improve equals performance significantly
        return reverseEqualsString(permission1.getPermissionName(), permission2.getPermissionName());
    }

    /**
     * Check whether the permission collection contains a permission
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull IPermission permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            // Using equalsPermission here improves execution efficiency
            if (equalsPermission(permission, item.getPermissionName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the permission collection contains a permission
     */
    public static boolean containsPermission(@NonNull List<String> permissions, @NonNull String permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (String item : permissions) {
            // Using equalsPermission here improves execution efficiency
            if (equalsPermission(permission, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the permission collection contains a permission
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull String permissionName) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            // Using equalsPermission here improves execution efficiency
            if (equalsPermission(item.getPermissionName(), permissionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert IPermission[] to a List<String>
     */
    @NonNull
    public static List<String> convertPermissionList(@Nullable IPermission[] permissions) {
        List<String> list = new ArrayList<>();
        if (permissions == null) {
            return list;
        }
        for (IPermission permission : permissions) {
            list.add(permission.getPermissionName());
        }
        return list;
    }

    /**
     * Convert List<IPermission> to a String[]
     */
    @NonNull
    public static String[] convertPermissionArray(@Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new String[0];
        }
        String[] list = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            list[i] = permissions.get(i).getPermissionName();
        }
        return list;
    }

    /**
     * Get the package name URI
     */
    public static Uri getPackageNameUri(@NonNull Context context) {
        return Uri.parse("package:" + context.getPackageName());
    }

    /**
     * Check whether a class name exists
     */
    public static boolean isClassExist(@Nullable String className) {
        if (className == null) {
            return false;
        }
        if (className.isEmpty()) {
            return false;
        }
        try {
            // If the class exists, it is considered valid
            // If it does not exist, it is considered invalid and needs to be reauthorized
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compare whether two Intent lists contain the same content
     */
    public static boolean equalsIntentList(@NonNull List<Intent> intentList1, @NonNull List<Intent> intentList2) {
        if (intentList1.size() != intentList2.size() ) {
            return false;
        }

        for (int i = 0; i < intentList1.size(); i++) {
            if (!intentList1.get(i).filterEquals(intentList2.get(i))) {
                return false;
            }
        }
        return true;
    }
}
