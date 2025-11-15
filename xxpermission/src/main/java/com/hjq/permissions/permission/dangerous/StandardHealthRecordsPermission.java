package com.hjq.permissions.permission.dangerous;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import java.util.Objects;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/07/14
 *    desc   : Standard implementation class for health records permissions
 */
public final class StandardHealthRecordsPermission extends HealthDataBasePermission {

    /** Permission name */
    @NonNull
    private final String mPermissionName;
    /** Android version when this permission was introduced */
    private final int mFromAndroidVersion;

    public static final Creator<StandardHealthRecordsPermission> CREATOR = new Creator<StandardHealthRecordsPermission>() {

        @Override
        public StandardHealthRecordsPermission createFromParcel(Parcel source) {
            return new StandardHealthRecordsPermission(source);
        }

        @Override
        public StandardHealthRecordsPermission[] newArray(int size) {
            return new StandardHealthRecordsPermission[size];
        }
    };

    public StandardHealthRecordsPermission(@NonNull String permissionName, int fromAndroidVersion) {
        mPermissionName = permissionName;
        mFromAndroidVersion = fromAndroidVersion;
    }

    private StandardHealthRecordsPermission(Parcel in) {
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
