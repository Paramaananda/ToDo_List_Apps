package com.example.todo_list_apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date


import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch



class ToDoViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _todoList = MutableLiveData<List<ToDo>>()
    val todoList: LiveData<List<ToDo>> = _todoList

    private var listenerRegistration: ListenerRegistration? = null

    init {
        // Observe auth state changes to fetch todos for the current user
        auth.addAuthStateListener { firebaseAuth ->
            fetchTodos(firebaseAuth.currentUser?.uid)
        }
    }

    private fun fetchTodos(userId: String?) {
        // Cancel previous listener if exists
        listenerRegistration?.remove()

        // If no user is logged in, clear the todo list
        if (userId == null) {
            _todoList.value = listOf()
            return
        }

        // Create a new listener for the specific user's todos
        listenerRegistration = firestore.collection("todos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error (you might want to log this or show a toast)
                    _todoList.value = listOf()
                    return@addSnapshotListener
                }

                val todos = snapshot?.documents?.mapNotNull { document ->
                    ToDo(
                        id = document.id.hashCode(),
                        title = document.getString("title") ?: "",
                        createdAt = document.getDate("createdAt") ?: Date(),
                        documentId = document.id
                    )
                } ?: listOf()

                _todoList.value = todos
            }
    }

    fun addToDo(title: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val todo = hashMapOf(
                "title" to title,
                "createdAt" to Date(),
                "userId" to user.uid
            )

            viewModelScope.launch(Dispatchers.IO) {
                firestore.collection("todos")
                    .add(todo)
                // Snapshot listener will handle updating the list
            }
        }
    }

    fun deleteToDo(documentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("todos")
                .document(documentId)
                .delete()
            // Snapshot listener will handle updating the list
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Unregister the listener when the ViewModel is cleared
        listenerRegistration?.remove()
    }
}