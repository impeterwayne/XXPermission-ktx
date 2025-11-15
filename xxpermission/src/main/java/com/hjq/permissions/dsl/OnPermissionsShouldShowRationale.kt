package com.hjq.permissions.dsl

fun interface OnPermissionsShouldShowRationale {
    /**
     * Called when you should show request permission rationale.
     *
     * @param shouldShowRationaleList Permissions that you should explain.
     * @param onUserResult Call it when the user agrees or refuses to allow permission request.
     */
    fun onShouldShowRationale(
        shouldShowRationaleList: List<String>,
        onUserResult: OnUserResultCallback
    )
}