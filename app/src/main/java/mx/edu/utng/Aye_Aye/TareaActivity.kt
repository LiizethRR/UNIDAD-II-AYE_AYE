package mx.edu.utng.aye_ayeabts

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import mx.edu.utng.aye_ayeabts.R

//  Actualizar y borrar (Ver Productos)
class TareaActivity : AppCompatActivity() {

    private lateinit var txtNombreTarea: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtNotas: TextView
    private lateinit var txtTipo: TextView
    private lateinit var btnEditar: ImageButton
    private lateinit var btnBorrar: ImageButton
    private fun reloadTask(tareaId: Int) {
        val tarea = DatabaseHandler(this).getTareaById(tareaId)
        if (tarea != null) {
            txtNombreTarea.text = tarea.nombreProducto
            txtDescripcion.text = tarea.descripcion
            txtNotas.text = tarea.precio
            txtTipo.text = tarea.type
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializar los TextViews
        txtNombreTarea = findViewById(R.id.txtNombreTarea)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        txtNotas = findViewById(R.id.txtNotas)
        txtTipo = findViewById(R.id.txtTipo)

        // Inicializar los botones
        btnEditar = findViewById(R.id.btnEditar)
        btnBorrar = findViewById(R.id.btnBorrar)

        // Obtener el id de la tarea desde el Intent
        val tareaId = intent.getIntExtra("PRODUCT_ID", -1)

        if (tareaId != -1) {
            // Consultar la tarea con el id
            val tarea = DatabaseHandler(this).getTareaById(tareaId)
            if (tarea != null) {
                // Mostrar la tarea en los TextViews
                txtNombreTarea.text = tarea.nombreProducto
                txtDescripcion.text = tarea.descripcion
                txtNotas.text = tarea.precio
                txtTipo.text = tarea.type
            }

            // Acción del botón "Editar"
            btnEditar.setOnClickListener {
                if (tarea != null) {
                    updateRecord(
                        tareaId,
                        tarea.nombreProducto,
                        tarea.descripcion,
                        tarea.type,
                        tarea.precio
                    )
                }
            }

            // Acción del botón "Borrar"
            btnBorrar.setOnClickListener {
                deleteRecord(tareaId)
            }
        } else {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para actualizar la tarea
    fun updateRecord(id: Int, nombre: String, descripcion: String,
                     type: String, notas: String ) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val edtName = dialogView.findViewById<EditText>(R.id.updateName)
        val edtDescripcion = dialogView.findViewById<EditText>(R.id.updateDescripcion)
        val etdNotas = dialogView.findViewById<EditText>(R.id.etxNotas)

        val spinnerTipo: Spinner = dialogView.findViewById(R.id.spinnerTipo)
        val opciones = arrayOf("Lacteos", "Higiene", "Bebés", "Pepelería")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spinnerTipo.adapter = adapter

        // Llenar los campos con la información actual
        edtName.setText(nombre)
        edtDescripcion.setText(descripcion)
        etdNotas.setText(notas)

        // Crear un LinearLayout para el título con fondo verde fuerte
        val titleLayout = LinearLayout(this)
        titleLayout.orientation = LinearLayout.HORIZONTAL
        titleLayout.setBackgroundColor(Color.parseColor("#0F4AC1")) // Verde fuerte
        titleLayout.setPadding(30, 20, 30, 20)
        titleLayout.gravity = Gravity.CENTER_VERTICAL

        // Crear el título
        val titleView = TextView(this)
        titleView.text = "Actualizar Producto"
        titleView.setTextColor(Color.WHITE)
        titleView.textSize = 20f
        titleView.setTypeface(null, Typeface.BOLD)
        titleView.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)


        val iconSize = 150 // Tamaño del icono en píxeles
        val iconParams = LinearLayout.LayoutParams(iconSize, iconSize)
        iconParams.setMargins(10, 0, 0, 0)

        // Agregar elementos al layout del título
        titleLayout.addView(titleView)

        dialogBuilder.setCustomTitle(titleLayout)

        dialogBuilder.setPositiveButton("Actualizar") { _, _ ->
            val databaseHandler = DatabaseHandler(this)
            val updatedTarea = HomeModelClass(
                id,
                edtName.text.toString(),
                edtDescripcion.text.toString(),
                spinnerTipo.selectedItem.toString(),
                etdNotas.text.toString()
            )

            val status = databaseHandler.updateTarea(updatedTarea)
            if (status > -1) {
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_LONG).show()
                reloadTask(id)
            }
        }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    // Función para eliminar la tarea
    fun deleteRecord(id: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Eliminar Producto")
        dialogBuilder.setMessage("¿Estás seguro de que quieres eliminar este Producto?")
        dialogBuilder.setPositiveButton("Eliminar") { _, _ ->
            val databaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteTarea(id)
            if (status > -1) {
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_LONG).show()

                // Crear un Intent para regresar a la actividad principal (MainActivity)
                val intent = Intent(this, MainActivity::class.java)

                // Asegúrate de no tener duplicados en el stack de actividades al usar FLAG_ACTIVITY_CLEAR_TOP
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                // Inicia la actividad principal
                startActivity(intent)

                // Opcional: Si quieres asegurarte de que la actividad actual se cierre, usa finish()
                finish()
            }
        }
        dialogBuilder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        dialogBuilder.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itmCuestionario -> {
                Toast.makeText(this, "Productos", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.itmSalir -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
