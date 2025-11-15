package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.DangerousPermission;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionUtils;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Access media location permission class
 */
public final class AccessMediaLocationPermission extends DangerousPermission {

    /**
     * Current permission name.
     * Note: This constant field is for internal framework use only, not for external reference.
     * If you need the permission name string, please use the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.ACCESS_MEDIA_LOCATION;

    public static final Parcelable.Creator<AccessMediaLocationPermission> CREATOR = new Parcelable.Creator<AccessMediaLocationPermission>() {

        @Override
        public AccessMediaLocationPermission createFromParcel(Parcel source) {
            return new AccessMediaLocationPermission(source);
        }

        @Override
        public AccessMediaLocationPermission[] newArray(int size) {
            return new AccessMediaLocationPermission[size];
        }
    };

    public AccessMediaLocationPermission() {
        // default implementation ignored
    }

    private AccessMediaLocationPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_10;
    }

    @Override
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        return isGrantedReadMediaPermission(context, skipRequest) &&
                super.isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        return isGrantedReadMediaPermission(activity, true) &&
                super.isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getReadExternalStoragePermission().isDoNotAskAgainPermission(activity);
    }

    /**
     * Check whether read media permission is granted
     */
    private boolean isGrantedReadMediaPermission(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_13) {
            // Why not include Android 14 and READ_MEDIA_VISUAL_USER_SELECTED here?
            // Because if you select partial photos/videos and then request ACCESS_MEDIA_LOCATION,
            // the system will fail the request. You must choose "all photos and videos" to request this permission.
            return PermissionLists.getReadMediaImagesPermission().isGrantedPermission(context, skipRequest) ||
                    PermissionLists.getReadMediaVideoPermission().isGrantedPermission(context, skipRequest) ||
                    PermissionLists.getManageExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetVersion(context) >= PermissionVersion.ANDROID_11) {
            return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest) ||
                    PermissionLists.getManageExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        return PermissionLists.getReadExternalStoragePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        super.checkSelfByRequestPermissions(activity, requestList);

        int thisPermissionIndex = -1;
        int readMediaImagesPermissionIndex = -1;
        int readMediaVideoPermissionIndex = -1;
        int readMediaVisualUserSelectedPermissionIndex = -1;
        int manageExternalStoragePermissionIndex = -1;
        int readExternalStoragePermissionIndex = -1;
        int writeExternalStoragePermissionIndex = -1;
        for (int i = 0; i < requestList.size(); i++) {
            IPermission permission = requestList.get(i);
            if (PermissionUtils.equalsPermission(permission, this)) {
                thisPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_MEDIA_IMAGES)) {
                readMediaImagesPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_MEDIA_VIDEO)) {
                readMediaVideoPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED)) {
                readMediaVisualUserSelectedPermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
                manageExternalStoragePermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.READ_EXTERNAL_STORAGE)) {
                readExternalStoragePermissionIndex = i;
            } else if (PermissionUtils.equalsPermission(permission, PermissionNames.WRITE_EXTERNAL_STORAGE)) {
                writeExternalStoragePermissionIndex = i;
            }
        }

        if (readMediaImagesPermissionIndex != -1 && readMediaImagesPermissionIndex > thisPermissionIndex) {
            // Place ACCESS_MEDIA_LOCATION after READ_MEDIA_IMAGES
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.READ_MEDIA_IMAGES + "\" permission");
        }

        if (readMediaVideoPermissionIndex != -1 && readMediaVideoPermissionIndex > thisPermissionIndex) {
            // Place ACCESS_MEDIA_LOCATION after READ_MEDIA_VIDEO
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.READ_MEDIA_VIDEO + "\" permission");
        }

        if (readMediaVisualUserSelectedPermissionIndex != -1 && readMediaVisualUserSelectedPermissionIndex > thisPermissionIndex) {
            // Place ACCESS_MEDIA_LOCATION after READ_MEDIA_VISUAL_USER_SELECTED
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED + "\" permission");
        }

        if (manageExternalStoragePermissionIndex != -1 && manageExternalStoragePermissionIndex > thisPermissionIndex) {
            // Place ACCESS_MEDIA_LOCATION after MANAGE_EXTERNAL_STORAGE
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" permission");
        }

        if (readExternalStoragePermissionIndex != -1 && readExternalStoragePermissionIndex > thisPermissionIndex) {
            // Place ACCESS_MEDIA_LOCATION after READ_EXTERNAL_STORAGE
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" permission");
        }

        if (writeExternalStoragePermissionIndex != -1 && writeExternalStoragePermissionIndex > thisPermissionIndex) {
            // Place ACCESS_MEDIA_LOCATION after WRITE_EXTERNAL_STORAGE
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                    "\" permission after the \"" + PermissionNames.WRITE_EXTERNAL_STORAGE + "\" permission");
        }

        // Check whether the project targets Android 13
        if (PermissionVersion.getTargetVersion(activity) >= PermissionVersion.ANDROID_13) {
            // If request list already contains one of the required permissions, skip further checks
            if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
                    PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO) ||
                    PermissionUtils.containsPermission(requestList, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
                return;
            }

            // Otherwise, you must manually add READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, or MANAGE_EXTERNAL_STORAGE
            // to be able to request ACCESS_MEDIA_LOCATION
            throw new IllegalArgumentException("You must add \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                    PermissionNames.READ_MEDIA_VIDEO + "\" or \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE +
                    "\" rights to apply for \"" + getPermissionName() + "\" rights");
        }

        // If project has not yet targeted Android 13, check for these permissions instead
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE) ||
                PermissionUtils.containsPermission(requestList, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
            return;
        }

        // Otherwise, you must add READ_EXTERNAL_STORAGE or MANAGE_EXTERNAL_STORAGE
        // to be able to request ACCESS_MEDIA_LOCATION
        throw new IllegalArgumentException("You must add \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" or \"" +
                PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" rights to apply for \"" + getPermissionName() + "\" rights");
    }
}
