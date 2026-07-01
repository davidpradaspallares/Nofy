package com.nofy.app

import android.app.Application
import com.nofy.app.notification.GastoExtractionManager
import com.nofy.app.notification.NotificationFilterManager
import com.nofy.app.notification.NotificationPersistenceManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class NofyApplication : Application() {

    @Inject
    lateinit var notificationPersistenceManager: NotificationPersistenceManager

    @Inject
    lateinit var notificationFilterManager: NotificationFilterManager

    @Inject
    lateinit var gastoExtractionManager: GastoExtractionManager

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        gastoExtractionManager.initExtractorConfigs()
    }
}
