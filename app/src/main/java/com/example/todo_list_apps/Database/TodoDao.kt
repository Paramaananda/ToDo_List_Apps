package com.example.todo_list_apps.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todo_list_apps.ToDo


@Dao
interface TodoDao {

    @Query("SELECT * FROM ToDo ORDER BY createdAt DESC")
    fun getAllToDo(): LiveData<List<ToDo>>

    @Insert
    fun addToDo(todo : ToDo)

    @Query("Delete FROM ToDo WHERE id = :id")
    fun deleteToDo(id: Int)

}