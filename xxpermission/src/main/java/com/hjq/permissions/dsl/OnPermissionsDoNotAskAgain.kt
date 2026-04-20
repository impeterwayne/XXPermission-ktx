package com.hjq.permissions.dsl

fun interface OnPermissionsDoNotAskAgain {
    fun onDoNotAskAgain(permissions: List<String>, onUserResult: OnUserResultCallback)
}
