package com.example.tdlapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.DatabaseHelper

class LoginActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContent {
            LoginScreen(dbHelper)
        }
    }
}

@Composable
fun LoginScreen(dbHelper: DatabaseHelper) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = {
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
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Login")
        }
        Button(onClick = {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }) {
            Text("Register")
        }
    }
}





