package com.example.todo_list_apps

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.util.Date

data class ToDo(
    var id: Int,
    var title: String,
    var createdAt: Date
)


