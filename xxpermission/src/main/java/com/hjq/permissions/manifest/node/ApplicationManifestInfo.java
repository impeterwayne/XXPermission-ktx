package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * Represents the manifest information of an {@code Application} node
 * defined in the AndroidManifest.xml.
 *
 * <p>This class is used for parsing and holding relevant application-level
 * configuration details declared in the manifest.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 */
public final class ApplicationManifestInfo {

    /**
     * The fully qualified class name of the {@code Application}.
     * Defaults to an empty string if not defined.
     */
    @NonNull
    public String name = "";

    /**
     * Indicates whether the application requests to ignore
     * the scoped storage (partitioned storage) behavior.
     *
     * <p>This corresponds to the manifest attribute
     * {@code android:requestLegacyExternalStorage}.</p>
     */
    public boolean requestLegacyExternalStorage;

    /**
     * A list of metadata entries declared under the {@code Application} node.
     */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}
