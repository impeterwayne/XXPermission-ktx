package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
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
 *    desc   : Permission class for reading audio media
 */
public final class ReadMediaAudioPermission extends DangerousPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.READ_MEDIA_AUDIO;

    public static final Parcelable.Creator<ReadMediaAudioPermission> CREATOR = new Parcelable.Creator<ReadMediaAudioPermission>() {

        @Override
        public ReadMediaAudioPermission createFromParcel(Parcel source) {
            return new ReadMediaAudioPermission(source);
        }

        @Override
        public ReadMediaAudioPermission[] newArray(int size) {
            return new ReadMediaAudioPermission[size];
        }
    };

    public ReadMediaAudioPermission() {
        // default implementation ignored
    }

    private ReadMediaAudioPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_13;
    }

    @NonNull
    @Override
    public List<IPermission> getOldPermissions(Context context) {
        // On Android versions below 13, accessing media files required READ_EXTERNAL_STORAGE permission
        return PermissionUtils.asArrayList(PermissionLists.getReadExternalStoragePermission());
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getReadExternalStoragePermission().isDoNotAskAgainPermission(activity);
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        // If the permission’s introduced version is higher than minSdkVersion,
        // it means this permission might still be requested on older systems.
        // In that case, you must register the old permission in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.READ_EXTERNAL_STORAGE, PermissionVersion.ANDROID_12_L);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // Check if READ_EXTERNAL_STORAGE permission has been manually added.
        // If yes, throw an exception. Do not add this permission yourself,
        // the framework automatically adds and requests it on versions below Android 13.
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE)) {
            throw new IllegalArgumentException("You have added the \"" + getPermissionName() + "\" permission, "
                    + "please do not add the \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" permission, "
                    + "this conflicts with the framework's automatic compatibility policy.");
        }
    }
}
