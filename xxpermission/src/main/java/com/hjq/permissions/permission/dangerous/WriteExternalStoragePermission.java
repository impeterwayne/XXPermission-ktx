package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Write external storage permission class.
 */
public final class WriteExternalStoragePermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.WRITE_EXTERNAL_STORAGE;
    /** storage Meta Data Key( ) */
    static final String META_DATA_KEY_SCOPED_STORAGE = ReadExternalStoragePermission.META_DATA_KEY_SCOPED_STORAGE;

    public static final Parcelable.Creator<WriteExternalStoragePermission> CREATOR = new Parcelable.Creator<WriteExternalStoragePermission>() {

        @Override
        public WriteExternalStoragePermission createFromParcel(Parcel source) {
            return new WriteExternalStoragePermission(source);
        }

        @Override
        public WriteExternalStoragePermission[] newArray(int size) {
            return new WriteExternalStoragePermission[size];
        }
    };

    public WriteExternalStoragePermission() {
        // default implementation ignored
    }

    private WriteExternalStoragePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.STORAGE;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_6;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetSdkVersion(context) >= PermissionVersion.ANDROID_11) {
            // here this means reason:
            // 1. When targetSdk >= Android 11 version request WRITE_EXTERNAL_STORAGE, authorization dialog, no effect
            // relateddocumentation link: https://developer.android.google.cn/reference/android/Manifest.permission#WRITE_EXTERNAL_STORAGE
            // https://developer.android.google.cn/about/versions/11/privacy/storage?hl=zh-cn#permissions-target-11
            // will manifest filedeclare android:maxSdkVersion="29" attribute, this meanscauses WRITE_EXTERNAL_STORAGE permission requestfailure, hereneed toreturn true caller
            // 2. When targetSdk >= Android 13 version request WRITE_EXTERNAL_STORAGE, will systemdirectlydenied
            // will systemauthorization dialog, framework Android version callbackresult , hereneed toreturn true caller
            // reason, soWhenproject targetSdk >= Android 11 Android 11 above devices
            // check WRITE_EXTERNAL_STORAGE permission, result whether granted, will directlyreturn true caller
            return true;
        }
        // If the current project targetSdk > Android 10 Android 10 devices,
        // storage case , directlyreturn true caller( granted permission)
        if (PermissionVersion.getTargetSdkVersion(context) >= PermissionVersion.ANDROID_10 &&
                PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
            return true;
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_11) {
            return false;
        }
        // If the current project targetSdk > Android 10 Android 10 devices,
        // storage case , directlyreturn false caller( no Do not ask again)
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_10 &&
                PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
            return false;
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // checkmanifest permissionhas nodeclare, check, permission , need to check
        return false;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull AndroidManifestInfo manifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
            return;
        }

        // If the current targetSdk version , also no storage version, directly after it check, Check whether the current permission is statically declared in the manifest file
        if (PermissionVersion.getTargetSdkVersion(activity) < PermissionVersion.ANDROID_10) {
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName());
            return;
        }

        // check: current projectwhether Android 11, also in the manifest filewhether declare MANAGE_EXTERNAL_STORAGE permission
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_11 &&
            findPermissionInfoByList(permissionInfoList, PermissionNames.MANAGE_EXTERNAL_STORAGE) != null) {
            // Ifhas , maxSdkVersion must Android 10 above version
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName(), PermissionVersion.ANDROID_10);
        } else {
            // check permissionhas nodeclare in the manifest file, WRITE_EXTERNAL_STORAGE permission , check
            // Ifdeclared in the manifest file android:requestLegacyExternalStorage="true" attribute, Android version
            // so requestLegacyExternalStorage attribute state , maxSdkVersion attribute version
            checkPermissionRegistrationStatus(
                permissionInfoList, getPermissionName(), applicationInfo.requestLegacyExternalStorage ?
                                                        PermissionVersion.ANDROID_10 : PermissionVersion.ANDROID_9);
        }

        // Ifrequest Android 10medialocationpermission, after it check
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        int targetSdkVersion = PermissionVersion.getTargetSdkVersion(activity);
        // whether storage(default no )
        boolean scopedStorage = false;
        if (applicationInfo.metaDataInfoList != null) {
            for (MetaDataManifestInfo metaDataManifestInfo : applicationInfo.metaDataInfoList) {
                if (META_DATA_KEY_SCOPED_STORAGE.equals(metaDataManifestInfo.name)) {
                    scopedStorage = Boolean.parseBoolean(metaDataManifestInfo.value);
                    break;
                }
            }
        }
        // If already Android 10 case
        if (targetSdkVersion >= PermissionVersion.ANDROID_10 && !applicationInfo.requestLegacyExternalStorage && !scopedStorage) {
            // please manifest file Application node declare android:requestLegacyExternalStorage="true" attribute
            // otherwise request permission, Android 10 device storage
            // If projectalready storage, please declare in the manifest file meta-data attribute
            // <meta-data android:name="ScopedStorage" android:value="true" /> check
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }

        // If already Android 11 case
        if (targetSdkVersion >= PermissionVersion.ANDROID_11 && !scopedStorage) {
            // 1. storage feature, declare in the manifest file meta-data attribute
            // <meta-data android:name="ScopedStorage" android:value="true" />
            // 2. If storage, need to Permission.MANAGE_EXTERNAL_STORAGE request permission
            // need to , otherwise Android 11 device storage
            // If , documentation: https://github.com/getActivity/XXPermissions/blob/master/HelpDoc
            throw new IllegalArgumentException("The storage permission application is abnormal. If you have adapted the scope storage, " +
                "please register the <meta-data android:name=\"ScopedStorage\" android:value=\"true\" /> attribute in the AndroidManifest.xml file. " +
                "If there is no adaptation scope storage, please use \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" to apply for permission");
        }
    }
}