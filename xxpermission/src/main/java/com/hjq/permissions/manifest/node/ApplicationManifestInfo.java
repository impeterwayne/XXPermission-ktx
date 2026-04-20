package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * Application manifest info class.
 */
public final class ApplicationManifestInfo {

    /** Application class name */
    @NonNull
    public String name = "";

    /** Whether scoped storage is ignored */
    public boolean requestLegacyExternalStorage;

    /** Meta-data list */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}