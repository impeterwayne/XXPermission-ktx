package com.hjq.permissions.permission.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Permission interface
 */
public interface IPermission extends Parcelable {

    /**
     * Get the permission name
     */
    @NonNull
    String getPermissionName();

    /**
     * Get the permission name when requesting (defaults to the permission name)
     */
    default String getRequestPermissionName(Context context) {
        return getPermissionName();
    }

    /**
     * Get the permission request channel
     */
    @NonNull
    PermissionChannel getPermissionChannel(@NonNull Context context);

    /**
     * Get the type of permission page
     */
    @NonNull
    PermissionPageType getPermissionPageType(@NonNull Context context);

    /**
     * Get the permission group
     */
    @Nullable
    default String getPermissionGroup(@NonNull Context context) {
        // Returning null means no group
        return null;
    }

    /**
     * Get the Android version where this permission was introduced
     */
    int getFromAndroidVersion(@NonNull Context context);

    /**
     * Get the minimum targetSdk version required for using this permission
     */
    default int getMinTargetSdkVersion(@NonNull Context context) {
        return getFromAndroidVersion(context);
    }

    /**
     * Get the old permissions corresponding to the current permission
     */
    @Nullable
    default List<IPermission> getOldPermissions(Context context) {
        // Means there are no old permissions
        return null;
    }

    /**
     * Get the foreground permissions corresponding to the current permission
     */
    @Nullable
    default List<IPermission> getForegroundPermissions(@NonNull Context context) {
        // Means there are no foreground permissions
        return null;
    }

    /**
     * Whether the current permission is a background permission
     */
    default boolean isBackgroundPermission(@NonNull Context context) {
        List<IPermission> foregroundPermission = getForegroundPermissions(context);
        if (foregroundPermission == null) {
            return false;
        }
        return !foregroundPermission.isEmpty();
    }

    /**
     * Whether the current permission supports being requested
     */
    default boolean isSupportRequestPermission(@NonNull Context context) {
        // If the permission runs on a lower version (unsupported version),
        // then requesting this permission is not supported.
        // Example: MANAGE_EXTERNAL_STORAGE was introduced in Android 11,
        // so it cannot be requested on Android 10.
        return getFromAndroidVersion(context) <= PermissionVersion.getCurrentVersion();
    }

    /**
     * Check whether the current permission is granted
     */
    default boolean isGrantedPermission(@NonNull Context context) {
        return isGrantedPermission(context, true);
    }

    /**
     * Check whether the current permission is granted
     *
     * @param skipRequest  Whether to skip requesting and directly check the status
     */
    boolean isGrantedPermission(@NonNull Context context, boolean skipRequest);

    /**
     * Check whether the user has selected "Do not ask again" for the current permission
     */
    boolean isDoNotAskAgainPermission(@NonNull Activity activity);

    /**
     * Get all available setting page Intents for the current permission
     */
    @NonNull
    default List<Intent> getPermissionSettingIntents(@NonNull Context context) {
        return getPermissionSettingIntents(context, true);
    }

    /**
     * Get all available setting page Intents for the current permission
     *
     * Note:
     * - Do not check whether the intent exists before adding it here.
     *   The framework will filter non-existent intents before jumping.
     * - Even if you pre-check, an existing intent may still fail at runtime.
     * - If jumping fails, the framework will automatically try the next intent.
     *
     * In short:
     * - Non-existent intents will definitely fail to jump.
     * - Existing intents are not guaranteed to succeed 100%.
     *
     * @param skipRequest  Whether to skip requesting and directly return Intents
     */
    @NonNull
    List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest);

    /**
     * Get the interval time for permission requests
     */
    default int getRequestIntervalTime(@NonNull Context context) {
        return 0;
    }

    /**
     * Get the wait time for handling the permission result
     */
    default int getResultWaitTime(@NonNull Context context) {
        return 0;
    }

    /**
     * Check whether the permission request complies with requirements
     */
    default void checkCompliance(@NonNull Activity activity,
                                 @NonNull List<IPermission> requestList,
                                 @Nullable AndroidManifestInfo manifestInfo) {
        // default implementation ignored
    }
}
