package com.example.tdlapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.data.Task
import com.example.tdlapp.ui.theme.TDLAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContent {
            TDLAppTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        val taskList = remember { mutableStateListOf<Task>() }
        LaunchedEffect(Unit) {
            loadTasks(taskList)
        }
        val addTaskLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadTasks(taskList)
            }
        }
        val editTaskLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadTasks(taskList)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Ajuste de fondo
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "TDLAPP",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground // Color de texto del tema
                )
                TaskListScreen(
                    taskList = taskList,
                    onEditTask = { task ->
                        val editIntent = Intent(this@MainActivity, EditTaskActivity::class.java).apply {
                            putExtra("TASK_ID", task.id)
                            putExtra("TASK_NAME", task.name)
                            putExtra("TASK_DESCRIPTION", task.description)
                            putExtra("TASK_DUE_DATE", task.dueDate)
                        }
                        editTaskLauncher.launch(editIntent)
                    },
                    onDeleteTask = { task ->
                        dbHelper.writableDatabase.delete(
                            DatabaseHelper.TABLE_TASKS,
                            "${DatabaseHelper.COLUMN_TASK_ID}=?",
                            arrayOf(task.id.toString())
                        )
                        taskList.remove(task)
                    },
                    onAddTask = {
                        val addIntent = Intent(this@MainActivity, AddTaskActivity::class.java)
                        addTaskLauncher.launch(addIntent)
                    },
                    onToggleCompleted = { task, isCompleted ->
                        task.completed = isCompleted
                        dbHelper.writableDatabase.update(
                            DatabaseHelper.TABLE_TASKS,
                            ContentValues().apply {
                                put(DatabaseHelper.COLUMN_TASK_COMPLETED, if (isCompleted) 1 else 0)
                            },
                            "${DatabaseHelper.COLUMN_TASK_ID}=?",
                            arrayOf(task.id.toString())
                        )
                        loadTasks(taskList)
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        val addIntent = Intent(this@MainActivity, AddTaskActivity::class.java)
                        addTaskLauncher.launch(addIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary // Color del botón
                    )
                ) {
                    Text("Añadir Tarea", color = Color.White) // Texto blanco
                }
            }
        }
    }

    private fun loadTasks(taskList: MutableList<Task>) {
        taskList.clear()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_TASKS, null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_NAME))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DESCRIPTION))
            val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DUE_DATE))
            val completed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_COMPLETED)) > 0
            val task = Task(id, name, description, dueDate, completed)
            taskList.add(task)
        }
        cursor.close()
    }
}







