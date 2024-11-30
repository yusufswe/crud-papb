package com.example.crud_papb

import com.example.crud_papb.ToDoRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ToDoViewModel : ViewModel() {
    private val repository = ToDoRepository()

    private val _toDoList = MutableStateFlow<List<ToDoItem>>(emptyList())
    val toDoList: StateFlow<List<ToDoItem>> = _toDoList

    fun loadToDoItems() {
        viewModelScope.launch {
            val items = repository.getToDoItems()    // Operasi asynchronous
            _toDoList.value = items                  // Mengubah state UI setelah data didapat
        }
    }

    fun addToDoItem(title: String, description: String) {
        val newItem = ToDoItem(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            isDone = true
        )
        viewModelScope.launch {
            repository.addToDoItem(newItem) // Operasi asynchronous menyimpan ke Firestore
            loadToDoItems() // Memuat ulang data setelah menambahkan item baru
        }
    }

    fun updateToDoItem(updatedItem: ToDoItem) {
        viewModelScope.launch {
            repository.updateToDoItem(updatedItem) // Operasi asynchronous untuk update di Firestore
            loadToDoItems() // Memuat ulang data setelah update
        }
    }

    fun deleteToDoItem(item: ToDoItem) {
        viewModelScope.launch {
            repository.deleteToDoItem(item.id) // Hapus item berdasarkan ID
            loadToDoItems()                   // Refresh daftar
        }
    }


}