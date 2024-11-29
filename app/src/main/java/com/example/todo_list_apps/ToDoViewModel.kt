package com.example.todo_list_apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_list_apps.Database.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class ToDoViewModel : ViewModel() {
    val todoDao = MainApplication.todoDatabase.getTodoDao()
    val todoList: LiveData<List<ToDo>> = todoDao.getAllToDo()

    @RequiresApi(Build.VERSION_CODES.O)
    fun addToDo(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.addToDo(ToDo(title = title, createdAt = Date.from(Instant.now())))
        }
    }

    fun deleteToDo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteToDo(id)
        }
    }
}
