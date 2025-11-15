package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
 *    desc   : Post notifications permission class
 */
public final class PostNotificationsPermission extends DangerousPermission {

    /** Current permission name.
     *  Note: This constant field is for internal framework use only,
     *  not provided for external references.
     *  If you need to get the permission name string,
     *  please obtain it directly through the {@link PermissionNames} class.
     */
    public static final String PERMISSION_NAME = PermissionNames.POST_NOTIFICATIONS;

    public static final Parcelable.Creator<PostNotificationsPermission> CREATOR = new Parcelable.Creator<PostNotificationsPermission>() {

        @Override
        public PostNotificationsPermission createFromParcel(Parcel source) {
            return new PostNotificationsPermission(source);
        }

        @Override
        public PostNotificationsPermission[] newArray(int size) {
            return new PostNotificationsPermission[size];
        }
    };

    public PostNotificationsPermission() {
        // default implementation ignored
    }

    private PostNotificationsPermission(Parcel in) {
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
        // On Android versions below 13, enabling the notification bar service
        // requires the old notification bar permission (virtually created by the framework).
        return PermissionUtils.asArrayList(PermissionLists.getNotificationServicePermission());
    }

    @Override
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionLists.getNotificationServicePermission().isGrantedPermission(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return PermissionLists.getNotificationServicePermission().isDoNotAskAgainPermission(activity);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        // GitHub issue address: https://github.com/getActivity/XXPermissions/issues/208
        // POST_NOTIFICATIONS should navigate to the same permission settings page
        // as the NOTIFICATION_SERVICE permission.
        return PermissionLists.getNotificationServicePermission().getPermissionSettingIntents(context, skipRequest);
    }
}
