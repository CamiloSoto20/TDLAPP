package com.example.tdlapp


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

class RegisterActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContent {
            RegisterScreen(dbHelper)
        }
    }
}

@Composable
fun RegisterScreen(dbHelper: DatabaseHelper) {
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
            val db = dbHelper.writableDatabase
            db.execSQL(
                "INSERT INTO ${DatabaseHelper.TABLE_USERS} (${DatabaseHelper.COLUMN_USER_EMAIL}, ${DatabaseHelper.COLUMN_USER_PASSWORD}) VALUES (?, ?)",
                arrayOf(email, password)
            )
            Toast.makeText(context, "Registration successful.", Toast.LENGTH_SHORT).show()
            (context as ComponentActivity).finish()
        }) {
            Text("Register")
        }
    }
}
