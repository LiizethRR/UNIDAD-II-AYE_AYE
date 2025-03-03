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
            import android.widget.EditText
            import android.widget.ImageView
            import android.widget.LinearLayout
            import android.widget.Spinner
            import android.widget.TextView
            import android.widget.Toast
            import androidx.activity.enableEdgeToEdge
            import androidx.appcompat.app.AlertDialog
            import androidx.appcompat.app.AppCompatActivity
            import androidx.appcompat.widget.Toolbar
            import androidx.recyclerview.widget.LinearLayoutManager
            import androidx.recyclerview.widget.RecyclerView
            import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
            import com.google.android.material.floatingactionbutton.FloatingActionButton
            import mx.edu.utng.aye_ayeabts.RecyclerView.TareaAdapter

            class TareasActivity : AppCompatActivity() {
                private lateinit var swipeRefreshLayout: SwipeRefreshLayout
                private lateinit var adapter: TareaAdapter
                private lateinit var recyclerView: RecyclerView

                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    enableEdgeToEdge()
                    setContentView(R.layout.activity_main)

                    val toolbar = findViewById<Toolbar>(R.id.toolbar)
                    setSupportActionBar(toolbar)

                    recyclerView = findViewById(R.id.recyclerView)
                    recyclerView.layoutManager = LinearLayoutManager(this)

                    adapter = TareaAdapter(this, mutableListOf())
                    recyclerView.adapter = adapter

                    viewRecord()

                    val fabAdd = findViewById<FloatingActionButton>(R.id.fab)
                    fabAdd.setOnClickListener {
                        fabAdd.animate()
                            .scaleX(0.8f)
                            .scaleY(0.8f)
                            .setDuration(150)
                            .withEndAction {
                                fabAdd.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .start()
                                showAddTaskDialog()
                            }
                            .start()
                    }

                    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
                    swipeRefreshLayout.setOnRefreshListener {
                        refreshData()
                    }
                }

                fun refreshData() {
                    val databaseHandler = DatabaseHandler(this)
                    val tareas: List<HomeModelClass> = databaseHandler.viewTareas()

                    if (tareas.isEmpty()) {
                        Toast.makeText(this, "No hay tareas disponibles", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.updateTareas(tareas)
                    }

                    swipeRefreshLayout.isRefreshing = false
                }

                fun viewRecord() {
                    val databaseHandler = DatabaseHandler(this)
                    val tareas: List<HomeModelClass> = databaseHandler.viewTareas()

                    if (tareas.isEmpty()) {
                        Toast.makeText(this, "No hay tareas disponibles", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.updateTareas(tareas)
                    }
                }

                override fun onCreateOptionsMenu(menu: Menu?): Boolean {
                    menuInflater.inflate(R.menu.menu_main, menu)
                    return true
                }

                override fun onOptionsItemSelected(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.itmCuestionario -> {
                            Toast.makeText(this, "Tareas", Toast.LENGTH_LONG).show()
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

                fun showAddTaskDialog() {
                    val dialogBuilder = AlertDialog.Builder(this)
                    val inflater = this.layoutInflater
                    val dialogView = inflater.inflate(R.layout.crear_activity, null)
                    dialogBuilder.setView(dialogView)

                    val edtName = dialogView.findViewById<EditText>(R.id.u_name)
                    val edtDescripcion = dialogView.findViewById<EditText>(R.id.etxDescripcion)
                    val etdNotas = dialogView.findViewById<EditText>(R.id.etxNotas)

                    val spinnerTipo: Spinner = dialogView.findViewById(R.id.spinnerTipo)
                    val opciones = arrayOf("Lacteas", "Higiene", "Snacks", "Otro")
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
                    spinnerTipo.adapter = adapter

                    val titleLayout = LinearLayout(this)
                    titleLayout.orientation = LinearLayout.HORIZONTAL
                    titleLayout.setBackgroundColor(Color.parseColor("#DD0F2D6B"))
                    titleLayout.setPadding(30, 20, 30, 20)
                    titleLayout.gravity = Gravity.CENTER_VERTICAL

                    val titleView = TextView(this)
                    titleView.text = "Agregar Nuevo Producto"
                    titleView.setTextColor(Color.WHITE)
                    titleView.textSize = 20f
                    titleView.setTypeface(null, Typeface.BOLD)
                    titleView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)


                    val iconSize = 150
                    val iconParams = LinearLayout.LayoutParams(iconSize, iconSize)
                    iconParams.setMargins(10, 0, 0, 0)


                    titleLayout.addView(titleView)


                    dialogBuilder.setCustomTitle(titleLayout)


                    dialogBuilder.setPositiveButton("Guardar") { _, _ ->
                        val name = edtName.text.toString()
                        val descripcion = edtDescripcion.text.toString()
                        val notas = etdNotas.text.toString()
                        val tipo = spinnerTipo.selectedItem.toString()

                        if (name.isNotEmpty() && descripcion.isNotEmpty()) {
                            val databaseHandler = DatabaseHandler(this)
                            val status = databaseHandler.addTareas(
                                HomeModelClass(
                                    0,
                                    name,
                                    descripcion,
                                    tipo,
                                    notas
                                )
                            )

                            if (status > -1) {
                                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                                viewRecord()
                            } else {
                                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                        }
                    }
                        .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                    val dialog = dialogBuilder.create()
                    dialog.show()
                }
            }
