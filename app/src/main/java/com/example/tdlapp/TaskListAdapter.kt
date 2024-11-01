package com.example.tdlapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(taskList) { task ->
                TaskItem(task, onEditTask, onDeleteTask, onToggleCompleted)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onEditTask(task) }
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(text = task.name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
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
        Text(text = task.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        Text(text = task.dueDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground)
        TextButton(onClick = { showDialog = true }) {
            Text("Eliminar", color = Color.Red)
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Eliminar Tarea", color = MaterialTheme.colorScheme.onBackground) },
                text = { Text("Estas seguro de Eliminar la tarea?", color = MaterialTheme.colorScheme.onBackground) },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteTask(task)
                        showDialog = false
                    }) {
                        Text("Si", color = MaterialTheme.colorScheme.onSurface) // Texto en negro
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No", color = MaterialTheme.colorScheme.onSurface) // Texto en negro
                    }
                }
            )
        }
    }
}




