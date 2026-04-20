@file:Suppress("unused", "deprecation")

package com.hjq.permissions.dsl

import android.app.Activity
import android.app.Fragment
import com.hjq.permissions.permission.base.IPermission

inline fun Activity.xxPermissions(block: XXPermissionsDSL.() -> Unit) =
    XXPermissionsDSL(XXPermissionsExt.with(this)).apply(block).xxPermissions.request()

inline fun androidx.fragment.app.Fragment.xxPermissions(block: XXPermissionsDSL.() -> Unit) =
    XXPermissionsDSL(XXPermissionsExt.with(this)).apply(block).xxPermissions.request()

inline fun Fragment.xxPermissions(block: XXPermissionsDSL.() -> Unit) =
    XXPermissionsDSL(XXPermissionsExt.with(this)).apply(block).xxPermissions.request()

class XXPermissionsDSL(@PublishedApi internal val xxPermissions: XXPermissionsExt) {

    fun permissions(vararg permissions: IPermission) {
        xxPermissions.permissions(*permissions)
    }

    @JvmName("permissionsArray")
    fun permissions(permissions: Array<out IPermission>) {
        xxPermissions.permissions(permissions)
    }

    fun permissions(permissions: List<IPermission>) {
        xxPermissions.permissions(permissions)
    }

    fun onDoNotAskAgain(onDoNotAskAgain: OnPermissionsDoNotAskAgain) {
        xxPermissions.onDoNotAskAgain(onDoNotAskAgain)
    }

    fun onShouldShowRationale(onShouldShowRationale: OnPermissionsShouldShowRationale) {
        xxPermissions.onShouldShowRationale(onShouldShowRationale)
    }

    fun onResult(onResult: OnPermissionResult) {
        xxPermissions.onResult(onResult)
    }
}
