package com.hjq.permissions.dsl

fun interface OnPermissionsDoNotAskAgain {
    /**
     * Called when you should tell user to allow these permissions in settings.
     *
     * @param doNotAskAgainList Permissions that should allow in settings.
     * @param onUserResult Call it when the user agrees or refuses to allow these permissions in settings.
     */
    fun onDoNotAskAgain(doNotAskAgainList: List<String>, onUserResult: OnUserResultCallback)
}