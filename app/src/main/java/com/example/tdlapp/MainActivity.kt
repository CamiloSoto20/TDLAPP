package com.example.tdlapp

import android.app.Activity
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
        setContentView(R.layout.lista_activity)
        dbHelper = DatabaseHelper(this)
        todoListView = findViewById(R.id.todoListView)
        addTaskButton = findViewById(R.id.addTaskButton)
        taskListAdapter = TaskListAdapter(this, taskList)
        todoListView.adapter = taskListAdapter

        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 1) // Inicia la actividad esperando un resultado
        }

        loadTasks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadTasks() // Recarga las tareas después de agregar una nueva
        }
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



