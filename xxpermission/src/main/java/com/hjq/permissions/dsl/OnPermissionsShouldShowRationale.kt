package com.hjq.permissions.dsl

fun interface OnPermissionsShouldShowRationale {
    fun onShouldShowRationale(permissions: List<String>, onUserResult: OnUserResultCallback)
}
