package com.example.tdlapp.tareas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tdlapp.R
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.data.Task
import com.example.tdlapp.ui.theme.AppTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddTaskActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference

        setContent {
            AppTheme {
                AddTaskScreen(dbHelper, database) { resultIntent ->
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    dbHelper: DatabaseHelper,
    database: DatabaseReference,
    onSave: (Intent) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // Obtener la fecha y hora actuales para los diálogos
    val calendar = Calendar.getInstance()

    // Diálogo para seleccionar la fecha
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dueDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Diálogo para seleccionar la hora
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            dueTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // Formato de 24 horas
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable._f2c70854c2f350fd16c8261e3553f03), // Reemplaza con tu imagen de fondo
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Añadir Tarea",
                style = MaterialTheme.typography.headlineSmall,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Tarea") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )

            // Campo para la fecha de entrega con un icono de calendario
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Fecha de Entrega") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )

            // Campo para la hora de entrega con un icono de reloj
            OutlinedTextField(
                value = dueTime,
                onValueChange = { dueTime = it },
                label = { Text("Hora de Entrega") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(Icons.Default.Add, contentDescription = "Seleccionar Hora")
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty() && dueDate.isNotEmpty() && dueTime.isNotEmpty()) {
                        // Guardar en SQLite
                        val db = dbHelper.writableDatabase
                        db.execSQL(
                            "INSERT INTO ${DatabaseHelper.TABLE_TASKS} (${DatabaseHelper.COLUMN_TASK_NAME}, ${DatabaseHelper.COLUMN_TASK_DESCRIPTION}, ${DatabaseHelper.COLUMN_TASK_DUE_DATE}, ${DatabaseHelper.COLUMN_TASK_DUE_TIME}, ${DatabaseHelper.COLUMN_TASK_COMPLETED}) VALUES (?, ?, ?, ?, ?)",
                            arrayOf(name, description, dueDate, dueTime, 0)
                        )
                        db.close()

                        // Obtener el último ID utilizado de Firebase
                        database.child("last_task_id").get().addOnSuccessListener { snapshot ->
                            var lastId = snapshot.getValue(Long::class.java) ?: 0
                            lastId++
                            val taskId = lastId.toString()

                            // Guardar la tarea en Firebase con el nuevo ID
                            val task = Task(taskId, name, description, dueDate, dueTime, false)
                            database.child("tasks").child(taskId).setValue(task)
                                .addOnSuccessListener {
                                    // Actualizar el último ID utilizado en Firebase
                                    database.child("last_task_id").setValue(lastId)
                                    Toast.makeText(context, "Tarea Agregada en Firebase", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show()
                                }
                        }

                        Toast.makeText(context, "Tarea Agregada", Toast.LENGTH_SHORT).show()
                        val resultIntent = Intent().apply {
                            putExtra("TASK_NAME", name)
                            putExtra("TASK_DESCRIPTION", description)
                            putExtra("TASK_DUE_DATE", dueDate)
                            putExtra("TASK_DUE_TIME", dueTime)
                        }
                        onSave(resultIntent)
                    } else {
                        Toast.makeText(context, "Rellena los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Tarea", color = Color.White)
            }
        }
    }
}

