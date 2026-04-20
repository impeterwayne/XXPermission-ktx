package com.hjq.permissions.tools;

import android.app.Activity;
import android.app.Fragment;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.permission.base.IPermission;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Validation helpers for permission requests.
 */
public final class PermissionChecker {

    /** Validates the activity state before requesting permissions. */
    public static void checkActivityStatus(@Nullable Activity activity) {
        // Check whether the current Activity state is valid, If it is not, do not request permissions
        if (activity == null) {
            // The Context instance must be an Activity object
            throw new IllegalArgumentException("The instance of the context must be an activity object");
        }

        if (activity.isFinishing()) {
            // The current Activity object must not be finishing, This often happens when permissions are requested after an asynchronous operation
            // Please verify the Activity state in the caller before entering the permission request flow
            throw new IllegalStateException("The activity has been finishing, " +
                "please manually determine the status of the activity");
        }

        if (activity.isDestroyed()) {
            // The current Activity object must not be destroyed, This often happens when permissions are requested after an asynchronous operation
            // Please verify the Activity state in the caller before entering the permission request flow
            throw new IllegalStateException("The activity has been destroyed, " +
                "please manually determine the status of the activity");
        }
    }

    /** Validates the AndroidX fragment state before requesting permissions. */
    public static void checkAndroidXFragmentStatus(@NonNull androidx.fragment.app.Fragment xFragment) {
        if (!xFragment.isAdded()) {
            // This Fragment is not attached
            throw new IllegalStateException("This androidX fragment has no binding added, " +
                "please manually determine the status of the androidX fragment");
        }

        if (xFragment.isRemoving()) {
            // This Fragment has already been removed
            throw new IllegalStateException("This androidX fragment has been removed, " +
                "please manually determine the status of the androidX fragment");
        }
    }

    /** Validates the framework fragment state before requesting permissions. */
    @SuppressWarnings("deprecation")
    public static void checkAndroidFragmentStatus(@NonNull Fragment fragment) {
        if (!fragment.isAdded()) {
            // This Fragment is not attached
            throw new IllegalStateException("This android fragment has no binding added, " +
                "please manually determine the status of the android fragment");
        }

        if (fragment.isRemoving()) {
            // This Fragment has already been removed
            throw new IllegalStateException("This android fragment has been removed, " +
                "please manually determine the status of the android fragment");
        }
    }

    /** Validates the incoming permission list. */
    public static void checkPermissionList(@NonNull Activity activity, @Nullable List<IPermission> requestList, @Nullable AndroidManifestInfo manifestInfo) {
        if (requestList == null || requestList.isEmpty()) {
            // No permissions were passed in, yet a runtime permission request was attempted?
            throw new IllegalArgumentException("The requested permission cannot be empty");
        }

        for (IPermission permission : requestList) {
            // Check whether the permission Parcelable implementation has issues
            checkPermissionParcelable(permission);
            // Let the permission validate itself
            permission.checkCompliance(activity, requestList, manifestInfo);
        }
    }

    /** Validates the permission Parcelable implementation. */
    public static void checkPermissionParcelable(@NonNull IPermission permission) {
        Class<? extends IPermission> clazz = permission.getClass();
        String className = clazz.getName();

        // Get the CREATOR field
        Field creatorField = null;
        try {
            creatorField = permission.getClass().getDeclaredField("CREATOR");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (creatorField == null) {
            // This permission class does not define a CREATOR field
            throw new IllegalArgumentException("This permission class does not define the CREATOR field");
        }

        // get CREATOR object
        Object creatorObject;
        try {
            // Use null as the instance for a static field
            creatorObject = creatorField.get(null);
        } catch (Exception e) {
            // An error occurred while accessing the CREATOR field in the permission class. Please declare the CREATOR field as public static final
            throw new IllegalArgumentException("The CREATOR field in the " + className +
                " has an access exception. Please modify CREATOR field with \"public static final\"");
        }

        if (!(creatorObject instanceof Parcelable.Creator)) {
            // The CREATOR field in this permission class is not of type android.os.Parcelable.Creator
            throw new IllegalArgumentException("The CREATOR field in this " + className +
                " is not of type " + Parcelable.Creator.class.getName());
        }

        // Get the generic type of the field
        Type genericType = creatorField.getGenericType();

        // Check whether it is a parameterized type
        if (!(genericType instanceof ParameterizedType)) {
            // The generic type declared on the CREATOR field in this permission class is empty
            throw new IllegalArgumentException("The generic type defined for the CREATOR field in this " + className + " is empty");
        }

        // Get the generic argument
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();

        // Check whether there is exactly one generic argument
        if (typeArguments.length != 1) {
            // The generic type count declared on the CREATOR field in this permission class must be exactly one
            throw new IllegalArgumentException("The number of generics defined in the CREATOR field of this " + className + " can only be one");
        }

        // Get the generic argument type
        Type typeArgument = typeArguments[0];

        // Check whether the generic argument type matches the current class
        if (!(typeArgument instanceof Class && clazz.isAssignableFrom((Class<?>) typeArgument))) {
            // The generic type declared on the CREATOR field in this permission class is incorrect
            throw new IllegalArgumentException("The generic type defined in the CREATOR field of this " + className + " is incorrect");
        }

        // Call the newArray method directly to create the array
        Parcelable.Creator<?> parcelableCreator = (Parcelable.Creator<?>) creatorObject;
        Object[] array = parcelableCreator.newArray(0);
        if (array == null) {
            // The newArray method of the CREATOR field in this permission class returned null, but it must not return null
            throw new IllegalArgumentException("The newArray method of the CREATOR field in this " + className +
                " returns an empty value. This method cannot return an empty value");
        }
    }
}
