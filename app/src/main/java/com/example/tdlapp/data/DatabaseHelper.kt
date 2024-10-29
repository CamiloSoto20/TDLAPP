package com.example.tdlapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TodoList.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"

        const val TABLE_TASKS = "tasks"
        const val COLUMN_TASK_ID = "id"
        const val COLUMN_TASK_NAME = "name"
        const val COLUMN_TASK_DESCRIPTION = "description"
        const val COLUMN_TASK_DUE_DATE = "due_date"
        const val COLUMN_TASK_COMPLETED = "completed"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "Creating tables")
        val createUsersTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER_EMAIL TEXT, " +
                "$COLUMN_USER_PASSWORD TEXT)"
        db.execSQL(createUsersTable)

        val createTasksTable = "CREATE TABLE $TABLE_TASKS (" +
                "$COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TASK_NAME TEXT, " +
                "$COLUMN_TASK_DESCRIPTION TEXT, " +
                "$COLUMN_TASK_DUE_DATE TEXT, " +
                "$COLUMN_TASK_COMPLETED INTEGER)"
        db.execSQL(createTasksTable)
        Log.d("DatabaseHelper", "Tables created successfully")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }
}

