package com.example.todo_list_apps

import androidx.room.Entity
import java.util.Date

@Entity
data class ToDo(
    val id: Int = 0,
    val title: String,
    val createdAt: Date,
    val documentId: String = "",
    val isCompleted: Boolean = false,
    val imageUri: String? = null // New field to store image URI
)

