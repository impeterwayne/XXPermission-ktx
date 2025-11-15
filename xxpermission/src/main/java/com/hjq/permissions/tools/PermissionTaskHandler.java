package com.hjq.permissions.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/26
 *    desc   : Permission task handler.
 *             Schedules and runs permission-related tasks on the main thread.
 */
public final class PermissionTaskHandler {

    /** Handler object bound to the main thread */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * Post a delayed task.
     */
    public static void sendTask(@NonNull Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * Post a delayed task associated with a specific token.
     */
    public static void sendTask(@NonNull Runnable runnable, @NonNull Object token, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        long uptimeMillis = SystemClock.uptimeMillis() + delayMillis;
        HANDLER.postAtTime(runnable, token, uptimeMillis);
    }

    /**
     * Cancel all tasks associated with the specified token.
     */
    public static void cancelTask(@NonNull Object token) {
        // Remove all callbacks and messages linked to this token
        HANDLER.removeCallbacksAndMessages(token);
    }
}
