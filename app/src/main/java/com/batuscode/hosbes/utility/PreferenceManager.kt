package com.batuscode.hosbes.utility

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    val context = context
    fun sharedPreference(key:String): SharedPreferences {
        return context.getSharedPreferences(key , Context.MODE_PRIVATE)
    }

    fun saveSession(key:String , value: Boolean){
        val editor = sharedPreference(key).edit()
        editor.putBoolean(key , value)
        editor.apply()
    }

    fun getSession(key: String): Boolean{
        return sharedPreference(key).getBoolean(key , false)
    }

    fun saveuid(key: String , value: String){
        val editor = sharedPreference(key).edit()
        editor.putString(key , value)
        editor.apply()
    }

    fun getuidShared(key: String): String {
        return sharedPreference(key).getString(key , null).toString()
    }
}