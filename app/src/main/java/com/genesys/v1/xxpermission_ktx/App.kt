package com.genesys.v1.xxpermission_ktx

import android.app.Application
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


/**
 * @author : CuongNK
 * @created : 9/1/2025
 **/
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
    }
}