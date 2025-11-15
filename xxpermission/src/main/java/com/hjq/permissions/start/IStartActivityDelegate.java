package com.hjq.permissions.start;

import android.content.Intent;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : startActivity delegate interface
 */
public interface IStartActivityDelegate {

    /**
     * Launch an Activity
     */
    void startActivity(@NonNull Intent intent);

    /**
     * Launch an Activity (expects a result)
     */
    void startActivityForResult(@NonNull Intent intent, @IntRange(from = 1, to = 65535) int requestCode);
}
