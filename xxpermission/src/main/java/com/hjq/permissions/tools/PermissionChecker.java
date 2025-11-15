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
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/02/09
 *    desc   : Permission error-checking utilities.
 *             Validates Activities, Fragments, and permission inputs.
 */
public final class PermissionChecker {

    /**
     * Check whether the {@link android.app.Activity} is in a valid state.
     */
    public static void checkActivityStatus(@Nullable Activity activity) {
        // Verify the Activity state before requesting permissions.
        if (activity == null) {
            // The Context instance must be an Activity.
            throw new IllegalArgumentException("The instance of the context must be an activity object");
        }

        if (activity.isFinishing()) {
            // The Activity must not be finishing. This often happens after an async operation.
            // Ensure the Activity state is valid before requesting permissions.
            throw new IllegalStateException("The activity has been finishing, " +
                    "please manually determine the status of the activity");
        }

        if (activity.isDestroyed()) {
            // The Activity must not be destroyed. This often happens after an async operation.
            // Ensure the Activity state is valid before requesting permissions.
            throw new IllegalStateException("The activity has been destroyed, " +
                    "please manually determine the status of the activity");
        }
    }

    /**
     * Check whether the {@link android.app.Fragment} is in a valid state.
     */
    @SuppressWarnings("deprecation")
    public static void checkAppFragmentStatus(@NonNull Fragment appFragment) {
        if (!appFragment.isAdded()) {
            // This Fragment has not been added/bound.
            throw new IllegalStateException("This app fragment has no binding added, " +
                    "please manually determine the status of the app fragment");
        }

        if (appFragment.isRemoving()) {
            // This Fragment is being removed.
            throw new IllegalStateException("This app fragment has been removed, " +
                    "please manually determine the status of the app fragment");
        }
    }

    /**
     * Validate the incoming permission list.
     */
    public static void checkPermissionList(@NonNull Activity activity, @Nullable List<IPermission> requestList, @Nullable AndroidManifestInfo manifestInfo) {
        if (requestList == null || requestList.isEmpty()) {
            // You cannot request permissions with an empty list.
            throw new IllegalArgumentException("The requested permission cannot be empty");
        }

        for (IPermission permission : requestList) {
            // Verify the Parcelable implementation.
            checkPermissionParcelable(permission);
            // Let each permission validate its own compliance.
            permission.checkCompliance(activity, requestList, manifestInfo);
        }
    }

    /**
     * Verify the Parcelable implementation of a permission class.
     */
    public static void checkPermissionParcelable(@NonNull IPermission permission) {
        Class<? extends IPermission> clazz = permission.getClass();
        String className = clazz.getName();

        // Fetch the CREATOR field.
        Field creatorField = null;
        try {
            creatorField = permission.getClass().getDeclaredField("CREATOR");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (creatorField == null) {
            // The permission class does not define a CREATOR field.
            throw new IllegalArgumentException("This permission class does not define the CREATOR field");
        }

        // Get the CREATOR object.
        Object creatorObject;
        try {
            // For static fields, use null as the instance.
            creatorObject = creatorField.get(null);
        } catch (Exception e) {
            // Access to the CREATOR field failed; it must be declared as public static final.
            throw new IllegalArgumentException("The CREATOR field in the " + className +
                    " has an access exception. Please modify CREATOR field with \"public static final\"");
        }

        if (!(creatorObject instanceof Parcelable.Creator)) {
            // The CREATOR field is not of type android.os.Parcelable.Creator.
            throw new IllegalArgumentException("The CREATOR field in this " + className +
                    " is not of type " + Parcelable.Creator.class.getName());
        }

        // Read the field's generic type.
        Type genericType = creatorField.getGenericType();

        // Ensure it’s a parameterized type.
        if (!(genericType instanceof ParameterizedType)) {
            // The generic type defined for CREATOR is missing.
            throw new IllegalArgumentException("The generic type defined for the CREATOR field in this " + className + " is empty");
        }

        // Extract generic arguments.
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();

        // Ensure there is exactly one generic argument.
        if (typeArguments.length != 1) {
            // CREATOR must define exactly one generic parameter.
            throw new IllegalArgumentException("The number of generics defined in the CREATOR field of this " + className + " can only be one");
        }

        // Validate the generic argument matches the current class.
        Type typeArgument = typeArguments[0];
        if (!(typeArgument instanceof Class && clazz.isAssignableFrom((Class<?>) typeArgument))) {
            // The generic type for CREATOR is incorrect.
            throw new IllegalArgumentException("The generic type defined in the CREATOR field of this " + className + " is incorrect");
        }

        // Sanity-check by invoking newArray.
        Parcelable.Creator<?> parcelableCreator = (Parcelable.Creator<?>) creatorObject;
        Object[] array = parcelableCreator.newArray(0);
        if (array == null) {
            // CREATOR.newArray returned null; it must not return null.
            throw new IllegalArgumentException("The newArray method of the CREATOR field in this " + className +
                    " returns an empty value. This method cannot return an empty value");
        }
    }
}
