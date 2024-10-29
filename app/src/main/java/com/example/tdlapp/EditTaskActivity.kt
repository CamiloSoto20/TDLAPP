package com.example.tdlapp



import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tdlapp.data.DatabaseHelper

class EditTaskActivity : AppCompatActivity() {

    private lateinit var taskName: EditText
    private lateinit var taskDescription: EditText
    private lateinit var taskDueDate: EditText
    private lateinit var saveEditTaskButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edtitask_activity)

        dbHelper = DatabaseHelper(this)

        taskId = intent.getIntExtra("TASK_ID", -1)
        val taskNameStr = intent.getStringExtra("TASK_NAME")
        val taskDescriptionStr = intent.getStringExtra("TASK_DESCRIPTION")
        val taskDueDateStr = intent.getStringExtra("TASK_DUE_DATE")

        taskName = findViewById(R.id.editTaskName)
        taskDescription = findViewById(R.id.editTaskDescription)
        taskDueDate = findViewById(R.id.editTaskDueDate)
        saveEditTaskButton = findViewById(R.id.saveEditTaskButton)

        taskName.setText(taskNameStr)
        taskDescription.setText(taskDescriptionStr)
        taskDueDate.setText(taskDueDateStr)

        saveEditTaskButton.setOnClickListener {
            val name = taskName.text.toString().trim()
            val description = taskDescription.text.toString().trim()
            val dueDate = taskDueDate.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase
            db.execSQL("UPDATE ${DatabaseHelper.TABLE_TASKS} SET ${DatabaseHelper.COLUMN_TASK_NAME} = ?, ${DatabaseHelper.COLUMN_TASK_DESCRIPTION} = ?, ${DatabaseHelper.COLUMN_TASK_DUE_DATE} = ? WHERE ${DatabaseHelper.COLUMN_TASK_ID} = ?", arrayOf(name, description, dueDate, taskId.toString()))
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
