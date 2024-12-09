package com.example.tdlapp.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tdlapp.MainActivity
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.ui.theme.AppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task

class LoginActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Verificar si hay una sesión activa
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val userName = sharedPreferences.getString("USER_NAME", null)
        val userRole = sharedPreferences.getString("USER_ROLE", null) // Obtener el rol del usuario

        if (userEmail != null && userName != null && userRole != null) {
            // Si hay una sesión activa, ir a MainActivity
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", userRole) // Pasar el rol del usuario a MainActivity
            }
            startActivity(mainIntent)
            finish()
        } else {
            // Si no hay sesión, mostrar pantalla de login
            setContent {
                AppTheme {
                    LoginScreen(dbHelper, googleSignInClient) {
                        handleGoogleSignIn()
                    }
                }
            }
        }
    }

    private fun handleGoogleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(Exception::class.java)
                account?.let {
                    // Verificar si la cuenta está en la base de datos
                    val db = dbHelper.readableDatabase
                    val cursor = db.rawQuery(
                        "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_EMAIL}=?",
                        arrayOf(it.email)
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        // Si la cuenta ya está registrada, iniciar sesión
                        val userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
                        val userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_USERNAME))
                        val userRole = dbHelper.getUserRole(userId) // Obtener el rol del usuario
                        cursor.close()

                        // Guardar sesión en SharedPreferences
                        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        with (sharedPreferences.edit()) {
                            putString("USER_NAME", userName)
                            putString("USER_EMAIL", it.email)
                            putString("USER_ROLE", userRole) // Guardar el rol del usuario
                            apply()
                        }

                        // Navegar a MainActivity
                        val mainIntent = Intent(this, MainActivity::class.java).apply {
                            putExtra("USER_NAME", userName)
                            putExtra("USER_EMAIL", it.email)
                            putExtra("USER_ROLE", userRole) // Pasar el rol del usuario a MainActivity
                        }
                        startActivity(mainIntent)
                        finish()
                    } else {
                        cursor?.close()
                        // Si no está registrada, ir a RegisterActivity
                        val intent = Intent(this, RegisterActivity::class.java).apply {
                            putExtra("GOOGLE_EMAIL", it.email)
                            putExtra("FROM_GOOGLE_SIGN_IN", true)  // Indicar que proviene de Google Sign-In
                        }
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




