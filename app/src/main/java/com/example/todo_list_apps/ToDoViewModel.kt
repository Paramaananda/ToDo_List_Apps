package com.example.todo_list_apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToDoViewModel : ViewModel() {
    private var _todoList = MutableLiveData<List<ToDo>>()
    val todoList: LiveData<List<ToDo>> = _todoList

    fun getAllToDo() {
        _todoList.value = ToDoManager.getAllToDo().reversed()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addToDo(title: String) {
        ToDoManager.addToDo(title)
        getAllToDo()
    }

    fun deleteToDo(id : Int) {
        ToDoManager.deleteToDo(id)
        getAllToDo()
    }

}