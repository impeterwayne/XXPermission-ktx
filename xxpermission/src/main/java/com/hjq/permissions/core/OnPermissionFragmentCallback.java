package com.hjq.permissions.core;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : Permission Fragment Callback
 */
public interface OnPermissionFragmentCallback {

    /**
     * Callback when requesting permission
     */
    default void onRequestPermissionNow() {
        // default implementation ignored
    }

    /**
     * Callback when permission request is finished
     */
    void onRequestPermissionFinish();

    /**
     * Callback when there is an anomaly in permission request
     */
    default void onRequestPermissionAnomaly() {
        // default implementation ignored
    }
}