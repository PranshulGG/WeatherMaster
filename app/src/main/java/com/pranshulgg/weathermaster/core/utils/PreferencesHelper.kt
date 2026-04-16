package com.pranshulgg.weathermaster.core.utils


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import org.json.JSONException
import org.json.JSONObject

object PreferencesHelper {

    private const val PREF_NAME = "app_prefs"
    private lateinit var prefs: SharedPreferences

    // -------------------- Init --------------------

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    // -------------------- String --------------------

    fun setString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String): String? = prefs.getString(key, null)

    // -------------------- Bool --------------------

    fun setBool(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun getBool(key: String): Boolean? =
        if (prefs.contains(key)) prefs.getBoolean(key, false) else null

    // -------------------- Int --------------------

    fun setInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun getInt(key: String): Int? =
        if (prefs.contains(key)) prefs.getInt(key, 0) else null

    // -------------------- Double --------------------

    fun setDouble(key: String, value: Double) {
        prefs.edit { putString(key, value.toString()) }
    }

    fun getDouble(key: String): Double? {
        val str = prefs.getString(key, null)
        return str?.toDoubleOrNull()
    }

    // -------------------- List<String> --------------------

    fun setStringList(key: String, value: List<String>) {
        prefs.edit { putStringSet(key, value.toSet()) }
    }

    fun getStringList(key: String): List<String>? =
        prefs.getStringSet(key, null)?.toList()

    // -------------------- JSON (Map) --------------------

    fun setJson(key: String, value: Map<String, Any?>) {
        val json = JSONObject(value).toString()
        setString(key, json)
    }

    fun getJson(key: String): Map<String, Any>? {
        val jsonStr = getString(key) ?: return null
        return try {
            val jsonObject = JSONObject(jsonStr)
            val map = mutableMapOf<String, Any>()
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k] = jsonObject.get(k)
            }
            map
        } catch (e: JSONException) {
            null
        }
    }

    // -------------------- Color --------------------

    fun setColor(key: String, color: Int) {
        setInt(key, color)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getColor(key: String): Int? = getInt(key)?.let { Color.valueOf(it).toArgb() }

    // -------------------- Utilities --------------------

    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    fun clear() {
        prefs.edit { clear() }
    }

    fun logAllPrefs() {
        val all = prefs.all
        if (all.isEmpty()) {
            println("Preferences empty.")
        } else {
            println("---- Preferences Dump ----")
            all.forEach { (k, v) ->
                println("$k: $v")
            }
            println("--------------------------")
        }
    }
}