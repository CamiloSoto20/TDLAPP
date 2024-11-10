package com.example.tdlapp.data

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val dueDate: String,
    val dueTime: String, // Nuevo campo para la hora de entrega
    var completed: Boolean
)

