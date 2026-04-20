package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Partial photo and video access permission class.
 */
public final class ReadMediaVisualUserSelectedPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED;

    public static final Parcelable.Creator<ReadMediaVisualUserSelectedPermission> CREATOR = new Parcelable.Creator<ReadMediaVisualUserSelectedPermission>() {

        @Override
        public ReadMediaVisualUserSelectedPermission createFromParcel(Parcel source) {
            return new ReadMediaVisualUserSelectedPermission(source);
        }

        @Override
        public ReadMediaVisualUserSelectedPermission[] newArray(int size) {
            return new ReadMediaVisualUserSelectedPermission[size];
        }
    };

    public ReadMediaVisualUserSelectedPermission() {
        // default implementation ignored
    }

    private ReadMediaVisualUserSelectedPermission(Parcel in) {
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
        return PermissionVersion.ANDROID_14;
    }

    @Override
    public int getMinTargetSdkVersion(@NonNull Context context) {
        // granted photos and videos permission: https://developer.android.google.cn/about/versions/14/changes/partial-photo-video-access?hl=zh-cn
        // READ_MEDIA_VISUAL_USER_SELECTED permission , need to targetSdk version request, need to and READ_MEDIA_IMAGES and READ_MEDIA_VIDEO
        // permissioncannot request, and READ_MEDIA_IMAGES, READ_MEDIA_VIDEO request, otherwisewill has issue, so permission targetSdk 33 above
        return PermissionVersion.ANDROID_13;
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO)) {
            return;
        }
        // cannot request READ_MEDIA_VISUAL_USER_SELECTED permission, need to READ_MEDIA_IMAGES or READ_MEDIA_VIDEO permission, or has , otherwisepermission requestwill systemdirectlydenied
        throw new IllegalArgumentException("You cannot request the \"" + getPermissionName() + "\" permission alone. " +
                                            "must add either \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                                            PermissionNames.READ_MEDIA_VIDEO + "\" permission, or maybe both");
    }
}