package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Intent filter.
 */
public final class IntentFilterManifestInfo {

    /** Action list */
    @NonNull
    public final List<String> actionList = new ArrayList<>();

    /** Category list */
    @NonNull
    public final List<String> categoryList = new ArrayList<>();
}