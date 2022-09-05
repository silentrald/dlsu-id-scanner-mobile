package com.example.idscanner.repositories

import com.example.idscanner.network.AuthApi

class AuthRepo(
  private val api: AuthApi
): BaseRepo() {

  suspend fun login(
    username: String,
    password: String
  ) = safeApiCall {
    api.login(username, password)
  }

  suspend fun getUser() = safeApiCall {
    api.getUser()
  }

  suspend fun logout() = safeApiCall {
    api.logout()
  }

}