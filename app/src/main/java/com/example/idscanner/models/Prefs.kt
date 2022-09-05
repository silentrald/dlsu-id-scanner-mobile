package com.example.idscanner.models

import android.content.Context
import android.content.SharedPreferences
import com.example.idscanner.network.RemoteDataSource
import okhttp3.Cookie
import okhttp3.HttpUrl

class Prefs(val context: Context) {
  private val PREFS_NAME = "id_scanner"
  private val TAP_MODE_KEY = "tap_mode"
  private val COOKIE_PREFIX = "cookie_"
  private val COOKIE_LENGTH = "cookie_len"

  private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  // TAP MODE
  fun setTapMode(isIn: Boolean) {
    sharedPref.edit()
      .putBoolean(TAP_MODE_KEY, isIn)
      .apply()
  }

  fun getTapMode(): Boolean {
    return sharedPref.getBoolean(TAP_MODE_KEY, true)
  }

  // COOKIE FUNCTIONS
  fun getCookieSize(): Int {
    return sharedPref.getInt(COOKIE_LENGTH, 0)
  }

  fun getCookies(): List<Cookie> {
    val size = sharedPref.getInt(COOKIE_LENGTH, 0)
    if (size == 0) {
      return listOf()
    }

    val cookies = arrayListOf<Cookie>()
    for (i in 0 until size) {
      val cookie = Cookie.parse(
        HttpUrl.get("http://${RemoteDataSource.HOST}:${RemoteDataSource.PORT}"),
        sharedPref.getString("${COOKIE_PREFIX}$i", "")!!
      )

      if (cookie != null) {
        cookies.add(cookie)
      }
    }

    return cookies
  }

  fun setCookies(cookies: List<Cookie>) {
    clearCookies()

    val sp = sharedPref.edit()
    sp.putInt(COOKIE_LENGTH, cookies.size)
    for (i in cookies.indices) {
      sp.putString("$COOKIE_PREFIX$i", cookies[i].toString())
    }
    sp.apply()
  }

  /**
   * Removes all the cookies in shared preference
   */
  fun clearCookies() {
    val size = sharedPref.getInt(COOKIE_LENGTH, 0)
    if (size > 0) {
      val sp = sharedPref.edit()
      for (i in 0 until size) sp.remove("$COOKIE_PREFIX$i")
      sp.apply()
    }
  }
}