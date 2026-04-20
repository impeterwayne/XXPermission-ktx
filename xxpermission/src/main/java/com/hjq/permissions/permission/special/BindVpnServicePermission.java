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
 * VPN permission class.
 */
public final class BindVpnServicePermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // VPN permission ColorOS devices will opaque Activity page case, the test results are as follows:
        // ColorOS 16.0.0(Beta)Android 15 OPPO Find X8: opaque Activity
        // ColorOS 16.0.0(Beta)Android 15 13: opaque Activity
        // ColorOS 15.0.2 Android 15 OPPO Find X8s+: transparent Activity
        // ColorOS 15.0.1 Android 15 2 Pro: transparent Activity
        // ColorOS 15.0.0 Android 15 OPPO Pad2: opaque Activity
        // ColorOS 15.0.0 Android 15 12: opaque Activity
        // ColorOS 14.1.0 Android 14 OPPO Find X7: transparent Activity
        // ColorOS 14.0.1 Android 14 OPPO A3 Pro 5G: transparent Activity
        // ColorOS 14.0.0 Android 14 Reno8 Pro: transparent Activity
        if (DeviceOs.isColorOs() && (DeviceOs.getOsBigVersionCode() >= 16 ||
                                     DeviceOs.getOsVersionName().startsWith("15.0.0"))) {
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
        // check whether any Service class declares android:permission="android.permission.BIND_VPN_SERVICE" attribute
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
            // current whether declare VPN service Intent
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
                // The requirements are satisfied, so stop all loops and return to avoid reaching the exception code below
                return;
            }

            String xmlCode = "\t\t<intent-filter>\n"
                           + "\t\t    <action android:name=\"" + action + "\" />\n"
                           + "\t\t</intent-filter>";
            throw new IllegalArgumentException("Please add an intent filter for \"" + serviceInfo.name +
                                               "\" in the AndroidManifest.xml file.\n" + xmlCode);
        }

        /*
         * No Service was found with the android:permission="android.permission.BIND_VPN_SERVICE" attribute.
         * Register this attribute on the VpnService subclass in the AndroidManifest.xml file.
         */
        throw new IllegalArgumentException("No Service was found to have registered the android:permission=\"" + getPermissionName() +
            "\" property, Please register this property to VpnService subclass by AndroidManifest.xml file, "
            + "otherwise it will lead to can't apply for the permission");
    }
}
