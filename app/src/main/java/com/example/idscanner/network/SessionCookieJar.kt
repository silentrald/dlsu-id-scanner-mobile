package com.example.idscanner.network

import android.content.Context
import android.util.Log
import com.example.idscanner.models.Prefs
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SessionCookieJar(ctx: Context) : CookieJar {
  private val TAG = "Cookies"

  private var prefs: Prefs
  private val cookies: ArrayList<Cookie> = arrayListOf()

  init {
    prefs = Prefs(ctx)
    cookies.addAll(prefs.getCookies())
  }

  override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
    Log.d(TAG, url.toString())
    if (url.toString().endsWith("/api/auth/login-temp")) {
      prefs.setCookies(cookies)
      this.cookies.clear()
      this.cookies.addAll(cookies)
    } else if (url.toString().endsWith("/api/auth/logout")) {
      Log.d(TAG, "Logout")
      prefs.setCookies(listOf())
      this.cookies.clear()
    }
  }

  override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
    return cookies.toMutableList()
  }


}