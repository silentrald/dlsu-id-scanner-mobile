package com.example.idscanner.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.idscanner.databinding.ActivityLoginBinding
import com.example.idscanner.network.AuthApi
import com.example.idscanner.network.RemoteDataSource
import com.example.idscanner.network.Resource
import com.example.idscanner.repositories.AuthRepo
import com.example.idscanner.viewmodels.AuthViewModel
import com.example.idscanner.viewmodels.ViewModelFactory

class LoginActivity : AppCompatActivity() {
  private val TAG: String = "Login"

  private lateinit var binding: ActivityLoginBinding
  private lateinit var model: AuthViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val remoteDataSource = RemoteDataSource(applicationContext)
    // TODO: Singleton
    val factory = ViewModelFactory(applicationContext, AuthRepo(remoteDataSource.buildApi(AuthApi::class.java)))
    model = ViewModelProvider(this, factory)[AuthViewModel::class.java]

    model.userResponse.observe(this) {
      when (it) {
        is Resource.Success -> {
          toTapActivity()
        }
        is Resource.Failure -> {}
      }
    }

    model.loginResponse.observe(this) {
      when (it) {
        is Resource.Success -> {
          toTapActivity()
        }
        is Resource.Failure -> {
          Log.d(TAG, "Bad")
          Toast.makeText(
            baseContext,
            "Auth Failed",
            Toast.LENGTH_LONG
          ).show()
        }
      }
    }

    binding.btnLogin.setOnClickListener {
      val username = binding.etUsername.text.toString().trim()
      val password = binding.etPassword.text.toString().trim()

      // Validate username and password
      model.login(username, password)
    }
  }

  private fun toTapActivity() {
    val intent = Intent(this, TapActivity::class.java)
    intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(intent)
  }
}