package com.example.idscanner.responses

import com.example.idscanner.models.Student

data class TapInResponse (
  val id: String,
  val student: Student
)
