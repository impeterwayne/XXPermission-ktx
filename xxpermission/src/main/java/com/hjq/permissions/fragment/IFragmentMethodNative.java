package com.hjq.permissions.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.permissions.start.IStartActivityDelegate;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment native interface methods
 */
public interface IFragmentMethodNative<A extends Activity> extends IStartActivityDelegate {

    /** Get the Activity object */
    @Nullable
    A getActivity();

    /** Request permissions */
    void requestPermissions(@NonNull String[] permissions, @IntRange(from = 1, to = 65535) int requestCode);

    /** Get the argument bundle */
    @Nullable
    Bundle getArguments();

    /** Set the argument bundle */
    void setArguments(@NonNull Bundle arguments);

    /**
     * Set whether to retain the instance.
     * If retained, the Fragment will not be recreated due to screen orientation
     * changes or configuration changes.
     */
    void setRetainInstance(boolean retainInstance);

    /** Check whether the Fragment is currently added (attached) */
    boolean isAdded();

    /** Check whether the Fragment is currently being removed */
    boolean isRemoving();
}
