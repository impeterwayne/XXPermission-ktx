package com.hjq.permissions.start;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.Iterator;
import java.util.List;

/**
 * Activity launch agent.
 */
public final class StartActivityAgent {

    public static void startActivity(@NonNull Context context,
                                     @NonNull List<Intent> intentList) {
        startActivity(context, new StartActivityDelegateByContext(context), intentList);
    }

    public static void startActivity(@NonNull Activity activity,
                                     @NonNull List<Intent> intentList) {
        startActivity(activity, new StartActivityDelegateByActivity(activity), intentList);
    }

    @SuppressWarnings("deprecation")
    public static void startActivity(@NonNull Fragment fragment,
                                     @NonNull List<Intent> intentList) {
        startActivity(fragment.getActivity(), new StartActivityDelegateByFragmentAndroid(fragment), intentList);
    }

    public static void startActivity(@NonNull androidx.fragment.app.Fragment fragment,
                                     @NonNull List<Intent> intentList) {
        startActivity(fragment.getActivity(), new StartActivityDelegateByFragmentAndroidX(fragment), intentList);
    }

    public static void startActivity(@NonNull Context context,
                                     @NonNull IStartActivityDelegate delegate,
                                     @NonNull List<Intent> intentList) {
        Iterator<Intent> iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (PermissionUtils.areActivityIntent(context, intent)) {
                continue;
            }
            // Remove Intents that do not exist. This has the following benefits:
            // 1. Navigating with a non-existent Intent will always fail(If the project targets Android 11, note that package visibility support may also be required)
            // 2. During debug sessions, this makes it easy to see which Intents actually exist and compare the Intent list before and after filtering
            iterator.remove();
        }

        // When none of the Intents exist, add an Android system settings Intent by default for the following reasons:
        // This prevents the user from seeing an immediate failure as soon as a permission is requested. At minimum, navigating to the Android system settings page provides a much better experience
        if (intentList.isEmpty()) {
            intentList.add(PermissionSettingPage.getAndroidSettingsIntent());
        }

        // Because Iterator does not provide a way to reset its index, the only option here is to obtain a new Iterator instance
        iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (intent == null) {
                continue;
            }
            try {
                delegate.startActivity(intent);
                // Navigation succeeded, so stop the loop
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startActivityForResult(@NonNull Activity activity,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(activity, new StartActivityDelegateByActivity(activity), intentList, requestCode);
    }

    @SuppressWarnings("deprecation")
    public static void startActivityForResult(@NonNull Fragment fragment,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(fragment.getActivity(), new StartActivityDelegateByFragmentAndroid(fragment), intentList, requestCode);
    }

    public static void startActivityForResult(@NonNull androidx.fragment.app.Fragment fragment,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(fragment.getActivity(), new StartActivityDelegateByFragmentAndroidX(fragment), intentList, requestCode);
    }

    public static void startActivityForResult(@NonNull Context context,
                                              @NonNull IStartActivityDelegate delegate,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode) {
        startActivityForResult(context, delegate, intentList, requestCode, null);
    }

    public static void startActivityForResult(@NonNull Context context,
                                              @NonNull IStartActivityDelegate delegate,
                                              @NonNull List<Intent> intentList,
                                              @IntRange(from = 1, to = 65535) int requestCode,
                                              @Nullable Runnable ignoreActivityResultCallback) {
        Iterator<Intent> iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (PermissionUtils.areActivityIntent(context, intent)) {
                continue;
            }
            // Remove Intents that do not exist. This has the following benefits:
            // 1. Navigating with a non-existent Intent will always fail(If the project targets Android 11, note that package visibility support may also be required)
            // 2. During debug sessions, this makes it easy to see which Intents actually exist and compare the Intent list before and after filtering
            iterator.remove();
        }

        // When none of the Intents exist, add an Android system settings Intent by default for the following reasons:
        // 1. This prevents the user from seeing an immediate failure as soon as a permission is requested. At minimum, navigating to the Android system settings page provides a much better experience
        // 2. Even if navigation to the Android system settings page also fails, this still lets the system trigger onActivityResult so the entire permission request flow can complete
        if (intentList.isEmpty()) {
            intentList.add(PermissionSettingPage.getAndroidSettingsIntent());
        }

        // Because Iterator does not provide a way to reset its index, the only option here is to obtain a new Iterator instance
        iterator = intentList.iterator();
        while (iterator.hasNext()) {
            Intent intent = iterator.next();
            if (intent == null) {
                continue;
            }
            try {
                delegate.startActivityForResult(intent, requestCode);
                // Navigation succeeded, so stop the loop
                break;
            } catch (Exception e) {
                // android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.settings.APPLICATION_DETAILS_SETTINGS dat=package:xxx.xxx.xxx }
                // java.lang.SecurityException: Permission Denial: starting Intent { act=android.settings.MANAGE_UNKNOWN_APP_SOURCES (has data) cmp=xxxx/.xxx }
                e.printStackTrace();
                // Only trigger the failure callback when the next Intent is not null, because a null next Intent means there are no more Intents left to try,
                // so there is no need to count this navigation failure. In that case the earlier startActivityForResult failure will still cause the system to invoke onActivityResult and close the flow correctly
                if (iterator.hasNext() && ignoreActivityResultCallback != null) {
                    ignoreActivityResultCallback.run();
                }
            }
        }
    }
}