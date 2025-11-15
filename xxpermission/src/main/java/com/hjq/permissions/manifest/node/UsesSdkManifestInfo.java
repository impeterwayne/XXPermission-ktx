package com.hjq.permissions.manifest.node;

/**
 * Represents the manifest information of a {@code <uses-sdk>} element
 * defined in the AndroidManifest.xml.
 *
 * <p>This class is used for parsing and holding SDK-related configuration
 * declared in the manifest, such as minimum installation requirements.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 */
public final class UsesSdkManifestInfo {

    /**
     * The minimum SDK version required to install the application.
     *
     * <p>Corresponds to the {@code android:minSdkVersion} attribute
     * in the manifest.</p>
     */
    public int minSdkVersion;
}
