package com.hjq.permissions.manager;

import androidx.annotation.IntRange;
import com.hjq.permissions.XXPermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Permission request code manager
 */
public final class PermissionRequestCodeManager {

    /** Request code limit: low value */
    public static final int REQUEST_CODE_LIMIT_LOW_VALUE = 255;

    /** Request code limit: high value */
    public static final int REQUEST_CODE_LIMIT_HIGH_VALUE = 65535;

    /** Collection of currently used request codes */
    private static final List<Integer> REQUEST_CODE_ARRAY = new ArrayList<>();

    /** Random number generator */
    private static final Random RANDOM = new Random();

    /** Private constructor */
    private PermissionRequestCodeManager() {
        // default implementation ignored
    }

    /**
     * Randomly generate a request code
     *
     * @param maxRequestCode the maximum allowed request code (either 255 or 65535)
     * @return a unique request code within the valid range
     */
    @IntRange(from = 1, to = 65535)
    public static synchronized int generateRandomRequestCode(@IntRange(from = 1, to = 65535) int maxRequestCode) {
        int requestCode;
        // Rules for generating request codes:
        // 1. The request code cannot be 0 or negative
        // 2. The request code cannot equal XXPermissions.REQUEST_CODE
        // 3. To reduce conflicts with app-defined request codes, we discard smaller values
        //    Observed issues:
        //    a. Using App package Fragments: host Activity does NOT receive onActivityResult/onRequestPermissionsResult
        //    b. Using Support library Fragments: host Activity DOES receive these callbacks
        //    Reason: Support library Fragments forward callbacks by overriding Activity methods,
        //            while App Fragments call Activity.dispatchActivityResult directly.
        do {
            // maxRequestCode can only be 255 or 65535
            // 1. If 255 (rare): valid range is (255 / 2 + 1) ~ (255 - 1) = 128 ~ 254
            // 2. If 65535 (common): valid range is (65535 - 10000 + 1) ~ (65535 - 1) = 55536 ~ 65534
            //
            // Even with strict handling, conflicts are still possible (though rare).
            // If an app developer sets very high request codes, collisions with the framework may occur.
            // In practice this is unlikely, and even if it happens, the framework picks randomly from ~10,000 values,
            // making the chance of conflict very small.
            // If a collision does occur, the impact will be minimal since it's not a consistent failure.
            int minRequestCode = maxRequestCode > 20000 ? maxRequestCode - 10000 : maxRequestCode / 2;
            requestCode = RANDOM.nextInt(maxRequestCode - minRequestCode) + minRequestCode;
        } while (requestCode == XXPermissions.REQUEST_CODE || REQUEST_CODE_ARRAY.contains(requestCode));

        // Mark this request code as used
        REQUEST_CODE_ARRAY.add(requestCode);
        return requestCode;
    }

    /**
     * Release a previously reserved request code
     *
     * @param requestCode the request code to release
     */
    public static synchronized void releaseRequestCode(int requestCode) {
        REQUEST_CODE_ARRAY.remove((Integer) requestCode);
    }
}
