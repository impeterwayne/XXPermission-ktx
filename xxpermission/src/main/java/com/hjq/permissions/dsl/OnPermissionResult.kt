package com.hjq.permissions.dsl

fun interface OnPermissionResult {
    fun onResult(allGranted: Boolean, grantedList: List<String>, deniedList: List<String>)
}
