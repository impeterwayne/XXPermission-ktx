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

    /**
     * add permissions
     */
    fun permissions(vararg permissions: IPermission): XXPermissionsExt {
        permissionList.addAll(permissions)
        return this
    }

    /**
     * add permissions
     */
    @JvmName("permissionsArray")
    fun permissions(permissions: Array<out IPermission>): XXPermissionsExt {
        permissionList.addAll(permissions)
        return this
    }

    /**
     * add permissions
     */
    fun permissions(permissions: List<IPermission>): XXPermissionsExt {
        permissionList.addAll(permissions)
        return this
    }

    /**
     * Called when you should tell user to allow these permissions in settings.
     */
    fun onDoNotAskAgain(onDoNotAskAgain: OnPermissionsDoNotAskAgain): XXPermissionsExt {
        this.onDoNotAskAgain = onDoNotAskAgain
        return this
    }

    /**
     * Called when you should show request permission rationale.
     */
    fun onShouldShowRationale(onShouldShowRationale: OnPermissionsShouldShowRationale): XXPermissionsExt {
        this.onShouldShowRationale = onShouldShowRationale
        return this
    }

    /**
     * Callback for the permissions request result.
     */
    fun onResult(onResult: OnPermissionResult): XXPermissionsExt {
        this.onResult = onResult
        return this
    }

    /**
     * Init permissions request
     */
    fun request() {
        XXPermissions.with(activity)
            .permissions(permissionList)
            .interceptor(
                object : OnPermissionInterceptor {
                    @Suppress("TooGenericExceptionCaught")
                    override fun onRequestPermissionStart(
                        activity: Activity,
                        requestList: List<IPermission>,
                        fragmentFactory: PermissionFragmentFactory<*, *>,
                        permissionDescription: OnPermissionDescription,
                        callback: OnPermissionCallback?
                    ) {
                        val rationale = onShouldShowRationale
                        if (rationale == null) {
                            // No custom rationale, proceed directly
                            dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                            return
                        }

                        // Build a list of permissions that need rationale (dangerous permissions only)
                        val rationalePermissions = requestList.filter { perm ->
                            try {
                                perm.getPermissionChannel(activity) != PermissionChannel.START_ACTIVITY_FOR_RESULT &&
                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity,
                                        perm.getRequestPermissionName(activity)
                                    )
                            } catch (_: Exception) {
                                false
                            }
                        }.map { it.getPermissionName() }

                        if (rationalePermissions.isEmpty()) {
                            // Nothing to explain, continue
                            dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                            return
                        }

                        rationale.onShouldShowRationale(rationalePermissions) { isAgree ->
                            if (isAgree) {
                                dispatchPermissionRequest(activity, requestList, fragmentFactory, permissionDescription, callback)
                            } else {
                                // User declined; synthesize a result based on current grant state
                                val granted = requestList.filter { it.isGrantedPermission(activity) }
                                val denied = requestList.filter { !it.isGrantedPermission(activity) }
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
                            // Extract the list of permissions marked as "Do not ask again"
                            val dnaList = deniedList.filter { it.isDoNotAskAgainPermission(activity) }
                            if (dnaList.isNotEmpty()) {
                                val dnaNames = dnaList.map { it.getPermissionName() }
                                doNotAskAgainHandler.onDoNotAskAgain(dnaNames) { isAgree ->
                                    if (isAgree) {
                                        // Try to navigate to settings for these permissions
                                        val intents = PermissionSettingPage.getCommonPermissionSettingIntent(activity, *dnaList.toTypedArray())
                                        StartActivityAgent.startActivityForResult(activity, intents, XXPermissions.REQUEST_CODE)
                                    }
                                    // Regardless, pass the current result through
                                    callback?.onResult(grantedList, deniedList)
                                }
                                return
                            }
                        }
                        // Default behavior: forward the result
                        callback?.onResult(grantedList, deniedList)
                    }
                }
            )
            .request { grantedList, deniedList ->
                val grantedNames = grantedList.map { it.permissionName }
                val deniedNames = deniedList.map { it.permissionName }
                val allGranted = deniedNames.isEmpty()
                onResult?.onResult(allGranted, grantedNames, deniedNames)
            }
    }
}
