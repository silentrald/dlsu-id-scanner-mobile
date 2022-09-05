package com.example.idscanner.repositories

import android.util.Log
import com.example.idscanner.models.Student
import com.example.idscanner.models.User
import com.example.idscanner.network.AttendanceApi

class AttendanceRepo(
  private val api: AttendanceApi
): BaseRepo() {

  suspend fun postTapIn(studentId: ULong) = safeApiCall {
    api.postTapIn(studentId)
  }

  suspend fun postTapOut(studentId: ULong) = safeApiCall {
    api.postTapOut(studentId)
  }

  suspend fun postStudent(student: Student) = safeApiCall {
    api.postStudent(student)
  }

}