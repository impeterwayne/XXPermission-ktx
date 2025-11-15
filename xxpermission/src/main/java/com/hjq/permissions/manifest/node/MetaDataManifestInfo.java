package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Represents the manifest information of a {@code <meta-data>} element
 * defined inside AndroidManifest.xml.
 *
 * <p>This class is used for parsing and holding metadata details
 * declared under components such as {@code Application}, {@code Activity},
 * {@code Service}, or {@code BroadcastReceiver}.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2025/08/10
 */
public final class MetaDataManifestInfo {

    /**
     * The name of the {@code <meta-data>} entry.
     *
     * <p>Corresponds to the attribute
     * {@code android:name} in the manifest.</p>
     */
    @NonNull
    public String name = "";

    /**
     * The value of the {@code <meta-data>} entry.
     *
     * <p>Corresponds to the attribute
     * {@code android:value} in the manifest.</p>
     */
    @Nullable
    public String value;

    /**
     * The resource ID associated with the {@code <meta-data>} entry.
     *
     * <p>Corresponds to the attribute
     * {@code android:resource} in the manifest.</p>
     */
    public int resource;
}
