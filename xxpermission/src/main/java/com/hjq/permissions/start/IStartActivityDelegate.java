package com.hjq.permissions.start;

import android.content.Intent;
import androidx.annotation.IntRange;

/**
 * Delegate interface for starting activities.
 */
public interface IStartActivityDelegate {

    /**
     * Starts an activity.
     */
    void startActivity(Intent intent);

    /**
     * Starts an activity for a result.
     */
    void startActivityForResult(Intent intent, @IntRange(from = 1, to = 65535) int requestCode);
}
