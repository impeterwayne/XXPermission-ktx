@file:Suppress("unused", "deprecation")

package com.hjq.permissions.dsl

import android.app.Activity
import android.app.Fragment
import com.hjq.permissions.permission.base.IPermission

inline fun Activity.xxPermissions(block: XXPermissionsDSL.() -> Unit) =
    XXPermissionsDSL(XXPermissionsExt.Companion.with(this)).apply { block(this) }.xxPermissions.request()

inline fun androidx.fragment.app.Fragment.xxPermissions(block: XXPermissionsDSL.() -> Unit) =
    XXPermissionsDSL(XXPermissionsExt.Companion.with(this)).apply { block(this) }.xxPermissions.request()

inline fun Fragment.xxPermissions(block: XXPermissionsDSL.() -> Unit) =
    XXPermissionsDSL(XXPermissionsExt.Companion.with(this)).apply { block(this) }.xxPermissions.request()

class XXPermissionsDSL(@PublishedApi internal val xxPermissions: XXPermissionsExt) {
    /**
     * add permissions
     */
    fun permissions(vararg permissions: IPermission) {
        xxPermissions.permissions(permissions)
    }

    /**
     * add permissions
     */
    @JvmName("permissionsArray")
    fun permissions(permissions: Array<out IPermission>) {
        xxPermissions.permissions(permissions)
    }

    /**
     * add permissions
     */
    fun permissions(permissions: List<IPermission>) {
        xxPermissions.permissions(permissions)
    }

    /**
     * Called when you should tell user to allow these permissions in settings.
     */
    fun onDoNotAskAgain(onDoNotAskAgain: OnPermissionsDoNotAskAgain) {
        xxPermissions.onDoNotAskAgain(onDoNotAskAgain)
    }

    /**
     * Called when you should show request permission rationale.
     */
    fun onShouldShowRationale(onShouldShowRationale: OnPermissionsShouldShowRationale) {
        xxPermissions.onShouldShowRationale(onShouldShowRationale)
    }

    /**
     * Callback for the permissions request result.
     */
    fun onResult(onResult: OnPermissionResult) {
        xxPermissions.onResult(onResult)
    }
}