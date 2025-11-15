package com.hjq.permissions.permission.dangerous;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import java.util.Objects;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : Standard implementation class for fitness and wellness data permissions
 */
public final class StandardFitnessAndWellnessDataPermission extends HealthDataBasePermission {

    /** Permission name */
    @NonNull
    private final String mPermissionName;
    /** Android version when this permission was introduced */
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
