package com.hjq.permissions.dsl

fun interface OnUserResultCallback {
    /**
     * Call it when the user agrees or refuses to allow these permissions in settings.
     *
     * @param isAgree agrees or refuses
     */
    fun onResult(isAgree: Boolean)
}