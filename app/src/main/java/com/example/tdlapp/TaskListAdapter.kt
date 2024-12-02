package com.example.tdlapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.Task

@Composable
fun TaskListScreen(
    taskList: List<Task>,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onToggleCompleted: (Task, Boolean) -> Unit,
    onAddTask: () -> Unit
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
                TaskItem(task, onEditTask, onDeleteTask, onToggleCompleted)
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onToggleCompleted: (Task, Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)) // Añadir borde con esquinas redondeadas
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)) // Fondo con forma redondeada
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onEditTask(task) }
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge,
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Fecha De Entega: ${task.dueDate} - Hora: ${task.dueTime}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
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
}
