package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * Represents the manifest information of a {@code BroadcastReceiver} node
 * defined in the AndroidManifest.xml.
 *
 * <p>This class is used for parsing and holding relevant receiver-level
 * configuration details declared in the manifest.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 */
public final class BroadcastReceiverManifestInfo {

    /**
     * The fully qualified class name of the {@code BroadcastReceiver}.
     * Defaults to an empty string if not defined.
     */
    @NonNull
    public String name = "";

    /**
     * The permission required to receive the broadcast.
     *
     * <p>This corresponds to the {@code android:permission} attribute
     * in the manifest.</p>
     */
    @Nullable
    public String permission;

    /**
     * A list of intent filters associated with this {@code BroadcastReceiver}.
     */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;

    /**
     * A list of metadata entries declared under the {@code BroadcastReceiver} node.
     */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}
