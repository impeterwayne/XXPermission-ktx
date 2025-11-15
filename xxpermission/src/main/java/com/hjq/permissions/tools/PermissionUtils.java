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
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : General permission utilities.
 *             Helpers for Activity/Fragment resolution, string comparison,
 *             permission list operations, and intent checks.
 */
public final class PermissionUtils {

    /**
     * Whether the current app is in debug mode.
     */
    public static boolean isDebugMode(@NonNull Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * Convert an array into an {@link ArrayList}.
     *
     * Why not use Arrays.asList?
     * 1. The return type is not java.util.ArrayList but java.util.Arrays.ArrayList.
     * 2. That list is fixed-size (cannot add/remove), otherwise it throws an exception.
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

    /**
     * Find an {@link Activity} from a given {@link Context}.
     */
    @Nullable
    public static Activity findActivity(@Nullable Context context) {
        do {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper) {
                // Handles ContextWrapper, MutableContextWrapper, ContextThemeWrapper, etc.
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }

    /**
     * Whether an Activity is unavailable.
     */
    public static boolean isActivityUnavailable(@Nullable Activity activity) {
        return activity == null || activity.isDestroyed() || activity.isFinishing();
    }

    /**
     * Whether an app Fragment (android.app.Fragment) is unavailable.
     */
    @SuppressWarnings("deprecation")
    public static boolean isFragmentUnavailable(@Nullable Fragment appFragment) {
        return appFragment == null || !appFragment.isAdded() || appFragment.isRemoving();
    }

    /**
     * Check whether there is an Activity that can handle the given Intent.
     *
     * Why not use intent.resolveActivity(packageManager)?
     * On OPPO R7 Plus (Android 5.0) it may incorrectly return a ComponentName even if none exists.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean areActivityIntent(@NonNull Context context, @Nullable Intent intent) {
        if (intent == null) {
            return false;
        }
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
     * Compare two strings for equality (from the beginning).
     */
    public static boolean equalsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.hashCode() == s2.hashCode()) {
            return true;
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
     * Compare two strings for equality (from the end).
     */
    public static boolean reverseEqualsString(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.hashCode() == s2.hashCode()) {
            return true;
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
     * Whether two permission strings are the same.
     *
     * Optimization: since most permission strings start with "android.permission",
     * comparing from the end improves performance.
     */
    public static boolean equalsPermission(@NonNull String permission1, @NonNull String permission2) {
        return reverseEqualsString(permission1, permission2);
    }

    /**
     * Whether a permission object matches a permission string.
     */
    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull String permission2) {
        return reverseEqualsString(permission1.getPermissionName(), permission2);
    }

    /**
     * Whether two permission objects are the same.
     */
    public static boolean equalsPermission(@NonNull IPermission permission1, @NonNull IPermission permission2) {
        return reverseEqualsString(permission1.getPermissionName(), permission2.getPermissionName());
    }

    /**
     * Whether a collection of permission objects contains a given permission.
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull IPermission permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            if (equalsPermission(permission, item.getPermissionName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether a list of permission strings contains a given permission.
     */
    public static boolean containsPermission(@NonNull List<String> permissions, @NonNull String permission) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (String item : permissions) {
            if (equalsPermission(permission, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether a collection of permission objects contains a given permission name.
     */
    public static boolean containsPermission(@NonNull Collection<IPermission> permissions, @NonNull String permissionName) {
        if (permissions.isEmpty()) {
            return false;
        }
        for (IPermission item : permissions) {
            if (equalsPermission(item.getPermissionName(), permissionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert an array of IPermission into a list of Strings.
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
     * Convert a list of IPermission into a String[] of permission names.
     */
    @NonNull
    public static String[] convertPermissionArray(@NonNull Context context, @Nullable List<IPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new String[0];
        }
        String[] list = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            list[i] = permissions.get(i).getRequestPermissionName(context);
        }
        return list;
    }

    /**
     * Get the package name as a Uri.
     */
    public static Uri getPackageNameUri(@NonNull Context context) {
        return Uri.parse("package:" + context.getPackageName());
    }

    /**
     * Whether a class name exists on the classpath.
     */
    public static boolean isClassExist(@Nullable String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compare two lists of Intents for equality (order and filterEquals match).
     */
    public static boolean equalsIntentList(@NonNull List<Intent> intentList1, @NonNull List<Intent> intentList2) {
        if (intentList1.size() != intentList2.size()) {
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
