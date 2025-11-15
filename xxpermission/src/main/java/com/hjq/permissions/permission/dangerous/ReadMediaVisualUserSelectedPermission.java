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
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/13
 *    desc   : Permission class for accessing a user-selected subset of photos and videos
 */
public final class ReadMediaVisualUserSelectedPermission extends DangerousPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through {@link PermissionNames}.
     */
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
        // Partial photo and video access docs:
        // https://developer.android.google.cn/about/versions/14/changes/partial-photo-video-access?hl=en
        //
        // The READ_MEDIA_VISUAL_USER_SELECTED permission is special:
        // - It does not require raising targetSdk to request it,
        // - But it must be combined with READ_MEDIA_IMAGES and/or READ_MEDIA_VIDEO.
        //
        // This permission cannot be requested alone, otherwise the system will deny it.
        // Therefore, the minimum targetSdk requirement is 33 (Android 13) or higher.
        return PermissionVersion.ANDROID_13;
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
                PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO)) {
            return;
        }
        // READ_MEDIA_VISUAL_USER_SELECTED cannot be requested alone.
        // It must be combined with either READ_MEDIA_IMAGES or READ_MEDIA_VIDEO, or both,
        // otherwise the system will reject the request immediately.
        throw new IllegalArgumentException("You cannot request the \"" + getPermissionName() + "\" permission alone. " +
                "You must add either \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                PermissionNames.READ_MEDIA_VIDEO + "\" permission, or both.");
    }
}
