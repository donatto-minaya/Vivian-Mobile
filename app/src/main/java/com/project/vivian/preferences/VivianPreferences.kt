package com.project.vivian.preferences

import android.content.Context
import android.content.SharedPreferences

class VivianPreferences(context: Context) {

    val PREF_FILENAME = "com.project.vivian"

    val prefs: SharedPreferences = context.getSharedPreferences(PREF_FILENAME, 0)

    fun setBoolean(key: String, value: Boolean){
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String) : Boolean{
        return prefs.getBoolean(key, false);
    }

    // String
    fun setString(key: String, value: String){
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String) : String{
        return prefs.getString(key, "").toString()
    }

    fun clear(){
        prefs.edit().clear().apply()
    }
}