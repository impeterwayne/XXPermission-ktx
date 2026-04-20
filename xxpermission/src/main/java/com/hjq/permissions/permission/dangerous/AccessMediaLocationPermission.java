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
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Access media location permission class.
 */
public final class AccessMediaLocationPermission extends DangerousPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
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
     * Check whether the media read permission is granted
     */
    private boolean isGrantedReadMediaPermission(@NonNull Context context, boolean skipRequest) {
        if (PermissionVersion.isAndroid13() && PermissionVersion.getTargetSdkVersion(context) >= PermissionVersion.ANDROID_13) {
            // Why is there no additional Android 14 and READ_MEDIA_VISUAL_USER_SELECTED permission check？This is because if the user only grants partial photo and video access
            // request Permission.ACCESS_MEDIA_LOCATION the system returns failure, the user must grant access to all photos and videos before this permission can be requested
            return PermissionLists.getReadMediaImagesPermission().isGrantedPermission(context, skipRequest) ||
                PermissionLists.getReadMediaVideoPermission().isGrantedPermission(context, skipRequest) ||
                PermissionLists.getManageExternalStoragePermission().isGrantedPermission(context, skipRequest);
        }
        if (PermissionVersion.isAndroid11() && PermissionVersion.getTargetSdkVersion(context) >= PermissionVersion.ANDROID_11) {
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
            // Please place the ACCESS_MEDIA_LOCATION permission after the READ_MEDIA_IMAGES permission.
            throw new IllegalArgumentException("Please place the " + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_MEDIA_IMAGES + "\" permission");
        }

        if (readMediaVideoPermissionIndex != -1 && readMediaVideoPermissionIndex > thisPermissionIndex) {
            // Please place the ACCESS_MEDIA_LOCATION permission after the READ_MEDIA_VIDEO permission.
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_MEDIA_VIDEO + "\" permission");
        }

        if (readMediaVisualUserSelectedPermissionIndex != -1 && readMediaVisualUserSelectedPermissionIndex > thisPermissionIndex) {
            // Please place the ACCESS_MEDIA_LOCATION permission after the READ_MEDIA_VISUAL_USER_SELECTED permission.
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_MEDIA_VISUAL_USER_SELECTED + "\" permission");
        }

        if (manageExternalStoragePermissionIndex != -1 && manageExternalStoragePermissionIndex > thisPermissionIndex) {
            // Please place the ACCESS_MEDIA_LOCATION permission after the MANAGE_EXTERNAL_STORAGE permission.
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" permission");
        }

        if (readExternalStoragePermissionIndex != -1 && readExternalStoragePermissionIndex > thisPermissionIndex) {
            // Please place the ACCESS_MEDIA_LOCATION permission after the READ_EXTERNAL_STORAGE permission.
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" permission");
        }

        if (writeExternalStoragePermissionIndex != -1 && writeExternalStoragePermissionIndex > thisPermissionIndex) {
            // Please place the ACCESS_MEDIA_LOCATION permission after the WRITE_EXTERNAL_STORAGE permission.
            throw new IllegalArgumentException("Please place the \"" + getPermissionName() +
                "\" permission after the \"" + PermissionNames.WRITE_EXTERNAL_STORAGE + "\" permission");
        }

        // Check whether the current project targets Android 13
        if (PermissionVersion.getTargetSdkVersion(activity) >= PermissionVersion.ANDROID_13) {
            // Check whether the requested permissions contain certain specific permissions
            if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_IMAGES) ||
                PermissionUtils.containsPermission(requestList, PermissionNames.READ_MEDIA_VIDEO) ||
                PermissionUtils.containsPermission(requestList, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
                // IfRequested permissions , contain those permissions ,
                return;
            }

            // If includes, the caller must manually add READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, MANAGE_EXTERNAL_STORAGE permission request ACCESS_MEDIA_LOCATION permission
            throw new IllegalArgumentException("You must add \"" + PermissionNames.READ_MEDIA_IMAGES + "\" or \"" +
                PermissionNames.READ_MEDIA_VIDEO + "\" or \"" + PermissionNames.MANAGE_EXTERNAL_STORAGE +
                "\" rights to apply for \"" + getPermissionName() + "\" rights");
        }

        // If the current projectalso no Android 13, Check whether the requested permissions contain certain specific permissions
        if (PermissionUtils.containsPermission(requestList, PermissionNames.READ_EXTERNAL_STORAGE) ||
            PermissionUtils.containsPermission(requestList, PermissionNames.MANAGE_EXTERNAL_STORAGE)) {
            // IfRequested permissions , contain those permissions ,
            return;
        }

        // If includes, the caller must manually add READ_EXTERNAL_STORAGE or MANAGE_EXTERNAL_STORAGE request ACCESS_MEDIA_LOCATION permission
        throw new IllegalArgumentException("You must add \"" + PermissionNames.READ_EXTERNAL_STORAGE + "\" or \"" +
            PermissionNames.MANAGE_EXTERNAL_STORAGE + "\" rights to apply for \"" + getPermissionName() + "\" rights");
    }
}