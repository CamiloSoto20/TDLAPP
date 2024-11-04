package com.example.tdlapp



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
import com.example.tdlapp.ui.theme.AppTheme

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
            AppTheme {
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
}

@OptIn(ExperimentalMaterial3Api::class)
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
            label = { Text("Descripcion") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
            )
        )
        OutlinedTextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Fecha De Entrega") },
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
                        "UPDATE ${DatabaseHelper.TABLE_TASKS} SET ${DatabaseHelper.COLUMN_TASK_NAME} = ?, ${DatabaseHelper.COLUMN_TASK_DESCRIPTION} = ?, ${DatabaseHelper.COLUMN_TASK_DUE_DATE} = ? WHERE ${DatabaseHelper.COLUMN_TASK_ID} = ?",
                        arrayOf(name, description, dueDate, taskId.toString())
                    )
                    db.close()
                    Toast.makeText(context, "Tarea Actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Rellena Los Cuadros", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Guardar Editado", color = Color.White)
        }
    }
}

