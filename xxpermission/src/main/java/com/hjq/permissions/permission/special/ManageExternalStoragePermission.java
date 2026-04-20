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
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * All files access permission class.
 */
public final class ManageExternalStoragePermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // Android 11 below manageneed to storage permission
        return PermissionUtils.asArrayList(PermissionLists.getReadExternalStoragePermission(),
                                            PermissionLists.getWriteExternalStoragePermission());
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid11()) {
            // Android 10 issue, request MANAGE_EXTERNAL_STORAGE permission
            // must AndroidManifest.xml declare android:requestLegacyExternalStorage="true"
            // Environment.isExternalStorageLegacy API : whether storage
            if (PermissionVersion.isAndroid10() && !Environment.isExternalStorageLegacy()) {
                return false;
            }
            return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest) &&
                PermissionLists.getWriteExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        // whether the all-files access permission is granted
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
        // Indicates that this permission must be declared statically in AndroidManifest.xml.
        return true;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // If the version where this permission was introduced is lower than minSdkVersion, it may be requested on older systems, so the legacy permission must also be declared in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.READ_EXTERNAL_STORAGE, PermissionVersion.ANDROID_10);
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.WRITE_EXTERNAL_STORAGE, PermissionVersion.ANDROID_10);
        }

        // If this is the Android 10 media location permission request, skip this check.
        if (PermissionUtils.containsPermission(requestList, PermissionNames.ACCESS_MEDIA_LOCATION)) {
            return;
        }

        ApplicationManifestInfo applicationInfo = manifestInfo.applicationInfo;
        if (applicationInfo == null) {
            return;
        }

        // If already Android 10 case , android:requestLegacyExternalStorage attribute false( nodeclare attribute case value false)
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_10 && !applicationInfo.requestLegacyExternalStorage) {
            // please manifest file Application node declare android:requestLegacyExternalStorage="true" attribute
            // otherwise request permission, Android 10 device storage
            // If projectalready storage, please declare in the manifest file meta-data attribute
            // <meta-data android:name="ScopedStorage" android:value="true" /> check
            throw new IllegalStateException("Please register the android:requestLegacyExternalStorage=\"true\" " +
                "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version");
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // checkwhether has legacy storagepermission, has directly , please runtime request permission
        // frameworkwill Android 10 below version add request permission
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.WRITE_EXTERNAL_STORAGE)) {
            throw new IllegalArgumentException("If you have applied for \"" + getPermissionName() + "\" permissions, " +
                                                "do not apply for the \"" + PermissionNames.READ_EXTERNAL_STORAGE +
                                                "\" or \"" + PermissionNames.WRITE_EXTERNAL_STORAGE + "\" permissions");
        }

        // because MANAGE_EXTERNAL_STORAGE permissionscope , has readmedia , need to request media permissions
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_AUDIO)) {
            throw new IllegalArgumentException("Because the \"" + getPermissionName() + "\" permission range is very large, "
                + "you can read media files with it, and there is no need to apply for additional media permissions.");
        }
    }
}