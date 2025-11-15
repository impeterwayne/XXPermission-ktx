package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.IntentFilterManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : VPN permission class
 */
public final class BindVpnServicePermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.BIND_VPN_SERVICE;

    public static final Parcelable.Creator<BindVpnServicePermission> CREATOR = new Parcelable.Creator<BindVpnServicePermission>() {

        @Override
        public BindVpnServicePermission createFromParcel(Parcel source) {
            return new BindVpnServicePermission(source);
        }

        @Override
        public BindVpnServicePermission[] newArray(int size) {
            return new BindVpnServicePermission[size];
        }
    };

    public BindVpnServicePermission() {
        // default implementation ignored
    }

    private BindVpnServicePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        // On OPPO systems with Android 15 and above, the VPN permission is an opaque Activity page
        if (DeviceOs.isColorOs() && PermissionVersion.isAndroid15()) {
            return PermissionPageType.OPAQUE_ACTIVITY;
        }
        return VpnService.prepare(context) != null ? PermissionPageType.TRANSPARENT_ACTIVITY : PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_4_0;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        return VpnService.prepare(context) == null;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(2);
        intentList.add(VpnService.prepare(context));
        intentList.add(getAndroidSettingIntent());
        return intentList;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // Check whether any Service class is registered with the attribute android:permission="android.permission.BIND_VPN_SERVICE"
        List<ServiceManifestInfo> serviceInfoList = manifestInfo.serviceInfoList;
        for (int i = 0; i < serviceInfoList.size(); i++) {

            ServiceManifestInfo serviceInfo = serviceInfoList.get(i);
            String permission = serviceInfo.permission;

            if (permission == null) {
                continue;
            }

            if (!PermissionUtils.equalsPermission(this, permission)) {
                continue;
            }

            String action = "android.net.VpnService";
            // Whether the VPN service intent has been registered
            boolean registeredVpnServiceAction = false;
            List<IntentFilterManifestInfo> intentFilterInfoList = serviceInfo.intentFilterInfoList;
            if (intentFilterInfoList != null) {
                for (IntentFilterManifestInfo intentFilterInfo : intentFilterInfoList) {
                    if (intentFilterInfo.actionList.contains(action)) {
                        registeredVpnServiceAction = true;
                        break;
                    }
                }
            }
            if (registeredVpnServiceAction) {
                // Requirements are met, break all loops and return to avoid hitting the exception-throwing code below
                return;
            }

            String xmlCode = "\t\t<intent-filter>\n"
                    + "\t\t    <action android:name=\"" + action + "\" />\n"
                    + "\t\t</intent-filter>";
            throw new IllegalArgumentException("Please add an intent filter for \"" + serviceInfo.name +
                    "\" in the AndroidManifest.xml file.\n" + xmlCode);
        }

        /*
         No Service was found that registered the attribute android:permission="android.permission.BIND_VPN_SERVICE".
         Please register this attribute to a subclass of VpnService in the AndroidManifest.xml file.
         */
        throw new IllegalArgumentException("No Service was found to have registered the android:permission=\"" + getPermissionName() +
                "\" property, Please register this property to VpnService subclass by AndroidManifest.xml file, "
                + "otherwise it will lead to can't apply for the permission");
    }
}
