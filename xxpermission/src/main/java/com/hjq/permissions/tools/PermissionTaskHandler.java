package com.hjq.permissions.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;

/**
 * Permission task handler class.
 */
public final class PermissionTaskHandler {

    /** Handler object */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * Post a delayed task
     */
    public static void sendTask(@NonNull Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * Post a delayed task for a specific token
     */
    public static void sendTask(@NonNull Runnable runnable, @NonNull Object token, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        long uptimeMillis = SystemClock.uptimeMillis() + delayMillis;
        HANDLER.postAtTime(runnable, token, uptimeMillis);
    }

    /**
     * Cancel a task for a specific token
     */
    public static void cancelTask(@NonNull Object token) {
        // Remove message callbacks related to the current object
        HANDLER.removeCallbacksAndMessages(token);
    }
}