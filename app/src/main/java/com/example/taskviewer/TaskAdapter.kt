package com.example.taskviewer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class TaskAdapter(private val context: Context,
                  private val taskList: MutableList<Task>,
                  private val onItemClick: (Task) -> Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

                      private val db = FirebaseFirestore.getInstance()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val detail: TextView = itemView.findViewById(R.id.taskDetail)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val completed: CheckBox = itemView.findViewById(R.id.taskCompleted)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = taskList[position]

        holder.title.text = currentTask.title
        holder.detail.text = currentTask.detail
        holder.description.text = currentTask.description
        holder.completed.isChecked = currentTask.isCompleted

        holder.completed.setOnCheckedChangeListener { _, isChecked ->
            currentTask.isCompleted = isChecked
            updateTaskInFirebase(currentTask)
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(context, TaskDetailActivity::class.java)
            intent.putExtra("TITLE_KEY", currentTask.title)
            intent.putExtra("DETAIL_KEY", currentTask.detail)
            intent.putExtra("DESCRIPTION_KEY", currentTask.description)
            intent.putExtra("IS_COMPLETED_KEY", currentTask.isCompleted)
            intent.putExtra("ID_KEY", currentTask.id)
            context.startActivity(intent)
        }

        holder.btnEdit.setOnClickListener() {
            editTask(currentTask, position)
        }

        holder.btnDelete.setOnClickListener() {
            deleteTask(position)
        }
    }

    override fun getItemCount() = taskList.size

    private fun editTask(task: Task, position: Int) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_add_task, null)

        dialogBuilder.setView(dialogView)

        val newTitle = dialogView.findViewById<EditText>(R.id.inputTitle)
        val newDetail = dialogView.findViewById<EditText>(R.id.inputDetail)
        val newDescription = dialogView.findViewById<EditText>(R.id.inputDescription)

        newTitle.setText(task.title)
        newDetail.setText(task.detail)
        newDescription.setText(task.description)

        dialogBuilder.setTitle("Editar Tarea")
        dialogBuilder.setPositiveButton("Guardar") { _, _ ->
            task.title = newTitle.text.toString()
            task.detail = newDetail.text.toString()
            task.description = newDescription.text.toString()

            updateTaskInFirebase(task)
            notifyItemChanged(position)
        }

        dialogBuilder.setNegativeButton("Cancelar", null)
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun deleteTask(position: Int) {
        val currentTask = taskList[position]

        db.collection("tasks")
            .document(currentTask.id)
            .delete()
            .addOnSuccessListener {
                taskList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, taskList.size)
                Toast.makeText(context, "Tarea eliminada con éxito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{ e ->
                Toast.makeText(context, "Error al eliminar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTaskInFirebase(task: Task) {
        db.collection("tasks")
            .document(task.id)
            .set(task)
            .addOnSuccessListener {
                //Actualizacion exitosa
            }
            .addOnFailureListener{ e ->
                Toast.makeText(context, "Error al actualizar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
