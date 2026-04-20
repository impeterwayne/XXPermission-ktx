package com.hjq.permissions.manager;

import androidx.annotation.IntRange;
import com.hjq.permissions.XXPermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Permission request code manager.
 */
public final class PermissionRequestCodeManager {

    /** Request code limit, low value */
    public static final int REQUEST_CODE_LIMIT_LOW_VALUE = 255;

    /** Request code limit, high value */
    public static final int REQUEST_CODE_LIMIT_HIGH_VALUE = 65535;

    /** Permission request code storage */
    private static final List<Integer> REQUEST_CODE_ARRAY = new ArrayList<>();

    /** Random object */
    private static final Random RANDOM = new Random();

    /** Private constructor */
    private PermissionRequestCodeManager() {
        // default implementation ignored
    }

    /**
     * Generate a random request code
     */
    @IntRange(from = 1, to = 65535)
    public static synchronized int generateRandomRequestCode(@IntRange(from = 1, to = 65535) int maxRequestCode) {
        int requestCode;
        // Request codes are generated randomly, so a loop is required to avoid collisions with earlier request codes.
        // 1. The request code cannot be 0 or negative.
        // 2. The request code cannot equal XXPermissions.REQUEST_CODE.
        // 3. To reduce conflicts with request codes in the current project, smaller request codes are discarded. Testing found the following:
        // a. Requesting permissions through framework Fragments does not trigger the host Activity callbacks onActivityResult and onRequestPermissionsResult.
        // b. Requesting permissions through AndroidX Fragments does trigger the host Activity callbacks onActivityResult and onRequestPermissionsResult.
        // This is because AndroidX Fragment permission callbacks are implemented by overriding Activity onActivityResult and onRequestPermissionsResult.
        // Framework Fragment onActivityResult and onRequestPermissionsResult callbacks are dispatched directly inside Activity.dispatchActivityResult.
        do {
            // maxRequestCode currently has only two practical values: 255 and 65535.
            // 1. If the caller passes 255, the valid request code range is 128 to 254.
            // 2. If the caller passes 65535, the valid request code range is 55536 to 65534.
            // Even with careful handling, request code conflicts are still theoretically possible, though the probability is very low, so callers should avoid using very large request codes.
            // Otherwise they may conflict with framework request codes. This is unlikely, but if the caller uses request codes that are too large, startActivityForResult may appear not to respond.
            // Because of that, very large request codes are uncommon. Even in the worst case, the framework still randomly selects from nearly ten thousand values.
            // So even if a collision happens, the affected range is small and the problem is not deterministic because the value is random. This is the best compromise so far.
            int minRequestCode = maxRequestCode > 20000 ? maxRequestCode - 10000 : maxRequestCode / 2;
            requestCode = RANDOM.nextInt(maxRequestCode - minRequestCode) + minRequestCode;
        } while (requestCode == XXPermissions.REQUEST_CODE || REQUEST_CODE_ARRAY.contains(requestCode));

        // Mark this request code as occupied
        REQUEST_CODE_ARRAY.add(requestCode);
        return requestCode;
    }

    /**
     * Release the reservation for a request code
     */
    public static synchronized void releaseRequestCode(int requestCode) {
        REQUEST_CODE_ARRAY.remove((Integer) requestCode);
    }
}
