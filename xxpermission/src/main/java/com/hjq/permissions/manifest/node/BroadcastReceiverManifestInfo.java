package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * BroadcastReceiver manifest info class.
 */
public final class BroadcastReceiverManifestInfo {

    /** BroadcastReceiver class name */
    @NonNull
    public String name = "";

    /** Permission used */
    @Nullable
    public String permission;

    /** Intent filter list */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;

    /** Meta-data list */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}