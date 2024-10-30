package com.example.tdlapp


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tdlapp.data.Task

@Composable
fun TaskListScreen(
    taskList: List<Task>,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onAddTask: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onAddTask) {
            Text("Add Task")
        }
        LazyColumn {
            items(taskList) { task ->
                TaskItem(task, onEditTask, onDeleteTask)
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditTask(task) }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = task.name, style = MaterialTheme.typography.bodyLarge)
        Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
        Text(text = task.dueDate, style = MaterialTheme.typography.bodySmall)
        Row {
            Text(text = "Completed: ")
            Checkbox(
                checked = task.completed,
                onCheckedChange = null
            )
        }
        TextButton(onClick = { showDialog = true }) {
            Text("Delete")
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete this task?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteTask(task)
                        showDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
