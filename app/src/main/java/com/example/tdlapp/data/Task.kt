package com.example.tdlapp.data

data class Task(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val dueDate: String = "",
    val dueTime: String = "",
    var completed: Boolean = false
)
