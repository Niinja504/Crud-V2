package RecyclerViewHelpers

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import bryan.miranda.crudbryan1b.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.dataClassMusica
import java.util.UUID

class Adaptador(var Datos: List<dataClassMusica>): RecyclerView.Adapter<ViewHolder>() {

    //Funcion para que cuando agregue datos
    // se actualice la lista automaticamente
    fun actualizarListado(nuevaLista: List<dataClassMusica>){
        Datos = nuevaLista
        notifyDataSetChanged()
    }
    ///////////Funcion todo: Eliminar datos
    fun eliminarDatos(nombreCancion: String, posicion: Int){
        //Eliminarlo de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Eliminarlo de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga
            //un PrepareStatement
            val deleteCancion = objConexion?.prepareStatement("delete tbMusica where nombreCancion = ?")!!
            deleteCancion.setString(1, nombreCancion)
            deleteCancion.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun actualizarDato(nombreCancion: String, uuid: String){
        GlobalScope.launch(Dispatchers.IO) {
            //1- Creo un obj de la clase conexion
            val ObjConexion = ClaseConexion().cadenaConexion()

            //2-Creo una variable que contenga un PrepareStatement
            val UpdateCancion = ObjConexion?.prepareStatement("update tbmusica set  nombreCancion = ? where uuid = ?")!!
        }
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Conectar el RecyclerView con la Card
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Poder darle clic a la elemento de la card
        val item = Datos[position]
        holder.txtNombre.text = item.nombreCancion

        //Todo: clic al icono de borrar
        holder.imgBorrar.setOnClickListener {
            //Creo la alerta para confirmar la eliminacion
            //1) Invoco el contexto

            val context = holder.itemView.context


            //2)Creo la alerta en blanco
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                eliminarDatos(item.nombreCancion, position)
            }

            builder.setNegativeButton("No"){dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        //TODO: ClIC AL ICONO DE EDITAR
        holder.imgEditar.setOnClickListener {
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")

            val cuadroTexto = EditText(context)
            cuadroTexto.setHint(item.nombreCancion)
            builder.setView(cuadroTexto)

            //Botones
            builder.setPositiveButton("Actualizar"){
                dialog, wich ->
                actualizarDato(cuadroTexto.text.toString(),item.uuid)
            }

            builder.setNegativeButton("Cancelar"){
                dialog, wich ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}