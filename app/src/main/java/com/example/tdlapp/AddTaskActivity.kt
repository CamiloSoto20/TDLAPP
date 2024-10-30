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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.DatabaseHelper

class AddTaskActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContent {
            AddTaskScreen(dbHelper)
        }
    }

    @Composable
    fun AddTaskScreen(dbHelper: DatabaseHelper) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf("") }
        val context = LocalContext.current

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Task Name") }
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Task Description") }
            )
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Task Due Date") }
            )
            Button(onClick = {
                if (name.isNotEmpty() && description.isNotEmpty() && dueDate.isNotEmpty()) {
                    val db = dbHelper.writableDatabase
                    db.execSQL(
                        "INSERT INTO ${DatabaseHelper.TABLE_TASKS} (${DatabaseHelper.COLUMN_TASK_NAME}, ${DatabaseHelper.COLUMN_TASK_DESCRIPTION}, ${DatabaseHelper.COLUMN_TASK_DUE_DATE}, ${DatabaseHelper.COLUMN_TASK_COMPLETED}) VALUES (?, ?, ?, ?)",
                        arrayOf(name, description, dueDate, 0)
                    )
                    db.close()
                    Toast.makeText(context, "Task added", Toast.LENGTH_SHORT).show()
                    (context as Activity).setResult(Activity.RESULT_OK)
                    context.finish()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save Task")
            }
        }
    }
}




