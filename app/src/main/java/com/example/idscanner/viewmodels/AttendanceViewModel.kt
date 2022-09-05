package com.example.idscanner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idscanner.models.Student
import com.example.idscanner.network.Resource
import com.example.idscanner.repositories.AttendanceRepo
import com.example.idscanner.responses.TapInResponse
import com.example.idscanner.responses.TapOutResponse
import kotlinx.coroutines.launch

class AttendanceViewModel (
  private val rep: AttendanceRepo
): ViewModel() {

  private val _tapInResponse: MutableLiveData<Resource<TapInResponse>> = MutableLiveData()
  val tapInResponse: LiveData<Resource<TapInResponse>>
    get() = _tapInResponse

  private val _tapOutResponse: MutableLiveData<Resource<TapOutResponse>> = MutableLiveData()
  val tapOutResponse: LiveData<Resource<TapOutResponse>>
    get() = _tapOutResponse

  private val _createStudentResponse: MutableLiveData<Resource<Unit>> = MutableLiveData()
  val createStudentResponse: LiveData<Resource<Unit>>
    get() = _createStudentResponse

  fun tapIn(studentId: ULong) = viewModelScope.launch {
    _tapInResponse.value = rep.postTapIn(studentId)
  }

  fun tapOut(studentId: ULong) = viewModelScope.launch {
    _tapOutResponse.value = rep.postTapOut(studentId)
  }

  fun postStudent(student: Student) = viewModelScope.launch {
    _createStudentResponse.value = rep.postStudent(student)
  }
}