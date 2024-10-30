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
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.DatabaseHelper

class EditTaskActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        taskId = intent.getIntExtra("TASK_ID", -1)
        val taskNameStr = intent.getStringExtra("TASK_NAME") ?: ""
        val taskDescriptionStr = intent.getStringExtra("TASK_DESCRIPTION") ?: ""
        val taskDueDateStr = intent.getStringExtra("TASK_DUE_DATE") ?: ""

        setContent {
            EditTaskScreen(
                dbHelper = dbHelper,
                taskId = taskId,
                taskNameStr = taskNameStr,
                taskDescriptionStr = taskDescriptionStr,
                taskDueDateStr = taskDueDateStr
            )
        }
    }
}

@Composable
fun EditTaskScreen(
    dbHelper: DatabaseHelper,
    taskId: Int?,
    taskNameStr: String,
    taskDescriptionStr: String,
    taskDueDateStr: String
) {
    var name by remember { mutableStateOf(taskNameStr) }
    var description by remember { mutableStateOf(taskDescriptionStr) }
    var dueDate by remember { mutableStateOf(taskDueDateStr) }
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
                    "UPDATE ${DatabaseHelper.TABLE_TASKS} SET ${DatabaseHelper.COLUMN_TASK_NAME} = ?, ${DatabaseHelper.COLUMN_TASK_DESCRIPTION} = ?, ${DatabaseHelper.COLUMN_TASK_DUE_DATE} = ? WHERE ${DatabaseHelper.COLUMN_TASK_ID} = ?",
                    arrayOf(name, description, dueDate, taskId.toString())
                )
                db.close()
                Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Save Task")
        }
    }
}
