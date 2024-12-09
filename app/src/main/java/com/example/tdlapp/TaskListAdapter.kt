package com.example.tdlapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskList: List<Task>,
    userRole: String,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onToggleCompleted: (Task, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            items(taskList) { task ->
                TaskItem(
                    task = task,
                    onEditTask = onEditTask,
                    onDeleteTask = onDeleteTask,
                    onToggleCompleted = onToggleCompleted,
                    userRole = userRole
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onToggleCompleted: (Task, Boolean) -> Unit,
    userRole: String
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable {
                if (userRole != "Administrador") {
                    showDetailsDialog = true
                } else {
                    onEditTask(task)
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = task.completed,
                onCheckedChange = { isChecked ->
                    onToggleCompleted(task, isChecked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    uncheckedColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
        Text(
            text = task.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Fecha de Entrega: ${task.dueDate} - Hora: ${task.dueTime}",
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (userRole == "Administrador") {
            TextButton(onClick = { showDialog = true }) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Eliminar Tarea", color = MaterialTheme.colorScheme.onSurface) },
                    text = { Text("¿Estás seguro de que quieres eliminar la tarea?", color = MaterialTheme.colorScheme.onSurface) },
                    confirmButton = {
                        TextButton(onClick = {
                            onDeleteTask(task)
                            showDialog = false
                        }) {
                            Text("Sí", color = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("No", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                )
            }
        }

        if (showDetailsDialog) {
            AlertDialog(
                onDismissRequest = { showDetailsDialog = false },
                title = { Text("Detalles de la Tarea") },
                text = {
                    Column {
                        Text("Nombre: ${task.name}")
                        Text("Descripción: ${task.description}")
                        Text("Fecha de Entrega: ${task.dueDate}")
                        Text("Hora de Entrega: ${task.dueTime}")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDetailsDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

