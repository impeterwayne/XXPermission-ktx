package com.hjq.permissions.manager;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.hjq.permissions.tools.PermissionVersion;

import java.util.HashMap;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Activity screen orientation manager
 */
public final class ActivityOrientationManager {

    /** Stores the mapping of Activity and its locked orientation */
    private static final Map<Integer, Integer> ACTIVITY_ORIENTATION_MAP = new HashMap<>();

    /** Private constructor */
    private ActivityOrientationManager() {
        // default implementation ignored
    }

    /**
     * Lock the Activity orientation
     */
    public static synchronized void lockActivityOrientation(@NonNull Activity activity) {
        // If the screen orientation is not currently unspecified, just return
        int sourceScreenOrientation = activity.getRequestedOrientation();
        if (sourceScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }

        int targetScreenOrientation;
        // Lock the current Activity orientation
        try {
            // Compatibility issue: On Android 8.0 devices, you can lock the Activity orientation,
            // but the Activity cannot be translucent, otherwise an exception will be thrown.
            // Reproduction scenario: Set <item name="android:windowIsTranslucent">true</item> in the Activity theme.
            switch (activity.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    targetScreenOrientation = isActivityReverse(activity) ?
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE :
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    activity.setRequestedOrientation(targetScreenOrientation);
                    ACTIVITY_ORIENTATION_MAP.put(getIntKeyByActivity(activity), targetScreenOrientation);
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    targetScreenOrientation = isActivityReverse(activity) ?
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT :
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    activity.setRequestedOrientation(targetScreenOrientation);
                    ACTIVITY_ORIENTATION_MAP.put(getIntKeyByActivity(activity), targetScreenOrientation);
                    break;
                default:
                    break;
            }
        } catch (IllegalStateException e) {
            // java.lang.IllegalStateException: Only fullscreen activities can request orientation
            e.printStackTrace();
        }
    }

    /**
     * Unlock the Activity orientation
     */
    public static synchronized void unlockActivityOrientation(@NonNull Activity activity) {
        // If the Activity has not locked its orientation, return
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        Integer targetScreenOrientation = ACTIVITY_ORIENTATION_MAP.get(getIntKeyByActivity(activity));
        if (targetScreenOrientation == null) {
            return;
        }
        // Check if the Activity was previously set to unspecified orientation
        // (this check may always be false, but is kept for robustness)
        if (targetScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        // Why no try/catch here like above?
        // Because here we are resetting the orientation to unspecified.
        // Crashes only occur when forcing portrait/landscape, not when unlocking.
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * Determine whether the Activity is rotated in reverse
     */
    @SuppressWarnings("deprecation")
    private static boolean isActivityReverse(@NonNull Activity activity) {
        Display display = null;
        if (PermissionVersion.isAndroid11()) {
            display = activity.getDisplay();
        } else {
            WindowManager windowManager = activity.getWindowManager();
            if (windowManager != null) {
                display = windowManager.getDefaultDisplay();
            }
        }

        if (display == null) {
            return false;
        }

        // Get the rotation angle of the Activity
        int activityRotation = display.getRotation();
        switch (activityRotation) {
            case Surface.ROTATION_180:
            case Surface.ROTATION_270:
                return true;
            case Surface.ROTATION_0:
            case Surface.ROTATION_90:
            default:
                return false;
        }
    }

    /**
     * Get an int key based on the Activity
     */
    private static int getIntKeyByActivity(@NonNull Activity activity) {
        // Use the Activity's hashCode as the key to avoid duplicates
        return activity.hashCode();
    }
}
