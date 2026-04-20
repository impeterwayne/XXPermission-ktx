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
                object : OnPermissionInterceptor {
                    override fun onRequestPermissionStart(
                        activity: Activity,
                        requestList: List<IPermission>,
                        fragmentFactory: PermissionFragmentFactory<*, *>,
                        permissionDescription: OnPermissionDescription,
                        callback: OnPermissionCallback?
                    ) {
                        val rationaleHandler = onShouldShowRationale
                        if (rationaleHandler == null) {
                            dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                            return
                        }

                        val rationalePermissions = requestList.filter { permission ->
                            try {
                                if (permission.getPermissionChannel(activity) != PermissionChannel.START_ACTIVITY) {
                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity,
                                        permission.getPermissionName()
                                    )
                                } else if (permission is SpecialPermission) {
                                    !permission.isGrantedPermission(activity)
                                } else {
                                    false
                                }
                            } catch (_: Exception) {
                                false
                            }
                        }.map { it.getPermissionName() }

                        if (rationalePermissions.isEmpty()) {
                            dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                            return
                        }

                        rationaleHandler.onShouldShowRationale(rationalePermissions) { isAgree ->
                            if (isAgree) {
                                dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                            } else {
                                val granted = requestList.filter { it.isGrantedPermission(activity) }
                                val denied = requestList.filterNot { it.isGrantedPermission(activity) }
                                callback?.onResult(granted, denied)
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
                        val doNotAskAgainHandler = onDoNotAskAgain
                        if (deniedList.isNotEmpty() && doNotAskAgainHandler != null) {
                            val doNotAskAgainList = deniedList.filter { it.isDoNotAskAgainPermission(activity) }
                            if (doNotAskAgainList.isNotEmpty()) {
                                val permissionNames = doNotAskAgainList.map { it.getPermissionName() }
                                doNotAskAgainHandler.onDoNotAskAgain(permissionNames) { isAgree ->
                                    if (isAgree) {
                                        val intents = PermissionSettingPage.getCommonPermissionSettingIntent(
                                            activity,
                                            *doNotAskAgainList.toTypedArray()
                                        )
                                        StartActivityAgent.startActivityForResult(
                                            activity,
                                            intents,
                                            XXPermissions.REQUEST_CODE
                                        )
                                    }
                                    callback?.onResult(grantedList, deniedList)
                                }
                                return
                            }
                        }
                        callback?.onResult(grantedList, deniedList)
                    }
                }
            )
            .request { grantedList, deniedList ->
                val grantedNames = grantedList.map { it.permissionName }
                val deniedNames = deniedList.map { it.permissionName }
                onResult?.onResult(deniedNames.isEmpty(), grantedNames, deniedNames)
            }
    }
}
