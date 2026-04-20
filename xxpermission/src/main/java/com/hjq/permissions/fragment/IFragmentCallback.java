package com.hjq.permissions.fragment;

import android.content.Intent;
import androidx.annotation.Nullable;

/**
 * Fragment callback interface.
 */
public interface IFragmentCallback {

    /** Callback when the Fragment becomes visible */
    void onFragmentResume();

    /** Callback when the Fragment is detached */
    void onFragmentDestroy();

    /** Fragment onRequestPermissionsResult callback */
    default void onFragmentRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        // default implementation ignored
    }

    /** Fragment onActivityResult callback */
    default void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // default implementation ignored
    }
}