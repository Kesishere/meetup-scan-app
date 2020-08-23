package com.kes.meetupscanapp

import android.app.Application
import androidx.room.Room
import com.kes.meetupscanapp.db.AppDatabase
import timber.log.Timber

class App : Application() {
    companion object {
        lateinit var appDatabase: AppDatabase
    }
    override fun onCreate() {
        super.onCreate()

        appDatabase =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "db").build()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }



}