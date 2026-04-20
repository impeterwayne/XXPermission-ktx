package com.hjq.permissions.permission.dangerous;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import java.util.Objects;

/**
 * Standard implementation of fitness and wellness data permissions.
 */
public final class StandardFitnessAndWellnessDataPermission extends HealthDataBasePermission {

    /** Permission name */
    @NonNull
    private final String mPermissionName;
    /** Android version where the permission was introduced */
    private final int mFromAndroidVersion;

    public static final Creator<StandardFitnessAndWellnessDataPermission> CREATOR = new Creator<StandardFitnessAndWellnessDataPermission>() {

        @Override
        public StandardFitnessAndWellnessDataPermission createFromParcel(Parcel source) {
            return new StandardFitnessAndWellnessDataPermission(source);
        }

        @Override
        public StandardFitnessAndWellnessDataPermission[] newArray(int size) {
            return new StandardFitnessAndWellnessDataPermission[size];
        }
    };

    public StandardFitnessAndWellnessDataPermission(@NonNull String permissionName, int fromAndroidVersion) {
        mPermissionName = permissionName;
        mFromAndroidVersion = fromAndroidVersion;
    }

    private StandardFitnessAndWellnessDataPermission(Parcel in) {
        this(Objects.requireNonNull(in.readString()), in.readInt());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mPermissionName);
        dest.writeInt(mFromAndroidVersion);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return mPermissionName;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return mFromAndroidVersion;
    }
}