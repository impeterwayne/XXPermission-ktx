package com.hjq.permissions.core;

/**
 * Callback for fragment based permission requests.
 */
public interface OnPermissionFragmentCallback {

    /**
     * Called when the permission request is about to start.
     */
    default void onRequestPermissionNow() {
        // default implementation ignored
    }

    /**
     * Called when the permission request finishes.
     */
    void onRequestPermissionFinish();

    /**
     * Called when the request flow ends unexpectedly.
     */
    default void onRequestPermissionAnomaly() {
        // default implementation ignored
    }
}
