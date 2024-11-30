package com.example.todo_list_apps
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


class ToDoViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _todoList = MutableLiveData<List<ToDo>>()
    val todoList: LiveData<List<ToDo>> = _todoList

    private var listenerRegistration: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            fetchTodos(firebaseAuth.currentUser?.uid)
        }
    }

    private fun fetchTodos(userId: String?) {
        listenerRegistration?.remove()

        if (userId == null) {
            _todoList.value = listOf()
            return
        }

        listenerRegistration = firestore.collection("todos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _todoList.value = listOf()
                    return@addSnapshotListener
                }

                val todos = snapshot?.documents?.mapNotNull { document ->
                    ToDo(
                        id = document.id.hashCode(),
                        title = document.getString("title") ?: "",
                        createdAt = document.getDate("createdAt") ?: Date(),
                        documentId = document.id,
                        isCompleted = document.getBoolean("isCompleted") ?: false
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
                "userId" to user.uid,
                "isCompleted" to false
            )

            viewModelScope.launch(Dispatchers.IO) {
                firestore.collection("todos")
                    .add(todo)
            }
        }
    }

    fun deleteToDo(documentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("todos")
                .document(documentId)
                .delete()
        }
    }

    fun addToDoWithImage(title: String, imageUri: Uri?) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val todo = hashMapOf(
                "title" to title,
                "createdAt" to Date(),
                "userId" to user.uid,
                "isCompleted" to false,
                "imageUri" to imageUri?.toString()
            )

            viewModelScope.launch(Dispatchers.IO) {
                firestore.collection("todos")
                    .add(todo)
            }
        }
    }

    // Update ToDo data class to include optional image
    fun updateToDo(documentId: String, newTitle: String, imageUri: Uri? = null) {
        val updates = mutableMapOf<String, Any>(
            "title" to newTitle
        )

        imageUri?.let {
            updates["imageUri"] = it.toString()
        }

        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("todos")
                .document(documentId)
                .update(updates)
        }
    }

    fun toggleToDoCompletion(documentId: String, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("todos")
                .document(documentId)
                .update("isCompleted", isCompleted)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}