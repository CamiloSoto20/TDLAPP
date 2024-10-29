package com.example.tdlapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.data.Task

class MainActivity : AppCompatActivity() {

    private lateinit var todoListView: ListView
    private lateinit var addTaskButton: Button
    private lateinit var taskListAdapter: TaskListAdapter
    private val taskList: MutableList<Task> = mutableListOf()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica si el usuario está autenticado
        val db = DatabaseHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USERS}", null)
        if (cursor.count == 0) {
            startActivity(Intent(this, LoginActivity::class.java))
            cursor.close()
            finish() // Cierra la actividad actual si el usuario no está autenticado
            return
        }
        cursor.close()

        setContentView(R.layout.lista_activity)
        dbHelper = DatabaseHelper(this)
        todoListView = findViewById(R.id.todoListView)
        addTaskButton = findViewById(R.id.addTaskButton)
        taskListAdapter = TaskListAdapter(this, taskList)
        todoListView.adapter = taskListAdapter

        addTaskButton.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        loadTasks()
    }

    private fun loadTasks() {
        taskList.clear()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_TASKS, null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_NAME))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DESCRIPTION))
            val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DUE_DATE))
            val completed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_COMPLETED)) > 0

            val task = Task(id, name, description, dueDate, completed)
            taskList.add(task)
        }
        cursor.close()
        taskListAdapter.notifyDataSetChanged()
    }
}



