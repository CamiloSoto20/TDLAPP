package com.example.tdlapp.data

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val dueDate: String,
    var completed: Boolean
)
