package com.example.tdlapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tdlapp.data.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        dbHelper = DatabaseHelper(this)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_EMAIL}=? AND ${DatabaseHelper.COLUMN_USER_PASSWORD}=?", arrayOf(emailText, passwordText))
            if (cursor.moveToFirst()) {
                startActivity(Intent(this, MainActivity::class.java))
                cursor.close()
                finish() // Cierra la actividad de login después del inicio de sesión exitoso
            } else {
                cursor.close()
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}




