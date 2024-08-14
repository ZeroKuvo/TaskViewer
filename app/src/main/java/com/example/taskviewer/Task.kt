package com.example.taskviewer

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class Task(
    var title: String = "",
    var detail: String = "",
    var description: String = "",
    var isCompleted: Boolean = false,
    var id: String = UUID.randomUUID().toString())

