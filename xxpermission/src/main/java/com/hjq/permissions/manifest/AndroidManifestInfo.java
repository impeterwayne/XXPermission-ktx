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
 * Represents the parsed information of an {@code AndroidManifest.xml} file.
 *
 * <p>This bean class aggregates various manifest node details,
 * including application, activities, services, receivers, permissions,
 * and SDK requirements.</p>
 *
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 */
public final class AndroidManifestInfo {

    /**
     * The package name of the application.
     *
     * <p>Corresponds to the {@code package} attribute
     * in the manifest root element.</p>
     */
    @NonNull
    public String packageName = "";

    /**
     * Information about the {@code <uses-sdk>} element.
     */
    @Nullable
    public UsesSdkManifestInfo usesSdkInfo;

    /**
     * List of declared {@code <uses-permission>} elements.
     */
    @NonNull
    public final List<PermissionManifestInfo> permissionInfoList = new ArrayList<>();

    /**
     * List of package names declared inside the {@code <queries>} element.
     *
     * <p>Introduced in Android 11 (API 30) for package visibility control.</p>
     */
    @NonNull
    public final List<String> queriesPackageList = new ArrayList<>();

    /**
     * Information about the {@code <application>} element.
     */
    @Nullable
    public ApplicationManifestInfo applicationInfo;

    /**
     * List of declared {@code <activity>} elements.
     */
    @NonNull
    public final List<ActivityManifestInfo> activityInfoList = new ArrayList<>();

    /**
     * List of declared {@code <service>} elements.
     */
    @NonNull
    public final List<ServiceManifestInfo> serviceInfoList = new ArrayList<>();

    /**
     * List of declared {@code <receiver>} elements.
     */
    @NonNull
    public final List<BroadcastReceiverManifestInfo> receiverInfoList = new ArrayList<>();
}
