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
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : Base class for permissions
 */
public abstract class BasePermission implements IPermission {

    /** Op permission mode: unknown mode */
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
        // If the object to compare has the same memory address as this one, return true
        if (obj == this) {
            return true;
        }
        // Overriding equals allows List and Map collections to distinguish
        // whether two different permission objects represent the same permission.
        // If their names are the same, treat them as the same permission.
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
        // Check if targetSdkVersion meets requirements
        checkSelfByTargetSdkVersion(activity);
        // Check if AndroidManifest.xml meets requirements
        if (manifestInfo != null) {
            List<PermissionManifestInfo> permissionInfoList = manifestInfo.permissionInfoList;
            PermissionManifestInfo currentPermissionInfo = findPermissionInfoByList(permissionInfoList, getPermissionName());
            checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        }
        // Check if the requested permission list meets requirements
        checkSelfByRequestPermissions(activity, requestList);
    }

    /**
     * Check if targetSdkVersion meets requirements; if not, throw an exception
     */
    protected void checkSelfByTargetSdkVersion(@NonNull Context context) {
        int minTargetSdkVersion = getMinTargetSdkVersion(context);
        // Must set the correct targetSdkVersion to detect permissions correctly
        if (PermissionVersion.getTargetVersion(context) >= minTargetSdkVersion) {
            return;
        }

        throw new IllegalStateException("Request \"" + getPermissionName() + "\" permission, " +
                "The targetSdkVersion SDK must be " + minTargetSdkVersion +
                " or more, if you do not want to upgrade targetSdkVersion, " +
                "please apply with the old permission");
    }

    /**
     * Whether the current permission is statically declared in the manifest file
     */
    protected abstract boolean isRegisterPermissionByManifestFile();

    /**
     * Check if AndroidManifest.xml meets requirements; if not, throw an exception
     */
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        if (!isRegisterPermissionByManifestFile()) {
            return;
        }
        // Check whether the current permission is declared in the manifest file,
        // and also check if the declared maxSdkVersion attribute has issues
        checkPermissionRegistrationStatus(currentPermissionInfo, getPermissionName());
    }

    /**
     * Check if the requested permission list meets requirements; if not, throw an exception
     */
    protected void checkSelfByRequestPermissions(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // default implementation ignored
        // No default implementation, left for subclasses to handle
    }

    /**
     * Check the registration status of the permission, throws exception if invalid
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
            // The dynamically requested permission is not declared in the manifest file; two possibilities:
            // 1. If your project did not declare this permission, simply add it in the manifest file.
            // 2. If you did declare it, check whether the compiled apk contains it. If not,
            //    the framework’s judgment is correct.
            //    Usually caused by a third-party SDK or framework declaring:
            //    <uses-permission android:name="xxx" tools:node="remove"/>
            //    Fix: declare <uses-permission android:name="xxx" tools:node="replace"/> in your project to override.
            // Example: https://github.com/getActivity/XXPermissions/issues/98
            throw new IllegalStateException("Please register permissions in the AndroidManifest.xml file " +
                    "<uses-permission android:name=\"" + checkPermission + "\" />");
        }

        int manifestMaxSdkVersion = permissionInfo.maxSdkVersion;
        if (manifestMaxSdkVersion < lowestMaxSdkVersion) {
            // The maxSdkVersion declared in the manifest file does not meet the minimum requirement; two possibilities:
            // 1. If you declared this attribute, modify or remove it according to the error message.
            // 2. If you did not declare it, check if the compiled apk contains it. If so,
            //    the framework’s judgment is correct.
            //    Usually caused by a third-party SDK or framework declaring:
            //    <uses-permission android:name="xxx" android:maxSdkVersion="xx"/>
            //    Fix: declare <uses-permission android:name="xxx" tools:node="replace"/> in your project.
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
     * Get the current project's minSdkVersion
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
     * Get a specific permission info from the list
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
     * Check if a dangerous permission is granted
     */
    @RequiresApi(PermissionVersion.ANDROID_6)
    public static boolean checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check whether to show a rationale for requesting the permission
     */
    @RequiresApi(PermissionVersion.ANDROID_6)
    @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions", "BooleanMethodIsAlwaysInverted"})
    public static boolean shouldShowRequestPermissionRationale(@NonNull Activity activity, @NonNull String permission) {
        // Fix for memory leak when calling shouldShowRequestPermissionRationale on Android 12.
        // Android 12L and Android 13 tested fine, Google has fixed it there.
        // But Android 12 still has this historical issue, unavoidable for all Android developers.
        // Issue: https://github.com/getActivity/XXPermissions/issues/133
        if (PermissionVersion.getCurrentVersion() == PermissionVersion.ANDROID_12) {
            try {
                // Also contributed a free fix to Google’s AndroidX project; merge request accepted.
                // This should solve memory leaks on nearly 1 billion Android 12 devices.
                // Pull Request: https://github.com/androidx/androidx/pull/435
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
     * Use AppOpsManager to check whether a permission is granted
     *
     * @param opName               must be a field from {@link AppOpsManager} starting with OPSTR
     * @param defaultGranted       return granted if the status cannot be determined
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
     * Use AppOpsManager to check whether a permission is granted
     *
     * @param opFieldName          field name in {@link AppOpsManager}
     * @param opDefaultValue       fallback value if reflection fails
     * @param defaultGranted       return granted if the status cannot be determined
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
     * Get the status of a permission from AppOpsManager
     *
     * @param opName               must be a field from {@link AppOpsManager} starting with OPSTR
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
        // This SystemService should never be null, but defensive programming just in case
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
     * Get the status of a permission from AppOpsManager
     *
     * @param opName                field name in {@link AppOpsManager}
     * @param opDefaultValue        fallback value if reflection fails
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
        // This SystemService should never be null, but defensive programming just in case
        if (appOpsManager == null) {
            return MODE_UNKNOWN;
        }
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            int opValue;
            try {
                Field opField = appOpsClass.getDeclaredField(opName);
                opValue = (int) opField.get(Integer.class);
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
     * Check whether AppOpsManager contains a specific Op permission
     *
     * @param opName                field name in {@link AppOpsManager}
     */
    @RequiresApi(PermissionVersion.ANDROID_4_4)
    public static boolean isExistOpPermission(String opName) {
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            appOpsClass.getDeclaredField(opName);
            // Field exists, return true
            return true;
        } catch (Exception ignored) {
            // default implementation ignored
            return false;
        }
    }
}
