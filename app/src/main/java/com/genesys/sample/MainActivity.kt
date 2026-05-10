package com.genesys.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.genesys.sample.databinding.ActivityMainBinding
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.dsl.OnUserResultCallback
import com.hjq.permissions.dsl.xxPermissions
import com.hjq.permissions.permission.PermissionLists
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val permissionDialogContentMapper by lazy { PermissionDialogContentMapper(this) }

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
                    showDoNotAskAgainDialog(permissions[0], userResult)
                }
                onResult { allGranted, _, deniedData ->
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
            Timber.tag("Here").d("Restrict: $hasFullImages, selectedOnly: $hasSelectedOnly, isRestricted: $isRestricted")
        }

        binding.btnSequencePermission.setOnClickListener {
            xxPermissions {
                permissions(
                    PermissionLists.getSystemAlertWindowPermission(true),
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

                onResult { allGranted, _, deniedList ->
                    if (allGranted) {
                        Timber.tag("Here").d("All sequence permissions granted!")
                    } else {
                        Timber.tag("Here").w("Sequence permissions denied: %s", deniedList)
                    }
                }
            }
        }
    }

    private fun showRationaleDialog(permission: String, onUserResult: OnUserResultCallback) {
        val content = permissionDialogContentMapper.map(permission, isDoNotAskAgain = false)
        showPermissionDialog(
            content = content,
            positiveButtonText = getString(R.string.permission_button_grant),
            negativeButtonText = getString(R.string.permission_button_cancel),
            onUserResult = onUserResult
        )
    }

    private fun showDoNotAskAgainDialog(permission: String, onUserResult: OnUserResultCallback) {
        val content = permissionDialogContentMapper.map(permission, isDoNotAskAgain = true)
        showPermissionDialog(
            content = content,
            positiveButtonText = getString(R.string.permission_button_go_to_settings),
            negativeButtonText = getString(R.string.permission_button_cancel),
            onUserResult = onUserResult
        )
    }

    private fun showPermissionDialog(
        content: PermissionDialogContent,
        positiveButtonText: String,
        negativeButtonText: String,
        onUserResult: OnUserResultCallback
    ) {
        PermissionDialogFragment.show(
            fragmentManager = supportFragmentManager,
            content = content,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            onUserResult = onUserResult
        )
    }

}
