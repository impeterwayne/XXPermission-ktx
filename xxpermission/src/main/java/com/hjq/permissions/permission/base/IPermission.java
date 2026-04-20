package com.hjq.permissions.permission.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 * Common contract for all permission definitions.
 */
public interface IPermission extends Parcelable {

    /** Returns the permission name. */
    @NonNull
    String getPermissionName();

    /** Returns the request channel used for this permission. */
    @NonNull
    PermissionChannel getPermissionChannel(@NonNull Context context);

    /** Returns the settings page type used for this permission. */
    @NonNull
    PermissionPageType getPermissionPageType(@NonNull Context context);

    /** Returns the permission group, or {@code null} if it has none. */
    @Nullable
    default String getPermissionGroup(@NonNull Context context) {
        // Returning null means there is no group
        return null;
    }

    /** Returns the Android version where this permission was introduced. */
    int getFromAndroidVersion(@NonNull Context context);

    /** Returns the minimum target SDK required to use this permission. */
    default int getMinTargetSdkVersion(@NonNull Context context) {
        return getFromAndroidVersion(context);
    }

    /** Returns legacy permissions that correspond to this permission. */
    @Nullable
    default List<IPermission> getOldPermissions(Context context) {
        // Indicates there are no legacy permissions
        return null;
    }

    /** Returns the foreground permissions that correspond to this permission. */
    @Nullable
    default List<IPermission> getForegroundPermissions(@NonNull Context context) {
        // Indicates there are no foreground permissions
        return null;
    }

    /** Returns whether this is a background permission. */
    default boolean isBackgroundPermission(@NonNull Context context) {
        List<IPermission> foregroundPermission = getForegroundPermissions(context);
        if (foregroundPermission == null) {
            return false;
        }
        return !foregroundPermission.isEmpty();
    }

    /** Returns whether this permission can be requested on the current device. */
    default boolean isSupportRequestPermission(@NonNull Context context) {
        // If the current permission is running on an older unsupported version, it does not support requesting.
        // For example, MANAGE_EXTERNAL_STORAGE was introduced in Android 11, so it cannot be requested on Android 10.
        return getFromAndroidVersion(context) <= PermissionVersion.getSdkVersion();
    }

    /** Returns whether this permission is granted. */
    default boolean isGrantedPermission(@NonNull Context context) {
        return isGrantedPermission(context, true);
    }

    /**
     * Returns whether this permission is granted.
     *
     * @param skipRequest whether to skip any request side effects and only inspect state
     */
    boolean isGrantedPermission(@NonNull Context context, boolean skipRequest);

    /** Returns whether the user selected Do not ask again for this permission. */
    boolean isDoNotAskAgainPermission(@NonNull Activity activity);

    /** Returns the available settings page intents for this permission. */
    @NonNull
    default List<Intent> getPermissionSettingIntents(@NonNull Context context) {
        return getPermissionSettingIntents(context, true);
    }

    /**
     * Returns the available settings page intents for this permission.
     *
     * <p>Implementations do not need to pre-filter unavailable intents. The framework
     * filters and retries them during navigation.</p>
     *
     * @param skipRequest whether to skip request side effects and return intents directly
     */
    @NonNull
    List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest);

    /** Returns the required interval between permission requests. */
    default int getRequestIntervalTime(@NonNull Context context) {
        return 0;
    }

    /** Returns the wait time before the permission result is handled. */
    default int getResultWaitTime(@NonNull Context context) {
        return 0;
    }

    /**
     * Check whether the permission request is compliant
     */
    default void checkCompliance(@NonNull Activity activity,
                                 @NonNull List<IPermission> requestList,
                                 @Nullable AndroidManifestInfo manifestInfo) {
        // default implementation ignored
    }
}
