package com.hjq.permissions.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import com.hjq.permissions.tools.PermissionUtils;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Implementation of {@link android.content.Context} for starting Activities
 */
public final class StartActivityDelegateByContext implements IStartActivityDelegate {

    @NonNull
    private final Context mContext;

    public StartActivityDelegateByContext(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public void startActivity(@NonNull Intent intent) {
        Activity activity = PermissionUtils.findActivity(mContext);
        if (activity != null) {
            activity.startActivity(intent);
            return;
        }
        // On Android 9.0 and above, starting an Activity outside of an Activity context
        // requires the FLAG_ACTIVITY_NEW_TASK flag.
        // Reference: https://developer.android.google.cn/about/versions/pie/android-9.0-changes-all?hl=en#fant-required
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, @IntRange(from = 1, to = 65535) int requestCode) {
        Activity activity = PermissionUtils.findActivity(mContext);
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
            return;
        }
        // On Android 9.0 and above, starting an Activity outside of an Activity context
        // requires the FLAG_ACTIVITY_NEW_TASK flag.
        // Reference: https://developer.android.google.cn/about/versions/pie/android-9.0-changes-all?hl=en#fant-required
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
