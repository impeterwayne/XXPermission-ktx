package com.hjq.permissions.start;

import android.app.Fragment;
import android.content.Intent;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Implementation of {@link android.app.Fragment} for starting Activities
 */
@SuppressWarnings("deprecation")
public final class StartActivityDelegateByFragmentApp implements IStartActivityDelegate {

    @NonNull
    private final Fragment mFragment;

    public StartActivityDelegateByFragmentApp(@NonNull Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void startActivity(@NonNull Intent intent) {
        mFragment.startActivity(intent);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, @IntRange(from = 1, to = 65535) int requestCode) {
        mFragment.startActivityForResult(intent, requestCode);
    }
}
