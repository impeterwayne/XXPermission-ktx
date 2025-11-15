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
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Permission class for writing to external storage
 */
public final class WriteExternalStoragePermission extends DangerousPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.WRITE_EXTERNAL_STORAGE;

    /** Scoped storage Meta Data Key (for internal use only) */
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
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_11) {
            // Explanation of why this always returns true:
            // 1. When targetSdk >= Android 11 and WRITE_EXTERNAL_STORAGE is requested,
            //    although a permission dialog may appear, it has no actual effect.
            //    Docs:
            //    https://developer.android.google.cn/reference/android/Manifest.permission#WRITE_EXTERNAL_STORAGE
            //    https://developer.android.google.cn/about/versions/11/privacy/storage?hl=en#permissions-target-11
            //    Developers may declare android:maxSdkVersion="29" in the manifest,
            //    which would cause WRITE_EXTERNAL_STORAGE to fail. In that case we must return true.
            // 2. When targetSdk >= Android 13 and WRITE_EXTERNAL_STORAGE is requested,
            //    the system rejects it directly without showing a dialog.
            //    To maintain consistent results across versions, the framework also returns true here.
            // Based on these two reasons, when targetSdk >= 11 and running on Android 11+ devices,
            // the check always returns true regardless of actual permission state.
            return true;
        }
        // If targetSdk > Android 10 and running on Android 10 devices,
        // but scoped storage is enabled, return true to simulate granted permission.
        if (PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_10 &&
                PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
            return true;
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_11) {
            return false;
        }
        // If targetSdk > Android 10 and running on Android 10 devices,
        // but scoped storage is enabled, return false to simulate "not checked as 'Do not ask again'".
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_10 &&
                PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
            return false;
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Do not use the parent’s default check for manifest registration.
        // This permission is more complex and requires custom checks.
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

        // If targetSdk < Android 10, skip scoped storage checks and only verify static manifest registration
        if (PermissionVersion.getTargetVersion(activity) < PermissionVersion.ANDROID_10) {
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName());
            return;
        }

        // If targetSdk >= Android 11 and MANAGE_EXTERNAL_STORAGE is declared in manifest,
        // then WRITE_EXTERNAL_STORAGE must have maxSdkVersion >= Android 10.
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_11 &&
                findPermissionInfoByList(permissionInfoList, PermissionNames.MANAGE_EXTERNAL_STORAGE) != null) {
            checkPermissionRegistrationStatus(permissionInfoList, getPermissionName(), PermissionVersion.ANDROID_10);
        } else {
            // Special handling for WRITE_EXTERNAL_STORAGE.
            // If android:requestLegacyExternalStorage="true" is set, extend support by one version.
            checkPermissionRegistrationStatus(
                    permissionInfoList, getPermissionName(), applicationInfo.requestLegacyExternalStorage ?
                            PermissionVersion.ANDROID_10 : PermissionVersion.ANDROID_9);
        }

        // Skip further checks if requesting Android 10 ACCESS_MEDIA_LOCATION permission.
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        int targetSdkVersion = PermissionVersion.getTargetVersion(activity);
        // Whether scoped storage is adapted (default = false)
        boolean scopedStorage = false;
        if (applicationInfo.metaDataInfoList != null) {
            for (MetaDataManifestInfo metaDataManifestInfo : applicationInfo.metaDataInfoList) {
                if (META_DATA_KEY_SCOPED_STORAGE.equals(metaDataManifestInfo.name)) {
                    scopedStorage = Boolean.parseBoolean(metaDataManifestInfo.value);
                    break;
                }
            }
        }
        // If targeting Android 10 but not using requestLegacyExternalStorage nor ScopedStorage,
        // external storage read/write will not work.
        // Developers must either set requestLegacyExternalStorage="true"
        // or explicitly declare <meta-data android:name="ScopedStorage" android:value="true" /> in manifest.
        if (targetSdkVersion >= PermissionVersion.ANDROID_10 && !applicationInfo.requestLegacyExternalStorage && !scopedStorage) {
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                    "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }

        // If targeting Android 11 and not using ScopedStorage:
        // Option 1: Adapt scoped storage and declare <meta-data android:name="ScopedStorage" android:value="true" /> in manifest.
        // Option 2: Use MANAGE_EXTERNAL_STORAGE permission instead.
        // One of the two approaches is required; otherwise external storage read/write won’t work on Android 11 devices.
        // See docs: https://github.com/getActivity/XXPermissions/blob/master/HelpDoc
        if (targetSdkVersion >= PermissionVersion.ANDROID_11 && !scopedStorage) {
            throw new IllegalArgumentException("The storage permission application is abnormal. If you have adapted the scope storage, " +
                    "please register the <meta-data android:name=\"ScopedStorage\" android:value=\"true\" /> attribute in the AndroidManifest.xml file. " +
                    "If there is no adaptation scope storage, please use \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" to apply for permission");
        }
    }
}
