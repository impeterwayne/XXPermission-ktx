package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * Service manifest info class.
 */
public final class ServiceManifestInfo {

    /** Service class name */
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