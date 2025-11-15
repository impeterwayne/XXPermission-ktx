package com.hjq.permissions.manifest.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/11/11
 *    desc   : Activity manifest information class
 */
public final class ActivityManifestInfo {

    /** Class name of the Activity */
    @NonNull
    public String name = "";

    /** Whether the Activity supports picture-in-picture mode */
    public boolean supportsPictureInPicture = false;

    /** List of intent filters */
    @Nullable
    public List<IntentFilterManifestInfo> intentFilterInfoList;

    /** List of MetaData entries */
    @Nullable
    public List<MetaDataManifestInfo> metaDataInfoList;
}
