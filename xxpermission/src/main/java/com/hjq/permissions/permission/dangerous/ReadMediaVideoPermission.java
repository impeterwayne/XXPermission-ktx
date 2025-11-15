package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
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
 *    desc   : Permission class for reading video media
 */
public final class ReadMediaVideoPermission extends DangerousPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
    public static final String PERMISSION_NAME = PermissionNames.READ_MEDIA_VIDEO;

    public static final Parcelable.Creator<ReadMediaVideoPermission> CREATOR = new Parcelable.Creator<ReadMediaVideoPermission>() {

        @Override
        public ReadMediaVideoPermission createFromParcel(Parcel source) {
            return new ReadMediaVideoPermission(source);
        }

        @Override
        public ReadMediaVideoPermission[] newArray(int size) {
            return new ReadMediaVideoPermission[size];
        }
    };

    public ReadMediaVideoPermission() {
        // default implementation ignored
    }

    private ReadMediaVideoPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public String getPermissionGroup(@NonNull Context context) {
        return PermissionGroups.IMAGE_AND_VIDEO_MEDIA;
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
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid14() && !skipRequest) {
            // On Android 14, if the request is for image or video permissions, the status needs to be re-checked.
            // Reason: When the user grants access to "selected photos/videos" only,
            // the READ_MEDIA_VISUAL_USER_SELECTED permission is granted,
            // but READ_MEDIA_IMAGES and READ_MEDIA_VIDEO are shown as denied.
            // To avoid failing the permission callback, we return true here,
            // signaling that the request should be considered successful.
            return PermissionLists.getReadMediaVisualUserSelectedPermission().isGrantedPermission(context, false);
        }
        return super.isGrantedPermissionByStandardVersion(context, skipRequest);
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
        // it means this permission may still be requested on older systems.
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
