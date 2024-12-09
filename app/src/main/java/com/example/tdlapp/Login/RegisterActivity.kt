package com.example.tdlapp.Login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.ui.theme.AppTheme

class RegisterActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el helper de base de datos
        dbHelper = DatabaseHelper(this)

        // Obtén el correo enviado desde la actividad previa
        val googleEmail = intent.getStringExtra("GOOGLE_EMAIL") ?: ""
        val fromGoogleSignIn = intent.getBooleanExtra("FROM_GOOGLE_SIGN_IN", false)
        setContent {
            AppTheme {
                RegisterScreen(dbHelper, googleEmail, fromGoogleSignIn)
            }
        }
    }
}


