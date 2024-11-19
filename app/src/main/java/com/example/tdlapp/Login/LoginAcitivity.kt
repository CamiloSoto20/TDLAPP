package com.example.tdlapp.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.tdlapp.ui.theme.AppTheme

class LoginActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        setContent {
            AppTheme {
                LoginScreen(dbHelper)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(dbHelper: DatabaseHelper) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Validación de correo
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
                            if (cursor != null && cursor.moveToFirst()) {
                                val userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_USERNAME))
                                val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL))
                                cursor.close()

                                val mainIntent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("USER_NAME", userName)
                                    putExtra("USER_EMAIL", userEmail)
                                }
                                context.startActivity(mainIntent)
                                (context as ComponentActivity).finish()
                            } else {
                                cursor?.close()
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
        }
    }
}


