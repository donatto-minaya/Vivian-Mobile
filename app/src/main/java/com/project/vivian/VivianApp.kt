package com.project.vivian

import android.app.Application
import com.project.vivian.preferences.VivianPreferences

class VivianApp : Application() {

    companion object {
        var prefs: VivianPreferences?= null
    }

    override fun onCreate() {
        super.onCreate()
        prefs = VivianPreferences(applicationContext)
    }


}