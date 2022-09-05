package com.example.idscanner.network

import com.example.idscanner.responses.UserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

  @FormUrlEncoded
  @POST("auth/login-temp")
  suspend fun login(
    @Field("username") username: String,
    @Field("password") password: String
  )

  @GET("user")
  suspend fun getUser(): UserResponse

  @POST("auth/logout")
  suspend fun logout()
}