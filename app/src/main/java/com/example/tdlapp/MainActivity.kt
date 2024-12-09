package com.example.tdlapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tdlapp.Login.LoginActivity
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.data.Task
import com.example.tdlapp.tareas.AddTaskActivity
import com.example.tdlapp.tareas.EditTaskActivity
import com.example.tdlapp.ui.theme.AppTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference

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
    fun MainScreen(userName: String, userEmail: String, userRole: String) {
        val taskList = remember { mutableStateListOf<Task>() }
        val context = LocalContext.current
        val openDialog = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            loadTasks(taskList)
        }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "TDLAPP",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 40.sp, // Ajustar el tamaño del texto
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = { openDialog.value = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.size(50.dp) // Ajustar el tamaño del ícono
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
                    userRole = userRole,
                    onLogout = {
                        openDialog.value = false
                        // Limpiar preferencias compartidas
                        val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            clear()
                            apply()
                        }
                        val loginIntent = Intent(context, LoginActivity::class.java)
                        context.startActivity(loginIntent)
                        (context as ComponentActivity).finish()
                    }
                )

                // Mostrar y gestionar tareas según el rol del usuario
                TaskListScreen(
                    taskList = taskList,
                    userRole = userRole,
                    onEditTask = { task ->
                        if (userRole == "Administrador") {
                            val editIntent = Intent(context, EditTaskActivity::class.java).apply {
                                putExtra("TASK_ID", task.id)
                                putExtra("TASK_NAME", task.name)
                                putExtra("TASK_DESCRIPTION", task.description)
                                putExtra("TASK_DUE_DATE", task.dueDate)
                                putExtra("TASK_DUE_TIME", task.dueTime)
                            }
                            startActivityForResult(editIntent, REQUEST_EDIT_TASK)
                        } else {
                            Toast.makeText(context, "Solo los administradores pueden editar tareas", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDeleteTask = { task ->
                        if (userRole == "Administrador") {
                            database.child("tasks").child(task.id).removeValue()
                            taskList.remove(task)
                        } else {
                            Toast.makeText(context, "Solo los administradores pueden eliminar tareas", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onToggleCompleted = { task, isCompleted ->
                        task.completed = isCompleted
                        database.child("tasks").child(task.id).setValue(task)
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
                if (userRole == "Administrador") {
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
    }

    @Composable
    fun AccountDialog(
        openDialog: Boolean,
        onDismiss: () -> Unit,
        userName: String,
        userEmail: String,
        userRole: String,
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
                        Text("Rol: $userRole")
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

    private fun saveTask(task: Task) {
        // Obtener el último ID utilizado de Firebase
        database.child("last_task_id").get().addOnSuccessListener { snapshot ->
            var lastId = snapshot.getValue(Long::class.java) ?: 0
            lastId++
            val taskId = lastId.toString()

            // Guardar la tarea en Firebase con el nuevo ID
            val taskWithId = task.copy(id = taskId)
            database.child("tasks").child(taskId).setValue(taskWithId)
                .addOnSuccessListener {
                    // Actualizar el último ID utilizado en Firebase
                    database.child("last_task_id").setValue(lastId)
                    Log.d("Firebase", "Tarea guardada exitosamente con ID: $taskId")
                }
                .addOnFailureListener {
                    Log.e("Firebase", "Error al guardar la tarea.", it)
                }
        }
    }

    private fun loadTasks(taskList: MutableList<Task>) {
        database.child("tasks").get().addOnSuccessListener { snapshot ->
            taskList.clear()
            snapshot.children.forEach {
                val task = it.getValue(Task::class.java)
                task?.let { taskList.add(task) }
            }
            Log.d("Firebase", "Tareas recuperadas: ${taskList.size}")
        }.addOnFailureListener {
            Log.e("Firebase", "Error al recuperar las tareas.", it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val taskList = mutableStateListOf<Task>()
            loadTasks(taskList)
            setContent {
                val userName = intent.getStringExtra("USER_NAME") ?: "Nombre no disponible"
                val userEmail = intent.getStringExtra("USER_EMAIL") ?: "Correo no disponible"
                val userRole = intent.getStringExtra("USER_ROLE") ?: "Rol no disponible"

                AppTheme {
                    MainScreen(userName = userName, userEmail = userEmail, userRole = userRole)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_ADD_TASK = 1
        private const val REQUEST_EDIT_TASK = 2
    }
}







