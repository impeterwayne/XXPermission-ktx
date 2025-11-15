package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ApplicationManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : All files access permission class
 */
public final class ManageExternalStoragePermission extends SpecialPermission {

    /**
     * Current permission name.
     * Note: This constant field is only for internal use by the framework, not for external reference.
     * If you need to get the permission name string, please use the {@link PermissionNames} class directly.
     */
    public static final String PERMISSION_NAME = PermissionNames.MANAGE_EXTERNAL_STORAGE;

    public static final Parcelable.Creator<ManageExternalStoragePermission> CREATOR = new Parcelable.Creator<ManageExternalStoragePermission>() {

        @Override
        public ManageExternalStoragePermission createFromParcel(Parcel source) {
            return new ManageExternalStoragePermission(source);
        }

        @Override
        public ManageExternalStoragePermission[] newArray(int size) {
            return new ManageExternalStoragePermission[size];
        }
    };

    public ManageExternalStoragePermission() {
        // default implementation ignored
    }

    private ManageExternalStoragePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_11;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // On Android 10 and below, accessing full file management requires read/write external storage permissions
        return PermissionUtils.asArrayList(PermissionLists.getReadExternalStoragePermission(),
                PermissionLists.getWriteExternalStoragePermission());
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid11()) {
            // This is a historical issue on Android 10: if applying for MANAGE_EXTERNAL_STORAGE permission,
            // you must register android:requestLegacyExternalStorage="true" in AndroidManifest.xml
            // API Environment.isExternalStorageLegacy explanation: whether non-scoped storage mode is used
            if (PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
                return false;
            }
            return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest) &&
                    PermissionLists.getWriteExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        // Whether the app has all files access permission
        return Environment.isExternalStorageManager();
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        if (PermissionVersion.isAndroid11()) {
            intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intentList.add(intent);
        }

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Indicates that this permission needs to be statically registered in the AndroidManifest.xml file
        return true;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // If the version where the permission appears is greater than minSdkVersion,
        // it means that the permission may be requested on older systems,
        // so old version permissions must be registered in AndroidManifest.xml
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.READ_EXTERNAL_STORAGE, PermissionVersion.ANDROID_10);
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.WRITE_EXTERNAL_STORAGE, PermissionVersion.ANDROID_10);
        }

        // If applying for Android 10's media location permission, skip this check
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
            return;
        }

        // If targeting Android 10 or above, but the android:requestLegacyExternalStorage attribute is false
        // (default false if not declared in manifest)
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_10 && !applicationInfo.requestLegacyExternalStorage) {
            // Please register android:requestLegacyExternalStorage="true" in the Application node of AndroidManifest.xml
            // Otherwise, even with permission granted, the app cannot properly read/write external storage files on Android 10 devices
            // If your project has fully adapted scoped storage, register a meta-data tag in the manifest:
            // <meta-data android:name="ScopedStorage" android:value="true" /> to skip this check
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                    "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // Check if old storage permissions exist. If yes, throw exception.
        // Do not dynamically request these permissions yourself.
        // The framework will automatically add and request them on versions below Android 10.
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE) ||
                PermissionUtils.containsPermission(requestList, PermissionNames.WRITE_EXTERNAL_STORAGE)) {
            throw new IllegalArgumentException("If you have applied for \"" + getPermissionName() + "\" permissions, " +
                    "do not apply for the \"" + PermissionNames.READ_EXTERNAL_STORAGE +
                    "\" or \"" + PermissionNames.WRITE_EXTERNAL_STORAGE + "\" permissions");
        }

        // Because the MANAGE_EXTERNAL_STORAGE permission scope is very large,
        // it already allows reading media files, so there's no need to request additional media permissions
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
                PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO) ||
                PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_AUDIO)) {
            throw new IllegalArgumentException("Because the \"" + getPermissionName() + "\" permission range is very large, "
                    + "you can read media files with it, and there is no need to apply for additional media permissions.");
        }
    }
}
