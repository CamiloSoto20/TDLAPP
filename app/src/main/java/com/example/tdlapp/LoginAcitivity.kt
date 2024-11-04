package com.example.tdlapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("TDLAPP", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
            )
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Button(
            onClick = {
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery(
                    "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_EMAIL}=? AND ${DatabaseHelper.COLUMN_USER_PASSWORD}=?",
                    arrayOf(email, password)
                )
                if (cursor.moveToFirst()) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    cursor.close()
                    (context as ComponentActivity).finish()
                } else {
                    cursor.close()
                    Toast.makeText(context, "Acceso Fallido.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Iniciar Sesion", color = Color.White)
        }
        Button(
            onClick = {
                context.startActivity(Intent(context, RegisterActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Registro", color = Color.White)
        }
    }
}




