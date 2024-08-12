package com.batuscode.hosbes.utility

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PreferenceManager(context: Context) {
    val context = context

    lateinit var sharedPreferences:SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("Hosbes" , Context.MODE_PRIVATE)
    }

    fun clear(){
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun saveSession(key:String , value: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(key , value)
        editor.apply()
    }

    fun getSession(key: String): Boolean{
        return sharedPreferences.getBoolean(key , false)
    }

    fun saveuid(key: String , value: String){
        val editor = sharedPreferences.edit()
        editor.putString(key , value)
        editor.apply()
    }

    fun getuidShared(key: String): String {
        return sharedPreferences.getString(key , null).toString()
    }

    fun saveString(key: String,value: String){
        val editor = sharedPreferences.edit()
        editor.putString(key , value)
        editor.apply()
    }

    fun getString(key: String):String{
        return sharedPreferences.getString(key , null).toString()
    }

    fun saveLastChatTime(key:String , value:Long){
        val editor = sharedPreferences.edit()
        editor.putLong(key , value)
        editor.apply()
    }

    fun getLastChatTime(key: String): Long {
        return sharedPreferences.getLong(key , 0L)
    }

    fun saveLong(key:String , value:Long){
        val editor = sharedPreferences.edit()
        editor.putLong(key , value)
        editor.apply()
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key , 0L)
    }
}