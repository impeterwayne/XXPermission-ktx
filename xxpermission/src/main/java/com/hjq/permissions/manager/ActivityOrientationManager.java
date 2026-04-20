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
 * Locks and restores activity orientation during permission requests.
 */
public final class ActivityOrientationManager {

    /** Stores Activity orientation values. */
    private static final Map<Integer, Integer> ACTIVITY_ORIENTATION_MAP = new HashMap<>();

    /** Private constructor */
    private ActivityOrientationManager() {
        // default implementation ignored
    }

    /** Locks the current activity orientation. */
    public static synchronized void lockActivityOrientation(@NonNull Activity activity) {
        // If the current screen orientation is not locked, the current orientation and lock it
        int sourceScreenOrientation = activity.getRequestedOrientation();
        if (sourceScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }

        int targetScreenOrientation;
        // Lock the current Activity orientation
        try {
            // Compatibility issue: on Android 8.0 devices, an Activity orientation can be locked, but the Activity must not be translucent or an exception will be thrown
            // Reproduction case: simply set <item name="android:windowIsTranslucent">true</item> in the Activity theme
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

    /** Restores automatic activity orientation. */
    public static synchronized void unlockActivityOrientation(@NonNull Activity activity) {
        // If the current Activity is not locked, return directly
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        Integer targetScreenOrientation = ACTIVITY_ORIENTATION_MAP.get(getIntKeyByActivity(activity));
        if (targetScreenOrientation == null) {
            return;
        }
        // Check whether the Activity was previously set to auto-rotate. This may always be false, but the extra check keeps the code safer
        if (targetScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return;
        }
        // A try/catch is not used here like because this path only removes the fixed Activity orientation. A crash is only likely when explicitly setting landscape or portrait
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ACTIVITY_ORIENTATION_MAP.remove(getIntKeyByActivity(activity));
    }

    /**
     * Check whether the Activity is rotated in the reverse direction.
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

        // Get the Activity rotation angle
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
     * Get an int key from the Activity.
     */
    private static int getIntKeyByActivity(@NonNull Activity activity) {
        // Use the Activity hashCode as the key here so duplicates are avoided
        return activity.hashCode();
    }
}
