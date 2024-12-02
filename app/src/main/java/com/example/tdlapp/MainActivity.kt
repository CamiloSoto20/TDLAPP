package com.example.tdlapp

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tdlapp.Login.LoginActivity
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.data.Task
import com.example.tdlapp.tareas.AddTaskActivity
import com.example.tdlapp.tareas.EditTaskActivity
import com.example.tdlapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        // Recupera los datos del Intent
        val userName = intent.getStringExtra("USER_NAME") ?: "Nombre no disponible"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: "Correo no disponible"
        val userRole = intent.getStringExtra("USER_ROLE") ?: "Rol no disponible" // Obtener el rol del usuario

        setContent {
            AppTheme {
                MainScreen(userName, userEmail, userRole)
            }
        }
    }

    @Composable
    fun MainScreen(userName: String, userEmail: String, userRole: String) { // Añadir parámetro userRole
        val taskList = remember { mutableStateListOf<Task>() }
        val context = LocalContext.current
        val openDialog = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            loadTasks(taskList)
        }

        // Fondo de imagen borrosa
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable._f2c70854c2f350fd16c8261e3553f03), // Reemplaza con tu imagen
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .blur(10.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "TDLAPP",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = { openDialog.value = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Cuenta")
                    }
                }

                // Cuadro de diálogo para mostrar los datos de la cuenta
                AccountDialog(
                    openDialog = openDialog.value,
                    onDismiss = { openDialog.value = false },
                    userName = userName,
                    userEmail = userEmail,
                    userRole = userRole, // Pasar el rol del usuario al diálogo
                    onLogout = {
                        openDialog.value = false
                        // Limpiar preferencias compartidas
                        val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        with (sharedPreferences.edit()) {
                            clear()
                            apply()
                        }
                        val loginIntent = Intent(context, LoginActivity::class.java)
                        context.startActivity(loginIntent)
                        (context as ComponentActivity).finish()
                    }
                )

                TaskListScreen(
                    taskList = taskList,
                    onEditTask = { task ->
                        val editIntent = Intent(context, EditTaskActivity::class.java).apply {
                            putExtra("TASK_ID", task.id)
                            putExtra("TASK_NAME", task.name)
                            putExtra("TASK_DESCRIPTION", task.description)
                            putExtra("TASK_DUE_DATE", task.dueDate)
                            putExtra("TASK_DUE_TIME", task.dueTime)
                        }
                        startActivityForResult(editIntent, REQUEST_EDIT_TASK)
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
                        val addIntent = Intent(context, AddTaskActivity::class.java)
                        startActivityForResult(addIntent, REQUEST_ADD_TASK)
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
                        val addIntent = Intent(context, AddTaskActivity::class.java)
                        startActivityForResult(addIntent, REQUEST_ADD_TASK)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Agregar Tarea", color = Color.White)
                }
            }
        }
    }

    @Composable
    fun AccountDialog(
        openDialog: Boolean,
        onDismiss: () -> Unit,
        userName: String,
        userEmail: String,
        userRole: String, // Añadir parámetro userRole
        onLogout: () -> Unit
    ) {
        if (openDialog) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Datos de la cuenta") },
                text = {
                    Column {
                        Text("Nombre: $userName")
                        Text("Correo: $userEmail")
                        Text("Rol: $userRole") // Mostrar el rol del usuario
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onLogout) {
                            Text("Cerrar sesión")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }
            )
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
            val dueTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DUE_TIME))
            val completed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_COMPLETED)) > 0

            val task = Task(id, name, description, dueDate, dueTime, completed)
            taskList.add(task)
        }
        cursor.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_ADD_TASK, REQUEST_EDIT_TASK -> {
                    val userName = intent.getStringExtra("USER_NAME") ?: "Nombre no disponible"
                    val userEmail = intent.getStringExtra("USER_EMAIL") ?: "Correo no disponible"
                    val userRole = intent.getStringExtra("USER_ROLE") ?: "Rol no disponible"

                    val taskList = mutableStateListOf<Task>()
                    loadTasks(taskList)
                    setContent {
                        AppTheme {
                            MainScreen(userName = userName, userEmail = userEmail, userRole = userRole) // Pasar userRole
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_ADD_TASK = 1
        private const val REQUEST_EDIT_TASK = 2
    }
}






