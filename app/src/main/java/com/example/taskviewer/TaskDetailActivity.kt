package com.example.taskviewer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val title = findViewById<TextView>(R.id.taskTitle)
        val detail = findViewById<TextView>(R.id.taskDetail)
        val description = findViewById<TextView>(R.id.taskDescription)
        val btnBack = findViewById<Button>(R.id.btnBack)

        val taskTitle = intent.getStringExtra("TITLE_KEY") ?: ""
        val taskDetail = intent.getStringExtra("DETAIL_KEY") ?: ""
        val taskDescription = intent.getStringExtra("DESCRIPTION_KEY") ?: ""
        val isCompleted = intent.getBooleanExtra("IS_COMPLETED_KEY", false)
        val taskId = intent.getStringExtra("ID_KEY") ?: ""

        // Asigna los valores a los TextViews
        title.text = taskTitle
        detail.text = taskDetail
        description.text = taskDescription

        btnBack.setOnClickListener {
            finish()
        }

    }
}