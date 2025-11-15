package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * Represents the manifest information of a {@code <service>} element
 * defined in the AndroidManifest.xml.
 *
 * <p>This class is used for parsing and holding relevant service-level
 * configuration details declared in the manifest.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 */
public final class ServiceManifestInfo {

    /**
     * The fully qualified class name of the {@code Service}.
     *
     * <p>Corresponds to the {@code android:name} attribute
     * in the manifest.</p>
     */
    @NonNull
    public String name = "";

    /**
     * The permission required to bind or start this {@code Service}.
     *
     * <p>Corresponds to the {@code android:permission} attribute
     * in the manifest.</p>
     */
    @Nullable
    public String permission;

    /**
     * A list of intent filters associated with this {@code Service}.
     */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;

    /**
     * A list of metadata entries declared under this {@code Service}.
     */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}
