package com.hjq.permissions.fragment;

import android.app.Activity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/20
 *    desc   : Fragment interface methods
 */
public interface IFragmentMethod<A extends Activity, M> extends IFragmentMethodNative<A>, IFragmentMethodExtension<M> {}
