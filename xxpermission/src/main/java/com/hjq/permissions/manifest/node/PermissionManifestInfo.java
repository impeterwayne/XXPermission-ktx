package com.hjq.permissions.manifest.node;

import android.content.pm.PackageInfo;
import com.hjq.permissions.tools.PermissionVersion;

/**
 * Represents the manifest information of a {@code <uses-permission>} element
 * declared in the AndroidManifest.xml.
 *
 * <p>This class holds permission-related details such as the name,
 * maximum SDK version, and usage flags.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 */
public final class PermissionManifestInfo {

    /** Default maximum effective SDK version */
    public static final int DEFAULT_MAX_SDK_VERSION = Integer.MAX_VALUE;

    /** Flag indicating that the permission does not require location derivation */
    private static final int REQUESTED_PERMISSION_NEVER_FOR_LOCATION;

    static {
        if (PermissionVersion.isAndroid12()) {
            REQUESTED_PERMISSION_NEVER_FOR_LOCATION = PackageInfo.REQUESTED_PERMISSION_NEVER_FOR_LOCATION;
        } else {
            // Fallback constant value for lower Android versions
            REQUESTED_PERMISSION_NEVER_FOR_LOCATION = 0x00010000;
        }
    }

    /** The name of the permission */
    public String name;

    /** The maximum SDK version for which this permission is effective */
    public int maxSdkVersion = DEFAULT_MAX_SDK_VERSION;

    /** Permission usage flags */
    public int usesPermissionFlags;

    /**
     * Determines whether this permission is marked as
     * {@code neverForLocation}, meaning it cannot be used
     * to infer location information.
     *
     * @return {@code true} if the permission does not allow location derivation,
     *         {@code false} otherwise
     */
    public boolean neverForLocation() {
        return (usesPermissionFlags & REQUESTED_PERMISSION_NEVER_FOR_LOCATION) != 0;
    }
}
