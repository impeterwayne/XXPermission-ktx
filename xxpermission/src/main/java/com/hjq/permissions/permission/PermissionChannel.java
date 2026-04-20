package com.hjq.permissions.permission;

import android.content.Intent;

/**
 * Permission request channel.
 */
public enum PermissionChannel {

    /** {@link android.app.Activity#requestPermissions(String[], int)} */
    REQUEST_PERMISSIONS,
    /** {@link android.app.Activity#startActivityForResult(Intent, int)} */
    START_ACTIVITY
}