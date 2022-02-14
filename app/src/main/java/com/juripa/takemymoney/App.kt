package com.juripa.takemymoney

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import net.danlew.android.joda.JodaTimeAndroid

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
            private set

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = this

        JodaTimeAndroid.init(this)
    }
}