@file:Suppress("unused", "deprecation")

package com.hjq.permissions.dsl

import android.app.Activity
import android.app.Fragment
import androidx.core.app.ActivityCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.OnPermissionDescription
import com.hjq.permissions.OnPermissionInterceptor
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.fragment.factory.PermissionFragmentFactory
import com.hjq.permissions.permission.PermissionChannel
import com.hjq.permissions.permission.base.IPermission
import com.hjq.permissions.permission.common.SpecialPermission
import com.hjq.permissions.start.StartActivityAgent
import com.hjq.permissions.tools.PermissionSettingPage

class XXPermissionsExt private constructor(private val activity: Activity) {

    private val permissionList = mutableListOf<IPermission>()
    private var onResult: OnPermissionResult? = null
    private var onShouldShowRationale: OnPermissionsShouldShowRationale? = null
    private var onDoNotAskAgain: OnPermissionsDoNotAskAgain? = null

    companion object {
        @JvmStatic
        fun with(activity: Activity): XXPermissionsExt {
            return XXPermissionsExt(activity)
        }

        @JvmStatic
        fun with(fragment: Fragment): XXPermissionsExt {
            return with(requireNotNull(fragment.activity))
        }

        @JvmStatic
        fun with(fragment: androidx.fragment.app.Fragment): XXPermissionsExt {
            return with(fragment.requireActivity())
        }
    }

    private class PermissionRequestInterceptor(
        private val rationaleHandler: OnPermissionsShouldShowRationale?,
        private val doNotAskAgainHandler: OnPermissionsDoNotAskAgain?
    ) : OnPermissionInterceptor {

        override fun onRequestPermissionStart(
            activity: Activity,
            requestList: List<IPermission>,
            fragmentFactory: PermissionFragmentFactory<*, *>,
            permissionDescription: OnPermissionDescription,
            callback: OnPermissionCallback?
        ) {
            val rationalePermissions = findRationalePermissions(activity, requestList)
            if (rationaleHandler == null || rationalePermissions.isEmpty()) {
                continueRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                return
            }

            rationaleHandler.onShouldShowRationale(rationalePermissions) { isAgree ->
                if (isAgree) {
                    continueRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                } else {
                    finishWithCurrentState(activity, requestList, callback)
                }
            }
        }

        override fun onRequestPermissionEnd(
            activity: Activity,
            skipRequest: Boolean,
            requestList: List<IPermission>,
            grantedList: List<IPermission>,
            deniedList: List<IPermission>,
            callback: OnPermissionCallback?
        ) {
            val doNotAskAgainPermissions = findDoNotAskAgainPermissions(activity, deniedList)
            if (deniedList.isEmpty() || doNotAskAgainHandler == null || doNotAskAgainPermissions.isEmpty()) {
                callback?.onResult(grantedList, deniedList)
                return
            }

            doNotAskAgainHandler.onDoNotAskAgain(doNotAskAgainPermissions.map { it.getPermissionName() }) { isAgree ->
                if (isAgree) {
                    openSettings(activity, doNotAskAgainPermissions)
                }
                callback?.onResult(grantedList, deniedList)
            }
        }

        private fun continueRequest(
            activity: Activity,
            requestList: List<IPermission>,
            fragmentFactory: PermissionFragmentFactory<*, *>,
            permissionDescription: OnPermissionDescription,
            callback: OnPermissionCallback?
        ) {
            dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
        }

        private fun findRationalePermissions(
            activity: Activity,
            requestList: List<IPermission>
        ): List<String> {
            return requestList
                .filter { shouldShowRationale(activity, it) }
                .map { it.getPermissionName() }
        }

        private fun shouldShowRationale(activity: Activity, permission: IPermission): Boolean {
            return try {
                when {
                    permission.getPermissionChannel(activity) != PermissionChannel.START_ACTIVITY -> {
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.getPermissionName())
                    }
                    permission is SpecialPermission -> !permission.isGrantedPermission(activity)
                    else -> false
                }
            } catch (_: Exception) {
                false
            }
        }

        private fun finishWithCurrentState(
            activity: Activity,
            requestList: List<IPermission>,
            callback: OnPermissionCallback?
        ) {
            val granted = requestList.filter { it.isGrantedPermission(activity) }
            val denied = requestList.filterNot { it.isGrantedPermission(activity) }
            callback?.onResult(granted, denied)
        }

        private fun findDoNotAskAgainPermissions(
            activity: Activity,
            deniedList: List<IPermission>
        ): List<IPermission> {
            return deniedList.filter { it.isDoNotAskAgainPermission(activity) }
        }

        private fun openSettings(activity: Activity, permissions: List<IPermission>) {
            val intents = PermissionSettingPage.getCommonPermissionSettingIntent(
                activity,
                *permissions.toTypedArray()
            )
            StartActivityAgent.startActivityForResult(
                activity,
                intents,
                XXPermissions.REQUEST_CODE
            )
        }
    }

    fun permissions(vararg permissions: IPermission): XXPermissionsExt {
        permissionList.addAll(permissions)
        return this
    }

    @JvmName("permissionsArray")
    fun permissions(permissions: Array<out IPermission>): XXPermissionsExt {
        permissionList.addAll(permissions)
        return this
    }

    fun permissions(permissions: List<IPermission>): XXPermissionsExt {
        permissionList.addAll(permissions)
        return this
    }

    fun onDoNotAskAgain(onDoNotAskAgain: OnPermissionsDoNotAskAgain): XXPermissionsExt {
        this.onDoNotAskAgain = onDoNotAskAgain
        return this
    }

    fun onShouldShowRationale(onShouldShowRationale: OnPermissionsShouldShowRationale): XXPermissionsExt {
        this.onShouldShowRationale = onShouldShowRationale
        return this
    }

    fun onResult(onResult: OnPermissionResult): XXPermissionsExt {
        this.onResult = onResult
        return this
    }

    fun request() {
        XXPermissions.with(activity)
            .permissions(permissionList)
            .interceptor(
                PermissionRequestInterceptor(
                    rationaleHandler = onShouldShowRationale,
                    doNotAskAgainHandler = onDoNotAskAgain
                )
            )
            .request { grantedList, deniedList ->
                val grantedNames = grantedList.map { it.permissionName }
                val deniedNames = deniedList.map { it.permissionName }
                onResult?.onResult(deniedNames.isEmpty(), grantedNames, deniedNames)
            }
    }
}
