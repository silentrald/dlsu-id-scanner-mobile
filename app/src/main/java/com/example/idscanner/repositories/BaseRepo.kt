package com.example.idscanner.repositories

import android.util.Log
import com.example.idscanner.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepo {
  suspend fun<T> safeApiCall(
    apiCall: suspend () -> T
  ): Resource<T> {
    return withContext(Dispatchers.IO) {
      try {
        Resource.Success(apiCall.invoke())
      } catch (err: Throwable) {
        Log.e("Repo", "Error: ${err.message}")
        when (err) {
          is HttpException -> {
            Resource.Failure(false, err.code(), err.response()?.errorBody())
          }
          else -> {
            Resource.Failure(true, null, null)
          }
        }
      }
    }
  }
}