package com.hjq.permissions.fragment;

import android.content.Intent;

import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment callback interface
 */
public interface IFragmentCallback {

    /** Callback when the Fragment becomes visible */
    void onFragmentResume();

    /** Callback when the Fragment is detached */
    void onFragmentDestroy();

    /** Callback for Fragment's onRequestPermissionsResult method */
    default void onFragmentRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        // default implementation ignored
    }

    /** Callback for Fragment's onActivityResult method */
    default void onFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // default implementation ignored
    }
}
