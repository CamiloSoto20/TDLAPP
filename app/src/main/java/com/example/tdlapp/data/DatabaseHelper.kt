package com.example.tdlapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TodoList.db"
        private const val DATABASE_VERSION = 4 // Incrementamos la versión a 4

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_USERNAME = "username"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"

        const val TABLE_TASKS = "tasks"
        const val COLUMN_TASK_ID = "id"
        const val COLUMN_TASK_NAME = "name"
        const val COLUMN_TASK_DESCRIPTION = "description"
        const val COLUMN_TASK_DUE_DATE = "due_date"
        const val COLUMN_TASK_DUE_TIME = "due_time"
        const val COLUMN_TASK_COMPLETED = "completed"

        // Nuevas tablas para roles
        const val TABLE_ROLES = "roles"
        const val COLUMN_ROLE_ID = "role_id"
        const val COLUMN_ROLE_NAME = "role_name"

        const val TABLE_USER_ROLES = "user_roles"
        const val COLUMN_USER_ROLE_ID = "user_role_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "Creating tables")

        val createUsersTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER_USERNAME TEXT, " +
                "$COLUMN_USER_EMAIL TEXT, " +
                "$COLUMN_USER_PASSWORD TEXT)"
        db.execSQL(createUsersTable)

        val createTasksTable = "CREATE TABLE $TABLE_TASKS (" +
                "$COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TASK_NAME TEXT, " +
                "$COLUMN_TASK_DESCRIPTION TEXT, " +
                "$COLUMN_TASK_DUE_DATE TEXT, " +
                "$COLUMN_TASK_DUE_TIME TEXT, " +
                "$COLUMN_TASK_COMPLETED INTEGER)"
        db.execSQL(createTasksTable)

        // Crear la tabla de roles
        val createRolesTable = "CREATE TABLE $TABLE_ROLES (" +
                "$COLUMN_ROLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ROLE_NAME TEXT)"
        db.execSQL(createRolesTable)

        // Crear la tabla de roles de usuarios
        val createUserRolesTable = "CREATE TABLE $TABLE_USER_ROLES (" +
                "$COLUMN_USER_ROLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER_ID INTEGER, " +
                "$COLUMN_ROLE_ID INTEGER, " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID), " +
                "FOREIGN KEY($COLUMN_ROLE_ID) REFERENCES $TABLE_ROLES($COLUMN_ROLE_ID))"
        db.execSQL(createUserRolesTable)

        // Insertar roles predefinidos
        db.execSQL("INSERT INTO $TABLE_ROLES ($COLUMN_ROLE_NAME) VALUES ('Administrador'), ('Usuario')")

        Log.d("DatabaseHelper", "Tables created successfully")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion")

        if (oldVersion < 2) {
            // Agregar columna due_time en tasks si actualizamos desde una versión anterior a la 2
            db.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $COLUMN_TASK_DUE_TIME TEXT")
            Log.d("DatabaseHelper", "Column $COLUMN_TASK_DUE_TIME added to $TABLE_TASKS")
        }

        if (oldVersion < 3) {
            // Agregar columna username en users si actualizamos desde una versión anterior a la 3
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_USER_USERNAME TEXT")
            Log.d("DatabaseHelper", "Column $COLUMN_USER_USERNAME added to $TABLE_USERS")
        }

        if (oldVersion < 4) {
            // Crear las nuevas tablas para roles y roles de usuarios si actualizamos desde una versión anterior a la 4
            val createRolesTable = "CREATE TABLE $TABLE_ROLES (" +
                    "$COLUMN_ROLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_ROLE_NAME TEXT)"
            db.execSQL(createRolesTable)

            val createUserRolesTable = "CREATE TABLE $TABLE_USER_ROLES (" +
                    "$COLUMN_USER_ROLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_USER_ID INTEGER, " +
                    "$COLUMN_ROLE_ID INTEGER, " +
                    "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID), " +
                    "FOREIGN KEY($COLUMN_ROLE_ID) REFERENCES $TABLE_ROLES($COLUMN_ROLE_ID))"
            db.execSQL(createUserRolesTable)

            // Insertar roles predefinidos
            db.execSQL("INSERT INTO $TABLE_ROLES ($COLUMN_ROLE_NAME) VALUES ('Administrador'), ('Usuario')")

            Log.d("DatabaseHelper", "Tables $TABLE_ROLES and $TABLE_USER_ROLES created successfully")
        }
    }

    fun getUserData(userId: Int): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS, arrayOf(COLUMN_USER_USERNAME, COLUMN_USER_EMAIL),
            "$COLUMN_USER_ID=?", arrayOf(userId.toString()),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL))
            cursor.close()
            User(userId, userName, userEmail)
        } else {
            cursor.close()
            null
        }
    }

    // Función para asignar un rol a un usuario
    fun assignRoleToUser(userId: Int, roleId: Int) {
        val db = writableDatabase
        db.execSQL(
            "INSERT INTO $TABLE_USER_ROLES ($COLUMN_USER_ID, $COLUMN_ROLE_ID) VALUES (?, ?)",
            arrayOf(userId, roleId)
        )
        db.close()
    }

    // Función para obtener el rol de un usuario
    fun getUserRole(userId: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ROLE_NAME FROM $TABLE_ROLES INNER JOIN $TABLE_USER_ROLES ON $TABLE_ROLES.$COLUMN_ROLE_ID = $TABLE_USER_ROLES.$COLUMN_ROLE_ID WHERE $TABLE_USER_ROLES.$COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )
        var role = ""
        if (cursor != null && cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE_NAME))
            cursor.close()
        }
        db.close()
        return role
    }
}

data class User(val id: Int, val username: String, val email: String)





