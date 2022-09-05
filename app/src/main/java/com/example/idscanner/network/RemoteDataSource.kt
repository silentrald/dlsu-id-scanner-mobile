package com.example.idscanner.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource(private val ctx: Context) {
  companion object {
    const val PORT = 5000
    const val HOST = "192.168.254.120"
    private const val BASE_URL = "http://$HOST:$PORT/api/"
  }

  fun<Api> buildApi(
    api: Class<Api>
  ): Api {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
      .addNetworkInterceptor(interceptor)
      .cookieJar(SessionCookieJar(ctx))
      .connectTimeout(10, TimeUnit.SECONDS)
      .writeTimeout(10, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()

    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(client)
      .build()
      .create(api)
  }
}