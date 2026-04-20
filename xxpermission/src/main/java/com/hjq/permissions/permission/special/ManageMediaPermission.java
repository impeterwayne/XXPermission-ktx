package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage media permission.
 */
public final class ManageMediaPermission extends SpecialPermission {

    /** Current permission name. Note: this constant field is for internal framework use only and is not exposed externally. If you need the permission name string, it directly from {@link PermissionNames}. */
    public static final String PERMISSION_NAME = PermissionNames.MANAGE_MEDIA;

    public static final Creator<ManageMediaPermission> CREATOR = new Creator<ManageMediaPermission>() {

        @Override
        public ManageMediaPermission createFromParcel(Parcel source) {
            return new ManageMediaPermission(source);
        }

        @Override
        public ManageMediaPermission[] newArray(int size) {
            return new ManageMediaPermission[size];
        }
    };

    public ManageMediaPermission() {
        // default implementation ignored
    }

    private ManageMediaPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_12;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid12()) {
            return true;
        }
        return MediaStore.canManageMedia(context);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(3);
        Intent intent;

        if (PermissionVersion.isAndroid12()) {
            intent = new Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            intent = new Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA);
            intentList.add(intent);
        }

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // Indicates that this permission must be declared statically in AndroidManifest.xml.
        return true;
    }
}