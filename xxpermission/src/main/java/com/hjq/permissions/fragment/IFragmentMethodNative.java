package com.hjq.permissions.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.start.IStartActivityDelegate;

/**
 * Native Fragment interface methods.
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
    void setArguments(@Nullable Bundle arguments);

    /** Whether the current Fragment is attached */
    boolean isAdded();

    /** Whether the current Fragment is being removed */
    boolean isRemoving();
}