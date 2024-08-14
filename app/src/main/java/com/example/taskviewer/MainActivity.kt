package com.example.taskviewer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var taskList: MutableList<Task>
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskList = mutableListOf()

        //Inicializar firebase firestore
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)

        taskAdapter = TaskAdapter(this, taskList) { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TITLE_KEY", task.title)
            intent.putExtra("DETAIL_KEY", task.detail)
            intent.putExtra("DESCRIPTION_KEY", task.description)
            intent.putExtra("IS_COMPLETED_KEY", task.isCompleted)
            intent.putExtra("ID_KEY", task.id)
            startActivity(intent)
        }
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            showAddTaskDialog()
        }

        loadTasksFromFirebase()
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.inputTitle)
        val detailInput = dialogView.findViewById<EditText>(R.id.inputDetail)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.inputDescription)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Nueva Tarea")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val title = titleInput.text.toString()
                val detail = detailInput.text.toString()
                val description = descriptionInput.text.toString()

                if (title.isNotEmpty() && detail.isNotEmpty() && description.isNotEmpty()) {
                    val newTask = Task(title, detail, description, false)
                    addTaskToFirebase(newTask)
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun addTaskToFirebase(task: Task) {
        db.collection("tasks")
            .document(task.id)
            .set(task)
            .addOnSuccessListener {
                taskList.add(task)
                taskAdapter.notifyItemInserted(taskList.size - 1)
                Toast.makeText(this, "Tarea agregada con éxito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTasksFromFirebase() {
        db.collection("tasks")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val task = document.toObject(Task::class.java)
                    taskList.add(task)
                }
                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar las tareas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
