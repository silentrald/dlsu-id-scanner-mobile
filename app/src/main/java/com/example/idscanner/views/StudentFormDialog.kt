package com.example.idscanner.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.idscanner.R

class StudentFormDialog: AppCompatDialogFragment() {
	lateinit var etIdNumber: EditText
	lateinit var etFname: EditText
	lateinit var etLname: EditText

	var cancelListener: (() -> Unit)? = null
	var acceptListener: (() -> Unit)? = null

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val builder = AlertDialog.Builder(activity)

		val view = layoutInflater.inflate(R.layout.layout_student_form, null)

		builder.setView(view)
		builder.setTitle("Create New Student")
		builder.setNegativeButton("Cancel") { _, _ ->
			cancelListener?.invoke()
		}
		builder.setPositiveButton("Create") { _, _ ->
			acceptListener?.invoke()
		}

		// TODO: Change this
		etIdNumber = view.findViewById(R.id.et_id_number)
		etFname = view.findViewById(R.id.et_fname)
		etLname = view.findViewById(R.id.et_lname)

		return builder.create()
	}
}