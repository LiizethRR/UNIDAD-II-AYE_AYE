package mx.edu.utng.aye_ayeabts

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.util.Log
import mx.edu.utng.aye_ayeabts.HomeModelClass

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "atbs"
        private const val TABLE_TAREAS = "Productos"
        private const val KEY_ID = "id"
        private const val KEY_NOMBRETAREA = "nombreProducto"
        private const val KEY_DESCRIPCION = "descripcion"
        private const val KEY_TYPE = "type"
        private const val KEY_NOTAS = "Precio"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_TAREAS = """
            CREATE TABLE $TABLE_TAREAS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NOMBRETAREA TEXT NOT NULL,
                $KEY_DESCRIPCION TEXT NOT NULL,
                $KEY_TYPE TEXT NOT NULL,
                $KEY_NOTAS TEXT NOT NULL
            )
        """
        db?.execSQL(CREATE_TABLE_TAREAS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_TAREAS")
            onCreate(db)
        }
    }

    // Método para insertar datos
    fun addTareas(emp: HomeModelClass): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_NOMBRETAREA, emp.nombreProducto)
            put(KEY_DESCRIPCION, emp.descripcion)
            put(KEY_TYPE, emp.type)
            put(KEY_NOTAS, emp.precio)
        }

        Log.d("DatabaseHandler", "Insertando tarea: nombreTarea=${emp.nombreProducto}, descripcion=${emp.descripcion}, type=${emp.type}, notas=${emp.precio}")

        // Insertar fila
        val success = db.insert(TABLE_TAREAS, null, contentValues)
        db.close() // Cerrar la conexión a la base de datos

        if (success == -1L) {
            Log.e("DatabaseHandler", "Error al insertar tarea")
        } else {
            Log.d("DatabaseHandler", "Tarea insertada con éxito, ID=$success")
        }

        return success
    }


    // Método para leer datos
    @SuppressLint("Range")
    fun viewTareas(): List<HomeModelClass> {
        val empList = mutableListOf<HomeModelClass>()
        val selectQuery = "SELECT * FROM $TABLE_TAREAS"
        val db = this.readableDatabase

        db.rawQuery(selectQuery, null).use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val tarea = HomeModelClass(
                        tareaId = cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        nombreProducto = cursor.getString(cursor.getColumnIndex(KEY_NOMBRETAREA)),
                        descripcion = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPCION)),
                        type = cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                        precio = cursor.getString(cursor.getColumnIndex(KEY_NOTAS))
                    )
                    empList.add(tarea)
                } while (cursor.moveToNext())
            }
        }
        db.close() // Cerrar la base de datos después de usarla
        return empList
    }

    // Método para actualizar datos
    fun updateTarea(emp: HomeModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_NOMBRETAREA, emp.nombreProducto)
            put(KEY_DESCRIPCION, emp.descripcion)
            put(KEY_TYPE, emp.type)
            put(KEY_NOTAS, emp.precio)
        }

        // Actualizar fila
        val success = db.update(TABLE_TAREAS, contentValues, "$KEY_ID = ?", arrayOf(emp.tareaId.toString()))
        db.close() // Cerrar la base de datos después de usarla
        return success
    }

    // Método para eliminar datos
    fun deleteTarea(tareaId: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_TAREAS, "$KEY_ID = ?", arrayOf(tareaId.toString()))
        db.close() // Cerrar la base de datos después de usarla
        return success
    }

    // Método para obtener una tarea por su ID
    fun getTareaById(id: Int): HomeModelClass? {
        val db = this.readableDatabase
        var tarea: HomeModelClass? = null
        val selectQuery = "SELECT * FROM $TABLE_TAREAS WHERE $KEY_ID = ?"

        db.rawQuery(selectQuery, arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                tarea = HomeModelClass(
                    tareaId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    nombreProducto = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOMBRETAREA)),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPCION)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                    precio = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTAS))
                )
            } else {
                Log.w("DatabaseWarning", "Tarea no encontrada con ID: $id")
            }
        }

        db.close()
        return tarea
    }
}
