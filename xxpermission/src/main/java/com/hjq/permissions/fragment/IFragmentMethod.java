package com.hjq.permissions.fragment;

import android.app.Activity;

/**
 * Fragment interface methods.
 */
public interface IFragmentMethod<A extends Activity, M> extends IFragmentMethodNative<A>, IFragmentMethodExtension<M> {}