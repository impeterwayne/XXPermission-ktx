package com.genesys.v1.xxpermission_ktx

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.genesys.v1.xxpermission_ktx.databinding.ActivityMainBinding
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.dsl.OnPermissionsDoNotAskAgain
import com.hjq.permissions.dsl.OnUserResultCallback
import com.hjq.permissions.dsl.xxPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.start.StartActivityAgent.startActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTestPermission.setOnClickListener {
            xxPermissions{
                permissions(PermissionLists.getReadMediaImagesPermission())
                permissions(PermissionLists.getReadMediaVisualUserSelectedPermission())
                permissions(PermissionLists.getWriteExternalStoragePermission())
                onDoNotAskAgain { permissions ,userResult ->
                    Timber.tag("Here").d("Do not ask again")
                }
                onShouldShowRationale {  permissions ,userResult ->
                    Timber.tag("Here").d("Rationale: ${permissions}")
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
            val hasFullImages = XXPermissions.isGrantedPermission(this@MainActivity, PermissionLists.getReadMediaImagesPermission())
            val hasSelectedOnly = XXPermissions.isGrantedPermission(this@MainActivity, PermissionLists.getReadMediaVisualUserSelectedPermission())
            val isRestricted = hasSelectedOnly && !hasFullImages
            Timber.tag("Here").d("Restrict: ${hasFullImages}")
        }

        binding.btnSequencePermission.setOnClickListener {
            xxPermissions {
                permissions(
                    PermissionLists.getPostNotificationsPermission(),
                    PermissionLists.getUseFullScreenIntentPermission(),
                    PermissionLists.getScheduleExactAlarmPermission(),
                    PermissionLists.getSystemAlertWindowPermission(),
                )
                onShouldShowRationale { shouldShowRationaleList, onUserResult ->
                    Timber.tag("naoh_debug").d( "onShouldShowRationale: ")
                }
                onDoNotAskAgain(object : OnPermissionsDoNotAskAgain {
                    override fun onDoNotAskAgain(
                        doNotAskAgainList: List<String>,
                        onUserResult: OnUserResultCallback
                    ) {
                        Timber.tag("naoh_debug").d( "onDoNotAskAgain: ")
                    }
                })

                onResult { allGranted, grantedList, deniedList ->

                }
            }
        }
    }


}
