package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * Activity manifest info class.
 */
public final class ActivityManifestInfo {

    /** Activity class name */
    @NonNull
    public String name = "";

    /** Whether picture-in-picture is supported */
    public boolean supportsPictureInPicture;

    /** Intent filter list */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;

    /** Meta-data list */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}