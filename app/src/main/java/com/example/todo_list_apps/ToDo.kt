package com.example.todo_list_apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Date


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


@Entity
// Updated ToDo data class to include Firestore document ID
data class ToDo(
    val id: Int = 0,
    val title: String,
    val createdAt: Date,
    val documentId: String = "" // Add this to store Firestore document ID
)


//
//data class ToDo(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//    val title: String,
//    val createdAt: Date
//)


