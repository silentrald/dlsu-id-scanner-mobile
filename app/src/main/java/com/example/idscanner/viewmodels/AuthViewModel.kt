package com.example.idscanner.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idscanner.models.Prefs
import com.example.idscanner.network.Resource
import com.example.idscanner.repositories.AuthRepo
import com.example.idscanner.responses.UserResponse
import kotlinx.coroutines.launch

class AuthViewModel(
  ctx: Context,
  private val repo: AuthRepo
): ViewModel() {

  private val prefs: Prefs

  private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
  val userResponse: LiveData<Resource<UserResponse>>
    get() = _userResponse

  private val _loginResponse: MutableLiveData<Resource<Unit>> = MutableLiveData()
  val loginResponse: LiveData<Resource<Unit>>
    get() = _loginResponse

  private val _logoutResponse: MutableLiveData<Resource<Unit>> = MutableLiveData()
  val logoutResponse: LiveData<Resource<Unit>>
    get() = _logoutResponse

  init {
    prefs = Prefs(ctx)
    val cookies = prefs.getCookieSize()
    if (cookies > 0) {
      getUser()
    }
  }

  private fun getUser() = viewModelScope.launch {
    _userResponse.value = repo.getUser()
  }

  fun login(
    username: String,
    password: String
  ) = viewModelScope.launch {
    _loginResponse.value = repo.login(username, password)
  }

  fun logout() = viewModelScope.launch {
    val response = repo.logout()

    if (response is Resource.Success) {
      prefs.clearCookies()
    }

    _logoutResponse.value = response
  }
}