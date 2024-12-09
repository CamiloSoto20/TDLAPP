package com.example.tdlapp.Login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tdlapp.MainActivity
import com.example.tdlapp.R
import com.example.tdlapp.data.DatabaseHelper
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(dbHelper: DatabaseHelper, googleSignInClient: GoogleSignInClient, onGoogleSignInClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isEmailValid = email.contains("@") && email.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.c64f4b9b3abddf6894923b219410cc84), // Reemplaza con tu imagen
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "TDLAPP",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isEmailValid && email.isNotEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isEmailValid) {
                        val db = dbHelper.readableDatabase
                        try {
                            val cursor = db.rawQuery(
                                "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_EMAIL}=? AND ${DatabaseHelper.COLUMN_USER_PASSWORD}=?",
                                arrayOf(email, password)
                            )
                            if (cursor.moveToFirst()) {
                                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_USER_ID))
                                val userName = cursor.getString(cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_USER_USERNAME))
                                val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_USER_EMAIL))
                                val userRole = dbHelper.getUserRole(userId) // Obtener el rol del usuario
                                cursor.close()

                                // Guardar sesión en SharedPreferences
                                val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                with (sharedPreferences.edit()) {
                                    putString("USER_NAME", userName)
                                    putString("USER_EMAIL", userEmail)
                                    putString("USER_ROLE", userRole) // Guardar el rol del usuario
                                    apply()
                                }

                                val mainIntent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("USER_NAME", userName)
                                    putExtra("USER_EMAIL", userEmail)
                                    putExtra("USER_ROLE", userRole) // Pasar el rol del usuario a MainActivity
                                }
                                context.startActivity(mainIntent)
                                (context as ComponentActivity).finish()
                            } else {
                                cursor.close()
                                Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al consultar la base de datos", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, "Correo no válido", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Iniciar con logos de Google y blanco
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Iniciar con:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onGoogleSignInClick) {
                    Image(
                        painter = painterResource(R.drawable.pngwing_com__2_), // Logo de Google
                        contentDescription = "Google",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

