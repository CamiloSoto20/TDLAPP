package com.example.tdlapp


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tdlapp.data.DatabaseHelper



class RegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_activity)

        dbHelper = DatabaseHelper(this)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val db = dbHelper.writableDatabase
            db.execSQL("INSERT INTO ${DatabaseHelper.TABLE_USERS} (${DatabaseHelper.COLUMN_USER_EMAIL}, ${DatabaseHelper.COLUMN_USER_PASSWORD}) VALUES (?, ?)", arrayOf(emailText, passwordText))
            Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
