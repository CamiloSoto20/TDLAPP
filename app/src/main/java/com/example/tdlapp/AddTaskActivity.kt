package com.example.tdlapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.ui.theme.TDLAppTheme

class AddTaskActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContent {
            TDLAppTheme {
                AddTaskScreen(dbHelper)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddTaskScreen(dbHelper: DatabaseHelper) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf("") }
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Tarea") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrpicion") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Fecha de Entrega") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Button(
                onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty() && dueDate.isNotEmpty()) {
                        val db = dbHelper.writableDatabase
                        db.execSQL(
                            "INSERT INTO ${DatabaseHelper.TABLE_TASKS} (${DatabaseHelper.COLUMN_TASK_NAME}, ${DatabaseHelper.COLUMN_TASK_DESCRIPTION}, ${DatabaseHelper.COLUMN_TASK_DUE_DATE}, ${DatabaseHelper.COLUMN_TASK_COMPLETED}) VALUES (?, ?, ?, ?)",
                            arrayOf(name, description, dueDate, 0)
                        )
                        db.close()
                        Toast.makeText(context, "Tarea Agregada", Toast.LENGTH_SHORT).show()
                        (context as Activity).setResult(Activity.RESULT_OK)
                        context.finish()
                    } else {
                        Toast.makeText(context, "Rellena Los Cuadros", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Guardar Tarea", color = Color.White)
            }
        }
    }
}





