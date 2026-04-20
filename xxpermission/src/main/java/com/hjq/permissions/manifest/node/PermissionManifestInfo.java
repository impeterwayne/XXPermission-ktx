package com.hjq.permissions.manifest.node;

import android.content.pm.PackageInfo;
import com.hjq.permissions.tools.PermissionVersion;

/**
 * Permission manifest info class.
 */
public final class PermissionManifestInfo {

    /** Default maximum effective SDK version */
    public static final int DEFAULT_MAX_SDK_VERSION = Integer.MAX_VALUE;

    /** Flag indicating that no location derivation request is needed */
    private static final int REQUESTED_PERMISSION_NEVER_FOR_LOCATION;

    static {
        if (PermissionVersion.isAndroid12()) {
            REQUESTED_PERMISSION_NEVER_FOR_LOCATION = PackageInfo.REQUESTED_PERMISSION_NEVER_FOR_LOCATION;
        } else {
            REQUESTED_PERMISSION_NEVER_FOR_LOCATION = 0x00010000;
        }
    }

    /** Permission name */
    public String name;

    /** Maximum effective SDK version */
    public int maxSdkVersion = DEFAULT_MAX_SDK_VERSION;

    /** Permission usage flags */
    public int usesPermissionFlags;

    /**
     * Whether this permission does not derive location information
     */
    public boolean neverForLocation() {
        return (usesPermissionFlags & REQUESTED_PERMISSION_NEVER_FOR_LOCATION) != 0;
    }
}