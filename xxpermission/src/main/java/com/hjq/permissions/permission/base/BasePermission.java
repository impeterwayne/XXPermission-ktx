package com.hjq.permissions.permission.base;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Base permission class.
 */
public abstract class BasePermission implements IPermission {

    /** Op permission mode, unknown */
    public static final int MODE_UNKNOWN = -1;

    protected BasePermission() {
        // default implementation ignored
    }

    protected BasePermission(Parcel in) {
        // default implementation ignored
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // default implementation ignored
    }

    @NonNull
    @Override
    public String toString() {
        return getPermissionName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // If the compared object is the same instance as the current object, return true.
        if (obj == this) {
            return true;
        }
        // equals is overridden so List and Map collections can determine whether different permission objects represent the same permission.
        // If the two permission objects have the same name, treat them as the same permission.
        if (obj instanceof IPermission) {
            return PermissionUtils.equalsPermission(this, ((IPermission) obj));
        } else if (obj instanceof String) {
            return PermissionUtils.equalsPermission(this, ((String) obj));
        }
        return false;
    }

    @NonNull
    protected Uri getPackageNameUri(@NonNull Context context) {
        return PermissionUtils.getPackageNameUri(context);
    }

    @NonNull
    protected Intent getApplicationDetailsSettingIntent(@NonNull Context context) {
        return PermissionSettingPage.getApplicationDetailsSettingsIntent(context, this);
    }

    @NonNull
    protected static Intent getManageApplicationSettingIntent() {
        return PermissionSettingPage.getManageApplicationSettingsIntent();
    }

    @NonNull
    protected static Intent getApplicationSettingIntent() {
        return PermissionSettingPage.getApplicationSettingsIntent();
    }

    @NonNull
    protected Intent getAndroidSettingIntent() {
        return PermissionSettingPage.getAndroidSettingsIntent();
    }

    @Override
    public void checkCompliance(@NonNull Activity activity,
                                @NonNull List<IPermission> requestList,
                                @Nullable AndroidManifestInfo manifestInfo) {
        // Check whether targetSdkVersion meets the requirement.
        checkSelfByTargetSdkVersion(activity);
        // Check whether AndroidManifest.xml meets the requirement.
        if (manifestInfo != null) {
            List<PermissionManifestInfo> permissionInfoList = manifestInfo.permissionInfoList;
            PermissionManifestInfo currentPermissionInfo = findPermissionInfoByList(permissionInfoList, getPermissionName());
            checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        }
        // Check whether the requested permission list meets the requirement.
        checkSelfByRequestPermissions(activity, requestList);
    }

    /**
     * Check whether targetSdkVersion meets the requirement., and throw an exception if it does not
     */
    protected void checkSelfByTargetSdkVersion(@NonNull Context context) {
        int minTargetSdkVersion = getMinTargetSdkVersion(context);
        // A correct targetSdkVersion is required for permission checks to work properly.
        if (PermissionVersion.getTargetSdkVersion(context) >= minTargetSdkVersion) {
            return;
        }

        throw new IllegalStateException("Request \"" + getPermissionName() + "\" permission, " +
            "The targetSdkVersion SDK must be " + minTargetSdkVersion +
            " or more, if you do not want to upgrade targetSdkVersion, " +
            "please apply with the old permission");
    }

    /**
     * Whether the current permission is declared statically in the manifest.
     */
    protected abstract boolean isRegisterPermissionByManifestFile();

    /**
     * Check whether AndroidManifest.xml meets the requirement., and throw an exception if it does not
     */
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        if (!isRegisterPermissionByManifestFile()) {
            return;
        }
        // Check whether the current permission is statically declared in the manifest file, If it is declared, also check whether the declared maxSdkVersion attribute is valid
        checkPermissionRegistrationStatus(currentPermissionInfo, getPermissionName());
    }

    /**
     * Check whether the requested permission list meets the requirement., and throw an exception if it does not
     */
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // default implementation ignored
        // There is no default implementation here; subclasses handle it themselves.
    }

    /**
     * Check the permission registration state and throw an exception when it is invalid.
     */
    protected static void checkPermissionRegistrationStatus(@Nullable PermissionManifestInfo permissionInfo,
                                                            @NonNull String checkPermission) {
        checkPermissionRegistrationStatus(permissionInfo, checkPermission, PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION);
    }

    protected static void checkPermissionRegistrationStatus(@Nullable List<PermissionManifestInfo> permissionInfoList,
                                                            @NonNull String checkPermission) {
        checkPermissionRegistrationStatus(permissionInfoList, checkPermission, PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION);
    }

    protected static void checkPermissionRegistrationStatus(@Nullable List<PermissionManifestInfo> permissionInfoList,
                                                            @NonNull String checkPermission,
                                                            int lowestMaxSdkVersion) {
        PermissionManifestInfo permissionInfo = null;
        if (permissionInfoList != null) {
            permissionInfo = findPermissionInfoByList(permissionInfoList, checkPermission);
        }
        checkPermissionRegistrationStatus(permissionInfo, checkPermission, lowestMaxSdkVersion);
    }

    protected static void checkPermissionRegistrationStatus(@Nullable PermissionManifestInfo permissionInfo,
                                                            @NonNull String checkPermission,
                                                            int lowestMaxSdkVersion) {
        if (permissionInfo == null) {
            // A dynamically requested permission is not declared in the manifest file, There are two common cases:
            // 1. If your project does not declare this permission in the manifest file, simply declare it in the manifest file
            // 2. If your project clearly already declares this permission, check whether the built APK actually contains that permission, If it does not, then the framework check is correct
            // This is usually caused by a third-party SDK or framework declaring <uses-permission android:name="xxx" tools:node="remove"/>
            // The fix is simple, declare <uses-permission android:name="xxx" tools:node="replace"/> to replace the original configuration
            // Concrete example: https://github.com/getActivity/XXPermissions/issues/98
            throw new IllegalStateException("Please register permissions in the AndroidManifest.xml file " +
                "<uses-permission android:name=\"" + checkPermission + "\" />");
        }

        int manifestMaxSdkVersion = permissionInfo.maxSdkVersion;
        if (manifestMaxSdkVersion < lowestMaxSdkVersion) {
            // The maxSdkVersion of the permission declared in the manifest file does not meet the minimum requirement, There are two common cases:
            // 1. If your project declared that attribute, modify maxSdkVersion or remove the maxSdkVersion attribute
            // 2. If you clearly never declared maxSdkVersion attribute, check whether the built APK contains that attribute, If it exists there, then the framework check is correct
            // This is usually caused by a third-party SDK or framework declaring <uses-permission android:name="xxx" android:maxSdkVersion="xx"/>
            // The fix is simple, declare <uses-permission android:name="xxx" tools:node="replace"/> to replace the original configuration
            throw new IllegalArgumentException("The AndroidManifest.xml file " +
                "<uses-permission android:name=\"" + checkPermission +
                "\" android:maxSdkVersion=\"" + manifestMaxSdkVersion +
                "\" /> does not meet the requirements, " +
                (lowestMaxSdkVersion != PermissionManifestInfo.DEFAULT_MAX_SDK_VERSION ?
                    "the minimum requirement for maxSdkVersion is " + lowestMaxSdkVersion :
                    "please delete the android:maxSdkVersion=\"" + manifestMaxSdkVersion + "\" attribute"));
        }
    }

    /**
     * Get the current project minSdkVersion.
     */
    protected static int getMinSdkVersion(@NonNull Context context, @Nullable AndroidManifestInfo manifestInfo) {
        if (PermissionVersion.isAndroid7()) {
            return context.getApplicationInfo().minSdkVersion;
        }

        if (manifestInfo == null || manifestInfo.usesSdkInfo == null) {
            return PermissionVersion.ANDROID_4_2;
        }
        return manifestInfo.usesSdkInfo.minSdkVersion;
    }

    /**
     * Get the specified permission info from the permission list.
     */
    @Nullable
    public static PermissionManifestInfo findPermissionInfoByList(@NonNull List<PermissionManifestInfo> permissionInfoList,
                                                                  @NonNull String permissionName) {
        PermissionManifestInfo permissionInfo = null;
        for (PermissionManifestInfo info : permissionInfoList) {
            if (PermissionUtils.equalsPermission(info.name, permissionName)) {
                permissionInfo = info;
                break;
            }
        }
        return permissionInfo;
    }

    /**
     * Check whether a dangerous permission has been granted.
     */
    @RequiresApi(PermissionVersion.ANDROID_6)
    public static boolean checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check whether a permission rationale should be shown to the user.
     */
    @RequiresApi(PermissionVersion.ANDROID_6)
    @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions", "BooleanMethodIsAlwaysInverted"})
    public static boolean shouldShowRequestPermissionRationale(@NonNull Activity activity, @NonNull String permission) {
        // Work around the memory leak that occurs on Android 12 when calling shouldShowRequestPermissionRationale
        // Android 12L and Android 13 versions tested do not have this issue, which shows that Google fixed this issue in newer versions
        // However, on Android 12 it is still a legacy issue, this is something all Android app developers still have to deal with
        // issue : https://github.com/getActivity/XXPermissions/issues/133
        if (PermissionVersion.getSdkVersion() == PermissionVersion.ANDROID_12) {
            try {
                // In addition, for this issue, I also contributed a fix to the AndroidX project, and the merge request has already been merged into the main branch
                // I believe this contribution helps address the memory leak on a very large number of Android 12 devices
                // Pull Request : https://github.com/androidx/androidx/pull/435
                PackageManager packageManager = activity.getApplication().getPackageManager();
                Method method = PackageManager.class.getMethod("shouldShowRequestPermissionRationale", String.class);
                return (boolean) method.invoke(packageManager, permission);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * Checks whether a permission is granted through AppOpsManager.
     *
     * @param opName the AppOpsManager field whose name starts with OPSTR
     * @param defaultGranted whether to treat an unknown state as granted
     */
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    public static boolean checkOpPermission(@NonNull Context context, @NonNull String opName, boolean defaultGranted) {
        int opMode = getOpPermissionMode(context, opName);
        if (opMode == MODE_UNKNOWN) {
            return defaultGranted;
        }
        return opMode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * Checks whether a permission is granted through AppOpsManager.
     *
     * @param opFieldName the AppOpsManager field name to resolve through reflection
     * @param opDefaultValue the fallback value to use when reflection fails
     * @param defaultGranted whether to treat an unknown state as granted
     */
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    public static boolean checkOpPermission(@NonNull Context context,
                                            @NonNull String opFieldName,
                                            int opDefaultValue,
                                            boolean defaultGranted) {
        int opMode = getOpPermissionMode(context, opFieldName, opDefaultValue);
        if (opMode == MODE_UNKNOWN) {
            return defaultGranted;
        }
        return opMode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * Returns the AppOpsManager state for a permission.
     *
     * @param opName the AppOpsManager field whose name starts with OPSTR
     */
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    @SuppressWarnings("deprecation")
    public static int getOpPermissionMode(@NonNull Context context, @NonNull String opName) {
        AppOpsManager appOpsManager;
        if (PermissionVersion.isAndroid6()) {
            appOpsManager = context.getSystemService(AppOpsManager.class);
        } else {
            appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        }
        // Although this SystemService should never be null, keep the check for defensive programming.
        if (appOpsManager == null) {
            return MODE_UNKNOWN;
        }
        try {
            if (PermissionVersion.isAndroid10()) {
                return appOpsManager.unsafeCheckOpNoThrow(opName, context.getApplicationInfo().uid, context.getPackageName());
            } else {
                return appOpsManager.checkOpNoThrow(opName, context.getApplicationInfo().uid, context.getPackageName());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return MODE_UNKNOWN;
        }
    }

    /**
     * Returns the AppOpsManager state for a permission.
     *
     * @param opName the AppOpsManager field name to resolve through reflection
     * @param opDefaultValue the fallback value to use when reflection fails
     */
    @SuppressWarnings("ConstantConditions")
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    public static int getOpPermissionMode(Context context, @NonNull String opName, int opDefaultValue) {
        AppOpsManager appOpsManager;
        if (PermissionVersion.isAndroid6()) {
            appOpsManager = context.getSystemService(AppOpsManager.class);
        } else {
            appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        }
        // Although this SystemService should never be null, keep the check for defensive programming.
        if (appOpsManager == null) {
            return MODE_UNKNOWN;
        }
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            int opValue;
            try {
                Field opField = appOpsClass.getDeclaredField(opName);
                opValue = opField.getInt(null);
            } catch (NoSuchFieldException e) {
                opValue = opDefaultValue;
            }
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
            return ((int) checkOpNoThrowMethod.invoke(appOpsManager, opValue, context.getApplicationInfo().uid, context.getPackageName()));
        } catch (Exception e) {
            e.printStackTrace();
            return MODE_UNKNOWN;
        }
    }

    /**
     * Returns whether AppOpsManager contains the given op field.
     *
     * @param opName the AppOpsManager field name to check
     */
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    public static boolean isExistOpPermission(String opName) {
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            appOpsClass.getDeclaredField(opName);
            // If the field exists, return true.
            return true;
        } catch (Exception ignored) {
            // default implementation ignored
            return false;
        }
    }
}
