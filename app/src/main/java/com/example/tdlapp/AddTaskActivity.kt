package com.example.tdlapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tdlapp.data.DatabaseHelper

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskName: EditText
    private lateinit var taskDescription: EditText
    private lateinit var taskDueDate: EditText
    private lateinit var saveTaskButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anadirtareas_activity)

        dbHelper = DatabaseHelper(this)
        taskName = findViewById(R.id.taskName)
        taskDescription = findViewById(R.id.taskDescription)
        taskDueDate = findViewById(R.id.taskDueDate)
        saveTaskButton = findViewById(R.id.saveTaskButton)

        saveTaskButton.setOnClickListener {
            val name = taskName.text.toString().trim()
            val description = taskDescription.text.toString().trim()
            val dueDate = taskDueDate.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase
            db.execSQL("INSERT INTO ${DatabaseHelper.TABLE_TASKS} (${DatabaseHelper.COLUMN_TASK_NAME}, ${DatabaseHelper.COLUMN_TASK_DESCRIPTION}, ${DatabaseHelper.COLUMN_TASK_DUE_DATE}, ${DatabaseHelper.COLUMN_TASK_COMPLETED}) VALUES (?, ?, ?, ?)", arrayOf(name, description, dueDate, 0))
            db.close()
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()

            setResult(Activity.RESULT_OK) // Envía un resultado de éxito
            finish() // Cierra la actividad
        }
    }
}


