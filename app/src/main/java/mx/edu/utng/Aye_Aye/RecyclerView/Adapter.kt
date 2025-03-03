package mx.edu.utng.aye_ayeabts.RecyclerView

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import mx.edu.utng.aye_ayeabts.R
import mx.edu.utng.aye_ayeabts.TareaActivity
import android.util.Pair
import android.view.Gravity
import mx.edu.utng.aye_ayeabts.HomeModelClass

//TRES PARAMETROS
class TareaAdapter(
    private val context: Context,
    private var tareas: List<HomeModelClass>, // Cambié a var para poder modificar la lista
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tarea_custom, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.bind(tarea)
    }

    override fun getItemCount(): Int = tareas.size

    // Método para actualizar las tareas
    fun updateTareas(newTareas: List<HomeModelClass>) {
        tareas = newTareas
        notifyDataSetChanged() // Notifica que los datos han cambiado
    }

    inner class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nombreTarea: TextView = itemView.findViewById(R.id.txtNombreTarea)
        private val descripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        private val type: Chip = itemView.findViewById(R.id.chipTipo)

        fun bind(tarea: HomeModelClass) {
            nombreTarea.text = tarea.nombreProducto
            descripcion.text = tarea.descripcion
            type.text = tarea.type

            itemView.setOnClickListener { view ->
                val intent = Intent(context, TareaActivity::class.java)
                intent.putExtra("TAREA_ID", tarea.tareaId)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Configurar la transición de salida en la actividad actual
                    val slide = Slide()
                    slide.slideEdge = Gravity.START // Puede ser START, TOP, BOTTOM
                    slide.duration = 500
                    (context as Activity).window.exitTransition = slide

                    // Configurar la transición de entrada en la actividad destino (en `TareaActivity`)
                    val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity)
                    context.startActivity(intent, options.toBundle())
                } else {
                    context.startActivity(intent)
                }
            }

        }
    }
}