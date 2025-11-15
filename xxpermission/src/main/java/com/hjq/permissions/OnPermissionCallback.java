package com.hjq.permissions;

import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android Wheel Brother
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : Permission request result callback interface
 */
public interface OnPermissionCallback {

    /**
     * Permission request result callback
     *
     * @param grantedList   list of granted permissions
     * @param deniedList    list of denied permissions
     */
    void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList);
}
