package com.example.idscanner.views

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.idscanner.databinding.ActivityTapBinding
import com.example.idscanner.models.Prefs
import com.example.idscanner.models.Student
import com.example.idscanner.network.AuthApi
import com.example.idscanner.network.RemoteDataSource
import com.example.idscanner.network.Resource
import com.example.idscanner.network.AttendanceApi
import com.example.idscanner.repositories.AuthRepo
import com.example.idscanner.repositories.AttendanceRepo
import com.example.idscanner.viewmodels.AuthViewModel
import com.example.idscanner.viewmodels.AttendanceViewModel
import com.example.idscanner.viewmodels.ViewModelFactory
import java.lang.NumberFormatException

class TapActivity : AppCompatActivity() {
  private val TAG: String = "Tap"

  private var nfcAdapter : NfcAdapter? = null
  private var studentId: ULong = 0UL
  private lateinit var prefs: Prefs

  private lateinit var binding: ActivityTapBinding
  private lateinit var attendanceViewModel: AttendanceViewModel
  private lateinit var authViewModel: AuthViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityTapBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Animate
    binding.rbTap.startRippleAnimation()
  }

  override fun onResume() {
    super.onResume()

    prefs = Prefs(applicationContext)
    setupNFC()
    setupViewModels()
    setupListeners()

    handleNFC()
  }

  override fun onPause() {
    super.onPause()

    nfcAdapter!!.disableForegroundDispatch(this)
    nfcAdapter!!.disableReaderMode(this)
    nfcAdapter = null
  }

  private fun toLoginActivity() {
    val intent = Intent(this, LoginActivity::class.java)
    intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(intent)
  }

  /**
   * Setup NFC Scanning
   */
  private fun setupNFC() {
    nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    if (nfcAdapter == null) {
      Toast.makeText(this,
        "NFC is not available",
        Toast.LENGTH_LONG).show()
      finish()
      return
    }

    if (!nfcAdapter?.isEnabled!!) {
      Toast.makeText(this,
        "NFC is not enabled",
        Toast.LENGTH_LONG).show()
      finish()
      return
    }

    onNewIntent(intent)
  }

  private fun setupViewModels() {
    val remoteDataSource = RemoteDataSource(applicationContext)
    // TODO: Singleton
    val attendanceFactory = ViewModelFactory(applicationContext, AttendanceRepo(remoteDataSource.buildApi(AttendanceApi::class.java)))
    attendanceViewModel = ViewModelProvider(this, attendanceFactory)[AttendanceViewModel::class.java]

    val authFactory = ViewModelFactory(applicationContext, AuthRepo(remoteDataSource.buildApi(AuthApi::class.java)))
    authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

    authViewModel.logoutResponse.observe(this) {
      when (it) {
        is Resource.Success -> {
          toLoginActivity()
        }
        is Resource.Failure -> {
          Toast.makeText(baseContext,
            "Could not logout",
            Toast.LENGTH_SHORT).show()
        }
      }
    }

    attendanceViewModel.tapInResponse.observe(this) {
      when (it) {
        is Resource.Success -> {
          // TODO: Can be designed to be better
          val student = it.value.student
          Toast.makeText(baseContext,
            "Tapped in: ${student.fname} ${student.lname} (${student.idNumber})",
            Toast.LENGTH_SHORT).show()
        }
        is Resource.Failure -> {
          val err = it.errorBody!!.string()
          if (err.lowercase().endsWith("student not found")) {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle("Alert")
            alertBuilder.setMessage("Student does not exist in the database, would you like to register the new student")
            alertBuilder.setNegativeButton("No", null)
            alertBuilder.setPositiveButton("Yes") { _, _ ->
              val dialog = StudentFormDialog()
              dialog.acceptListener = {
                Log.i(TAG, "Accept")
                val idNumber = dialog.etIdNumber.text.toString().trim()
                val fname = dialog.etFname.text.toString().trim()
                val lname = dialog.etLname.text.toString().trim()

                Log.i(TAG, "$idNumber $fname $lname")

                try {
                  attendanceViewModel.postStudent(Student(null, idNumber.toInt(), studentId, fname, lname))
                } catch (e: NumberFormatException) {
                  Log.e(TAG, Log.getStackTraceString(e))
                }
              }
              dialog.show(supportFragmentManager, "Example Dialog")
            }
            alertBuilder.show()
            return@observe
          }

          Toast.makeText(baseContext,
            err,
            Toast.LENGTH_SHORT).show()
        }
      }
    }

    attendanceViewModel.tapOutResponse.observe(this) {
      when (it) {
        is Resource.Success -> {
          // TODO: Can be designed to be better
          val student = it.value.student
          Toast.makeText(baseContext,
            "Tapped out: ${student.fname} ${student.lname} (${student.idNumber})",
            Toast.LENGTH_SHORT).show()
        }
        is Resource.Failure -> {
          Log.e(TAG, it.errorBody!!.string())
          Toast.makeText(baseContext,
            it.errorBody.string(),
            Toast.LENGTH_SHORT).show()
        }
      }
    }


    attendanceViewModel.createStudentResponse.observe(this) {
      when (it) {
        is Resource.Success -> {
          Toast.makeText(baseContext,
            "Student Created, Re-tap the id",
            Toast.LENGTH_SHORT).show()
        }
        is Resource.Failure -> {
          Log.e(TAG, it.errorBody!!.string())
          Toast.makeText(baseContext,
            it.errorBody.string(),
            Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun setupListeners() {
    binding.btnLogout.setOnClickListener {
      authViewModel.logout()
    }

    binding.tbMode.isChecked = prefs.getTapMode()
    binding.tbMode.setOnClickListener {
      prefs.setTapMode(binding.tbMode.isChecked)
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    setIntent(intent)
  }

  private fun handleNFC() {
    if (intent == null) return

    val action = intent?.action
    if (NfcAdapter.ACTION_TAG_DISCOVERED != action) {
      return
    }

    val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
    val tagId : ByteArray = tag?.id ?: return

    if (tagId.size > 4) {
      Log.e(TAG, "Error")
      return
    }

    // NOTE: Might cause an error if the id has more than 4 bytes
    studentId = 0UL
    for (byte in tagId) {
      studentId = (studentId shl 8) or (byte.toULong() and 255u)
    }

    if (binding.tbMode.isChecked) {
      attendanceViewModel.tapIn(studentId)
    } else {
      attendanceViewModel.tapOut(studentId)
    }
  }
}