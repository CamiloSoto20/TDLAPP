package com.example.tdlapp


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.tdlapp.data.DatabaseHelper
import com.example.tdlapp.data.Task

class TaskListAdapter(private val context: Context, private val taskList: MutableList<Task>) : BaseAdapter() {

    override fun getCount(): Int {
        return taskList.size
    }

    override fun getItem(position: Int): Any {
        return taskList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)

        val task = taskList[position]
        val taskName: TextView = view.findViewById(R.id.taskName)
        val taskDescription: TextView = view.findViewById(R.id.taskDescription)
        val taskDueDate: TextView = view.findViewById(R.id.taskDueDate)
        val taskCompleted: CheckBox = view.findViewById(R.id.taskCompleted)

        taskName.text = task.name
        taskDescription.text = task.description
        taskDueDate.text = task.dueDate
        taskCompleted.isChecked = task.completed

        view.setOnClickListener {
            // Código para editar la tarea
            val editIntent = Intent(context, EditTaskActivity::class.java)
            editIntent.putExtra("TASK_ID", task.id)
            editIntent.putExtra("TASK_NAME", task.name)
            editIntent.putExtra("TASK_DESCRIPTION", task.description)
            editIntent.putExtra("TASK_DUE_DATE", task.dueDate)
            context.startActivity(editIntent)
        }

        view.setOnLongClickListener {
            // Código para eliminar la tarea
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Task")
            builder.setMessage("Are you sure you want to delete this task?")
            builder.setPositiveButton("Yes") { _, _ ->
                val dbHelper = DatabaseHelper(context)
                val db = dbHelper.writableDatabase
                db.delete(DatabaseHelper.TABLE_TASKS, "${DatabaseHelper.COLUMN_TASK_ID}=?", arrayOf(task.id.toString()))
                taskList.removeAt(position)
                notifyDataSetChanged()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
            true
        }

        return view
    }
}
