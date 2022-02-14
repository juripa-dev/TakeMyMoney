package com.juripa.takemymoney

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences

object Prefs {
    const val PREFERENCE = "sharepreference"

    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
    }

    fun get(context: Context, key: String): Double? {
        return getPref(context).getString(key, null)?.toDouble()
    }

    fun setDouble(context: Context, key: String, value: Double) {
        getPref(context).edit().putString(key, value.toString()).apply()
    }

    fun getString(context: Context, key: String): String? {
        return getPref(context).getString(key, null)
    }

    fun setString(context: Context, key: String, value: String) {
        getPref(context).edit().putString(key, value).apply()
    }
}