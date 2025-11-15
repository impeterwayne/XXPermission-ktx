package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.MetaDataManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionLists;
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
 *    desc   : Read external storage permission class
 */
public final class ReadExternalStoragePermission extends DangerousPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not for external references.
     *  If you need the permission name string, please use {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.READ_EXTERNAL_STORAGE;

    /** Scoped storage Meta Data Key (for internal use only) */
    static final String META_DATA_KEY_SCOPED_STORAGE = "ScopedStorage";

    public static final Parcelable.Creator<ReadExternalStoragePermission> CREATOR = new Parcelable.Creator<ReadExternalStoragePermission>() {

        @Override
        public ReadExternalStoragePermission createFromParcel(Parcel source) {
            return new ReadExternalStoragePermission(source);
        }

        @Override
        public ReadExternalStoragePermission[] newArray(int size) {
            return new ReadExternalStoragePermission[size];
        }
    };

    public ReadExternalStoragePermission() {
        // default implementation ignored
    }

    private ReadExternalStoragePermission(Parcel in) {
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
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_13) {
            return PermissionLists.getReadMediaImagesPermission().isGrantedPermission(context, skipRequest) &&
                    PermissionLists.getReadMediaVideoPermission().isGrantedPermission(context, skipRequest) &&
                    PermissionLists.getReadMediaAudioPermission().isGrantedPermission(context, skipRequest);
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            return PermissionLists.getReadMediaImagesPermission().isDoNotAskAgainPermission(activity) &&
                    PermissionLists.getReadMediaVideoPermission().isDoNotAskAgainPermission(activity) &&
                    PermissionLists.getReadMediaAudioPermission().isDoNotAskAgainPermission(activity);
        }
        return super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);

        // If requesting Android 10's ACCESS_MEDIA_LOCATION permission, skip this check
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
            return;
        }

        int targetSdkVersion = PermissionVersion.getTargetVersion(activity);
        // Whether scoped storage is adapted (default is not adapted)
        boolean scopedStorage = false;
        if (applicationInfo.metaDataInfoList != null) {
            for (MetaDataManifestInfo metaDataManifestInfo : applicationInfo.metaDataInfoList) {
                if (META_DATA_KEY_SCOPED_STORAGE.equals(metaDataManifestInfo.name)) {
                    scopedStorage = Boolean.parseBoolean(metaDataManifestInfo.value);
                    break;
                }
            }
        }

        // If already targeting Android 10
        if (targetSdkVersion >= PermissionVersion.ANDROID_10 && !applicationInfo.requestLegacyExternalStorage && !scopedStorage) {
            // You must register android:requestLegacyExternalStorage="true" in the Application node of the manifest.
            // Otherwise, even if the permission is granted, the app cannot read/write files on external storage normally on Android 10 devices.
            // If your project is fully adapted to scoped storage, register a meta-data tag in the manifest:
            // <meta-data android:name="ScopedStorage" android:value="true" /> to skip this check.
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                    "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }

        // If already targeting Android 11
        if (targetSdkVersion >= PermissionVersion.ANDROID_11 && !scopedStorage) {
            // Option 1: Adapt scoped storage and register a meta-data tag in the manifest:
            // <meta-data android:name="ScopedStorage" android:value="true" />
            // Option 2: If you do not want to adapt scoped storage, you must request Permission.MANAGE_EXTERNAL_STORAGE instead.
            // You must choose one of the two options, otherwise reading/writing external storage files will not work on Android 11 devices.
            // If you’re not sure which to choose, see the documentation: https://github.com/getActivity/XXPermissions/blob/master/HelpDoc
            throw new IllegalArgumentException("The storage permission application is abnormal. If you have adapted the scope storage, " +
                    "please register the <meta-data android:name=\"ScopedStorage\" android:value=\"true\" /> attribute in the AndroidManifest.xml file. " +
                    "If there is no adaptation scope storage, please use \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" to apply for permission");
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            /*
               When the project's targetSdkVersion >= 33, you cannot request READ_EXTERNAL_STORAGE permission.
               Issues:
               - If targetSdkVersion >= 33 requests READ_EXTERNAL_STORAGE or WRITE_EXTERNAL_STORAGE,
                 the system will directly reject it without showing any permission dialog.
               - If the App has adapted scoped storage, you should request READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, or READ_MEDIA_AUDIO permissions.
               - If the App does not need scoped storage adaptation, you should request MANAGE_EXTERNAL_STORAGE instead.
             */
            throw new IllegalArgumentException("When the project targetSdkVersion >= 33, the \"" + PermissionNames.READ_EXTERNAL_STORAGE +
                    "\" permission cannot be applied for, and some problems will occur." + "Because after testing, if targetSdkVersion >= 33 applies for \"" +
                    PermissionNames.READ_EXTERNAL_STORAGE + "\" or \"" + PermissionNames.WRITE_EXTERNAL_STORAGE +
                    "\", it will be directly rejected by the system and no authorization dialog box will be displayed."
                    + "If the App has been adapted for scoped storage, the should be requested \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                    PermissionNames.READ_MEDIA_VIDEO + "\" or \"" + PermissionNames.READ_MEDIA_AUDIO + "\" permission."
                    + "If the App does not need to adapt scoped storage, the should be requested \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" permission");
        }
    }
}
