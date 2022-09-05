package com.example.idscanner.network

import com.example.idscanner.models.Student
import com.example.idscanner.responses.TapInResponse
import com.example.idscanner.responses.TapOutResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AttendanceApi {

  @FormUrlEncoded
  @POST("attendance/tap-in")
  suspend fun postTapIn(
    @Field("studentId") studentId: ULong
  ): TapInResponse

  @FormUrlEncoded
  @POST("attendance/tap-out")
  suspend fun postTapOut(
    @Field("studentId") studentId: ULong
  ): TapOutResponse

  @POST("student")
  suspend fun postStudent(
    @Body student: Student
  )
}