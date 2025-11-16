package com.genesys.v1.xxpermission_ktx

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.genesys.v1.xxpermission_ktx.databinding.ActivityMainBinding
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.dsl.OnUserResultCallback
import com.hjq.permissions.dsl.xxPermissions
import com.hjq.permissions.permission.PermissionLists
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTestPermission.setOnClickListener {
            xxPermissions {
                permissions(PermissionLists.getReadMediaImagesPermission())
                permissions(PermissionLists.getReadMediaVisualUserSelectedPermission())
                permissions(PermissionLists.getWriteExternalStoragePermission())
                onDoNotAskAgain { permissions, userResult ->
                    showDoNotAskAgainDialog(permissions[0],userResult)
                }
                onResult { allGranted, grantedData, deniedData ->
                    if (allGranted) {
                        startActivity(Intent(this@MainActivity, PhotoPickerActivity::class.java))
                    } else {
                        Timber.tag("Here").w("Media permission denied: %s", deniedData)
                    }
                }
            }
        }

        binding.btnCheckRestrict.setOnClickListener {
            val hasFullImages = XXPermissions.isGrantedPermission(
                this@MainActivity,
                PermissionLists.getReadMediaImagesPermission()
            )
            val hasSelectedOnly = XXPermissions.isGrantedPermission(
                this@MainActivity,
                PermissionLists.getReadMediaVisualUserSelectedPermission()
            )
            val isRestricted = hasSelectedOnly && !hasFullImages
            Timber.tag("Here").d("Restrict: ${hasFullImages}")
        }

        binding.btnSequencePermission.setOnClickListener {
            xxPermissions {
                permissions(
                    PermissionLists.getSystemAlertWindowPermission(),
                    PermissionLists.getPostNotificationsPermission(),
                    PermissionLists.getUseFullScreenIntentPermission(),
                    PermissionLists.getScheduleExactAlarmPermission()
                )
                onShouldShowRationale { shouldShowRationaleList, onUserResult ->
                    Timber.tag("Rationale").d("$shouldShowRationaleList")
                  showRationaleDialog(shouldShowRationaleList[0], onUserResult)
                }
                onDoNotAskAgain { doNotAskAgainList, onUserResult ->
                    Timber.tag("Ask").d("$doNotAskAgainList")
                   showDoNotAskAgainDialog(doNotAskAgainList[0], onUserResult)
                }

                onResult { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Timber.tag("Here").d("All sequence permissions granted!")
                    } else {
                        Timber.tag("Here")
                            .w("Sequence permissions denied: %s", deniedList)
                    }
                }
            }
        }
    }

    /**
     * Shows a specific rationale dialog for a given permission.
     */
    private fun showRationaleDialog(permission: String, onUserResult: OnUserResultCallback) {
        val context = this@MainActivity
        // Get a specific message based on the permission
        val message = when (permission) {
            PermissionLists.getPostNotificationsPermission().getRequestPermissionName(this) -> "We need notification permission to send you important updates and alerts."
            PermissionLists.getUseFullScreenIntentPermission().getRequestPermissionName(this) -> "This permission is required to show critical alerts (like an incoming call) as full-screen notifications."
            PermissionLists.getScheduleExactAlarmPermission().getRequestPermissionName(this) -> "We need to schedule exact alarms for timely reminders and critical tasks."
            PermissionLists.getSystemAlertWindowPermission().getRequestPermissionName(this) -> "This permission allows the app to display information over other apps, which is needed for some features."
            else -> "This permission is required for the app to function properly." // Fallback
        }

        AlertDialog.Builder(context)
            .setTitle("Permission Needed")
            .setMessage(message)
            .setPositiveButton("Grant") { dialog, _ ->
                onUserResult.onResult(true)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                onUserResult.onResult(false)
                dialog.dismiss()
            }
            .setCancelable(false) // Force user to make a choice
            .show()
    }

    /**
     * Shows a dialog explaining that the user has permanently denied a permission
     * and offers to take them to settings.
     */
    private fun showDoNotAskAgainDialog(
        permission: String,
        onUserResult: OnUserResultCallback
    ) {
        val context = this@MainActivity
        val message = when (permission) {
            PermissionLists.getPostNotificationsPermission().getRequestPermissionName(this) -> "You have permanently denied notification permission. To enable it, please go to the app settings."
            PermissionLists.getUseFullScreenIntentPermission().getRequestPermissionName(this) -> "You have permanently denied the full-screen intent permission. Please go to app settings to enable it."
            PermissionLists.getScheduleExactAlarmPermission().getRequestPermissionName(this) -> "You have permanently denied scheduling exact alarms. Please go to app settings to enable it."
            PermissionLists.getSystemAlertWindowPermission().getRequestPermissionName(this) -> "You have permanently denied the 'display over other apps' permission. Please go to app settings to enable it."
            else -> "You have permanently denied a required permission. Please go to app settings to enable it."
        }

        AlertDialog.Builder(context)
            .setTitle("Permission Denied")
            .setMessage(message)
            .setPositiveButton("Go to Settings") { dialog, _ ->
                onUserResult.onResult(true)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                onUserResult.onResult(false)
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}