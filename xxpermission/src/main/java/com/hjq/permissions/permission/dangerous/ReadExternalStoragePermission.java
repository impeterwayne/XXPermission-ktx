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
 * read external storage permission.
 */
public final class ReadExternalStoragePermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.READ_EXTERNAL_STORAGE;
    /** storage Meta Data Key( ) */
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
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetSdkVersion(context) >= PermissionVersion.ANDROID_13) {
            return PermissionLists.getReadMediaImagesPermission().isGrantedPermission(context, skipRequest) &&
                PermissionLists.getReadMediaVideoPermission().isGrantedPermission(context, skipRequest) &&
                PermissionLists.getReadMediaAudioPermission().isGrantedPermission(context, skipRequest);
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_13) {
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
        // If this is the Android 10 media location permission request, skip this check.
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
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

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_13) {
            /*
             * When the project targetSdkVersion is 33 or higher, READ_EXTERNAL_STORAGE cannot be requested safely.
             * Tests show that requesting READ_EXTERNAL_STORAGE or WRITE_EXTERNAL_STORAGE at that targetSdk level
             * is rejected directly by the system without showing any permission dialog.
             * If the app already supports scoped storage, request READ_MEDIA_IMAGES, READ_MEDIA_VIDEO,
             * or READ_MEDIA_AUDIO instead. If the app does not use scoped storage, request
             * MANAGE_EXTERNAL_STORAGE instead.
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
