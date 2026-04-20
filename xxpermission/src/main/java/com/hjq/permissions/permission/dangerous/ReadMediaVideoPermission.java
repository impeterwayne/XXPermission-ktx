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
 * Read video media permission class.
 */
public final class ReadMediaVideoPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
        // Android 13 below media need to read storage permission
        return PermissionUtils.asArrayList(PermissionLists.getReadExternalStoragePermission());
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid14() && !skipRequest) {
            // If Android 14 , permission or videospermission, need toagaincheckpermission state
            // becauseuserauthorization or videos, READ_MEDIA_VISUAL_USER_SELECTED permission state granted
            // READ_MEDIA_IMAGES and READ_MEDIA_VIDEO permission state denied
            // permission callback failure, here return true, this means callerrequest success
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
        // If the version where this permission was introduced is lower than minSdkVersion, it may be requested on older systems, so the legacy permission must also be declared in AndroidManifest.xml.
        if (getFromAndroidVersion(activity) > getMinSdkVersion(activity, manifestInfo)) {
            checkPermissionRegistrationStatus(permissionInfoList, PermissionNames.READ_EXTERNAL_STORAGE, PermissionVersion.ANDROID_12_L);
        }
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);
        // checkwhether has addread external storage permission, has directly , please add permission, frameworkwill Android 13 below version add request permission
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE)) {
            throw new IllegalArgumentException("You have added the \"" + getPermissionName() + "\" permission, "
                + "please do not add the \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" permission, "
                + "this conflicts with the framework's automatic compatibility policy.");
        }
    }
}