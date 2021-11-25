package com.project.vivian.launcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.project.vivian.MainActivity
import com.project.vivian.R
import com.project.vivian.VivianApp
import com.project.vivian.menu.MenuActivity
import com.project.vivian.preferences.Constantes

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        Handler(Looper.getMainLooper()).postDelayed({
            validateSession()
        }, 2000)
    }

    fun validateSession(){
        if (VivianApp.prefs!!.getBoolean(Constantes.KEY_LOGIN)){
            startActivity(Intent(this, MenuActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}