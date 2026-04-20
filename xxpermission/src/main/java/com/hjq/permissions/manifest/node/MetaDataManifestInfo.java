package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Meta-data manifest info class.
 */
public final class MetaDataManifestInfo {

    /** Meta-data name */
    @NonNull
    public String name = "";

    /** Meta-data value */
    @Nullable
    public String value;

    /** Meta-data resource ID */
    public int resource;
}