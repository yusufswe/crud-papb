package com.example.crud_papb.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.crud_papb.AuthState
import com.example.crud_papb.AuthViewModel
import com.example.crud_papb.ToDoItem
import com.example.crud_papb.ToDoViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homepage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    toDoViewModel: ToDoViewModel = viewModel ()
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var editMode by remember { mutableStateOf(false) }
    var currentEditingItem by remember { mutableStateOf<ToDoItem?>(null) }


    val toDoList by toDoViewModel.toDoList.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        toDoViewModel.loadToDoItems()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("To-Do App") },
            actions = {
                IconButton(onClick = {authViewModel.signout()}) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                }
            }
        )
    },
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) { innerPadding ->
        Column(
            modifier = modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (editMode) "Edit Title" else "Title") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Title Icon") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (editMode) "Edit Description" else "Description") },
                leadingIcon = { Icon(Icons.Default.Create, contentDescription = "Description Icon") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        if (editMode && currentEditingItem != null) {
                            val updatedItem = currentEditingItem!!.copy(
                                title = title,
                                description = description
                            )
                            toDoViewModel.updateToDoItem(updatedItem)
                            editMode = false
                            currentEditingItem = null
                        } else {
                            toDoViewModel.addToDoItem(title, description)
                        }
                        title = ""
                        description = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (editMode) "Update To-Do" else "Add To-Do")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(toDoList) { item ->
                    ToDoItemCard(item = item, onEdit = {
                        currentEditingItem = it
                        title = it.title
                        description = it.description
                        editMode = true
                    },
                        onDelete = {
                            toDoViewModel.deleteToDoItem(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = {
                authViewModel.signout()
            }) {
                Text(text = "Sign Out")
            }
        }
    }
}

@Composable
fun ToDoItemCard(item: ToDoItem, onEdit: (ToDoItem) -> Unit, onDelete: (ToDoItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = { onEdit(item) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Icon")
                    }
                    IconButton(onClick = { onDelete(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Icon")
                    }
                }
            }
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}