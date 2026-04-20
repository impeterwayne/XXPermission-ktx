package com.hjq.permissions.manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.BroadcastReceiverManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.manifest.node.UsesSdkManifestInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Manifest parsing bean class.
 */
public final class AndroidManifestInfo {

    /** Application package name */
    @NonNull
    public String packageName = "";

    /** Uses-sdk info */
    @Nullable
    public UsesSdkManifestInfo usesSdkInfo;

    /** Permission node info */
    @NonNull
    public final List<PermissionManifestInfo> permissionInfoList = new ArrayList<>();

    /** Queried package name list */
    @NonNull
    public final List<String> queriesPackageList = new ArrayList<>();

    /** Application node info */
    @Nullable
    public ApplicationManifestInfo applicationInfo;

    /** Activity node info */
    @NonNull
    public final List<ActivityManifestInfo> activityInfoList = new ArrayList<>();

    /** Service node info */
    @NonNull
    public final List<ServiceManifestInfo> serviceInfoList = new ArrayList<>();

    /** BroadcastReceiver node info */
    @NonNull
    public final List<BroadcastReceiverManifestInfo> receiverInfoList = new ArrayList<>();
}