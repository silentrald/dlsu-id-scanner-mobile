package com.example.idscanner.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.idscanner.repositories.AuthRepo
import com.example.idscanner.repositories.BaseRepo
import com.example.idscanner.repositories.AttendanceRepo

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (
  private val ctx: Context,
  private val repo: BaseRepo
): ViewModelProvider.NewInstanceFactory() {
  override fun <T: ViewModel> create(modelClass: Class<T>): T {
    return when {
      modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(
        ctx,
        repo as AuthRepo
      ) as T
      modelClass.isAssignableFrom(AttendanceViewModel::class.java) -> AttendanceViewModel(
        repo as AttendanceRepo
      ) as T
      else -> throw IllegalArgumentException("ViewModelClass Not Found")
    }
  }
}