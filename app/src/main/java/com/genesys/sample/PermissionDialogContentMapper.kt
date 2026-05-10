package com.genesys.sample

import android.content.Context
import androidx.annotation.DrawableRes
import com.hjq.permissions.permission.PermissionLists

data class PermissionDialogContent(
    @field:DrawableRes val imageResId: Int,
    val title: String,
    val description: String
)

class PermissionDialogContentMapper(private val context: Context) {
    fun map(permission: String, isDoNotAskAgain: Boolean): PermissionDialogContent {
        return when (permission) {
            PermissionLists.getPostNotificationsPermission().getPermissionName() -> PermissionDialogContent(
                imageResId = android.R.drawable.ic_dialog_info,
                title = context.getString(
                    if (isDoNotAskAgain) R.string.permission_notification_denied_title else R.string.permission_notification_needed_title
                ),
                description = context.getString(
                    if (isDoNotAskAgain) R.string.permission_notification_denied_description else R.string.permission_notification_needed_description
                )
            )

            PermissionLists.getUseFullScreenIntentPermission().getPermissionName() -> PermissionDialogContent(
                imageResId = android.R.drawable.ic_dialog_alert,
                title = context.getString(
                    if (isDoNotAskAgain) R.string.permission_full_screen_denied_title else R.string.permission_full_screen_needed_title
                ),
                description = context.getString(
                    if (isDoNotAskAgain) R.string.permission_full_screen_denied_description else R.string.permission_full_screen_needed_description
                )
            )

            PermissionLists.getScheduleExactAlarmPermission().getPermissionName() -> PermissionDialogContent(
                imageResId = android.R.drawable.ic_lock_idle_alarm,
                title = context.getString(
                    if (isDoNotAskAgain) R.string.permission_exact_alarm_denied_title else R.string.permission_exact_alarm_needed_title
                ),
                description = context.getString(
                    if (isDoNotAskAgain) R.string.permission_exact_alarm_denied_description else R.string.permission_exact_alarm_needed_description
                )
            )

            PermissionLists.getSystemAlertWindowPermission().getPermissionName() -> PermissionDialogContent(
                imageResId = android.R.drawable.ic_menu_view,
                title = context.getString(
                    if (isDoNotAskAgain) R.string.permission_overlay_denied_title else R.string.permission_overlay_needed_title
                ),
                description = context.getString(
                    if (isDoNotAskAgain) R.string.permission_overlay_denied_description else R.string.permission_overlay_needed_description
                )
            )

            PermissionLists.getReadMediaImagesPermission().getPermissionName(),
            PermissionLists.getReadMediaVisualUserSelectedPermission().getPermissionName(),
            PermissionLists.getWriteExternalStoragePermission().getPermissionName() -> PermissionDialogContent(
                imageResId = android.R.drawable.ic_menu_gallery,
                title = context.getString(
                    if (isDoNotAskAgain) R.string.permission_media_denied_title else R.string.permission_media_needed_title
                ),
                description = context.getString(
                    if (isDoNotAskAgain) R.string.permission_media_denied_description else R.string.permission_media_needed_description
                )
            )

            else -> PermissionDialogContent(
                imageResId = android.R.drawable.ic_dialog_info,
                title = context.getString(
                    if (isDoNotAskAgain) R.string.permission_default_denied_title else R.string.permission_default_needed_title
                ),
                description = context.getString(
                    if (isDoNotAskAgain) R.string.permission_default_denied_description else R.string.permission_default_needed_description
                )
            )
        }
    }
}
