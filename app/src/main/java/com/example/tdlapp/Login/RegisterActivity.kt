package com.example.tdlapp.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tdlapp.MainActivity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(dbHelper: DatabaseHelper, googleEmail: String, fromGoogleSignIn: Boolean) {
    // Variables de estado para los campos del formulario
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(googleEmail) } // Prellenar con el correo recibido
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Validaciones
    val isEmailValid = email.contains("@")
    val doPasswordsMatch = password == confirmPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Campo: Nombre de usuario
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            enabled = googleEmail.isEmpty(), // Deshabilita si proviene de Google Sign-In
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                disabledLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            ),
            isError = !isEmailValid && email.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Confirmar contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
            ),
            isError = !doPasswordsMatch && confirmPassword.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Botón: Registrar
        Button(
            onClick = {
                if (isEmailValid && doPasswordsMatch) {
                    val db = dbHelper.writableDatabase
                    try {
                        // Datos estáticos para el administrador
                        val adminUsername = "Camilo Alejandro"
                        val adminEmail = "AdminKA@gmail.com"
                        val adminPassword = "administrador"

                        // Comprobar si se está registrando como admin
                        val isRegisteringAsAdmin = username == adminUsername && email == adminEmail && password == adminPassword

                        db.execSQL(
                            "INSERT INTO ${DatabaseHelper.TABLE_USERS} (${DatabaseHelper.COLUMN_USER_USERNAME}, ${DatabaseHelper.COLUMN_USER_EMAIL}, ${DatabaseHelper.COLUMN_USER_PASSWORD}) VALUES (?, ?, ?)",
                            arrayOf(username, email, password)
                        )

                        // Obtener el ID del usuario recién creado
                        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
                        val userId = if (cursor.moveToFirst()) cursor.getInt(0) else -1
                        cursor.close()

                        // Asignar el rol adecuado al nuevo usuario
                        val roleId = if (isRegisteringAsAdmin) 1 else 2 // Asumiendo que el ID de rol 'Administrador' es 1 y 'Usuario' es 2
                        dbHelper.assignRoleToUser(userId, roleId)

                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()

                        // Mantener la sesión activa
                        val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        val userRole = dbHelper.getUserRole(userId)
                        with (sharedPreferences.edit()) {
                            putString("USER_NAME", username)
                            putString("USER_EMAIL", email)
                            putString("USER_ROLE", userRole)
                            apply()
                        }

                        if (fromGoogleSignIn) {
                            // Navegar a MainActivity si se registró con Google
                            val mainIntent = Intent(context, MainActivity::class.java).apply {
                                putExtra("USER_NAME", username)
                                putExtra("USER_EMAIL", email)
                                putExtra("USER_ROLE", userRole)
                            }
                            context.startActivity(mainIntent)
                            (context as ComponentActivity).finish()
                        } else {
                            // Volver a LoginActivity si se registró manualmente
                            (context as ComponentActivity).finish()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "Por favor, verifica tus datos.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar", color = Color.White)
        }
    }
}
