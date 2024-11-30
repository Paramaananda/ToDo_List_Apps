package com.example.todo_list_apps.Pages


import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.todo_list_apps.AuthViewModel
import com.example.todo_list_apps.Authstate
import com.example.todo_list_apps.R
import com.example.todo_list_apps.ToDo
import com.example.todo_list_apps.ToDoViewModel
import com.example.todo_list_apps.ui.theme.urbanistFontFamily
import java.text.SimpleDateFormat
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ToDoListPage(
    todoViewModel: ToDoViewModel,
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null
) {
    val authState = authViewModel?.authState?.observeAsState()

    LaunchedEffect(authState?.value) {
        when (authState?.value) {
            is Authstate.Unauthenticated -> navController?.navigate("login")
            else -> Unit
        }
    }

    val todoList by todoViewModel.todoList.observeAsState()
    var isAddTaskDialogVisible by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    var isCameraPageVisible by remember { mutableStateOf(false) }
    var newTaskImageUri by remember { mutableStateOf<Uri?>(null) }

    // If camera page is visible, show it
    if (isCameraPageVisible) {
        CameraPage(
            todoViewModel = todoViewModel,
            onImageCaptured = { uri ->
                newTaskImageUri = uri
                isCameraPageVisible = false
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                Column {
                    FloatingActionButton(
                        onClick = { isAddTaskDialogVisible = true },
                        containerColor = Color(0xFF00A3FF),
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Task"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = { isCameraPageVisible = true },
                        containerColor = Color(0xFF00A3FF),
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Camera,
                            contentDescription = "Open Camera"
                        )
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = Color(0xFF00A3FF),
                    contentColor = Color.Black
                ) {
                    Text(
                        text = "TaskFlow",
                        fontFamily = urbanistFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                    TextButton(onClick = {
                        authViewModel?.signout()
                        navController?.navigate("login")
                    }) {
                        Text(
                            text = "Sign Out",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            fontFamily = urbanistFontFamily
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ornamen),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    val currentState = authState?.value
                    if (currentState is Authstate.Authenticated) {
                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = "Selamat Datang",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = urbanistFontFamily
                        )

                        Text(
                            text = currentState.email ?: "User",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            fontFamily = urbanistFontFamily
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Your Tasks",
                        fontFamily = urbanistFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Black
                    )

                    todoList?.let {
                        LazyColumn(
                            content = {
                                itemsIndexed(it) { _, item: ToDo ->
                                    ToDoItem(
                                        item = item,
                                        onDelete = {
                                            todoViewModel.deleteToDo(item.documentId)
                                        },
                                        onToggleComplete = { completed ->
                                            todoViewModel.toggleToDoCompletion(item.documentId, completed)
                                        },
                                        onEdit = { newTitle ->
                                            todoViewModel.updateToDo(item.documentId, newTitle)
                                        }
                                    )
                                }
                            }
                        )
                    } ?: Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "No items yet",
                        fontSize = 16.sp,
                        fontFamily = urbanistFontFamily
                    )
                }
            }
        }
    }

    // Add Task Dialog with image support
    if (isAddTaskDialogVisible) {
        AlertDialog(
            onDismissRequest = { isAddTaskDialogVisible = false },
            title = {
                Text(
                    text = "Add New Task",
                    fontFamily = urbanistFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF00A3FF),
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = urbanistFontFamily
                        ),
                        placeholder = {
                            Text(
                                text = "Enter your task",
                                fontFamily = urbanistFontFamily,
                                color = Color.Gray
                            )
                        }
                    )

                    // Display selected image if available
                    newTaskImageUri?.let { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTaskText.isNotBlank()) {
                            todoViewModel.addToDoWithImage(newTaskText, newTaskImageUri)
                            newTaskText = ""
                            newTaskImageUri = null
                            isAddTaskDialogVisible = false
                        }
                    }
                ) {
                    Text(
                        "Add",
                        fontFamily = urbanistFontFamily,
                        color = Color(0xFF00A3FF)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isAddTaskDialogVisible = false }
                ) {
                    Text(
                        "Cancel",
                        fontFamily = urbanistFontFamily,
                        color = Color.Red
                    )
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoItem(
    item: ToDo,
    onDelete: () -> Unit,
    onToggleComplete: (Boolean) -> Unit,
    onEdit: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var isDeleteConfirmationDialogVisible by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(item.title) }

    // Delete Confirmation Dialog
    if (isDeleteConfirmationDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDeleteConfirmationDialogVisible = false },
            title = {
                Text(
                    text = "Delete Task",
                    fontFamily = urbanistFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this task?",
                    fontFamily = urbanistFontFamily
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        isDeleteConfirmationDialogVisible = false
                    }
                ) {
                    Text(
                        "Delete",
                        fontFamily = urbanistFontFamily,
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isDeleteConfirmationDialogVisible = false }
                ) {
                    Text(
                        "Cancel",
                        fontFamily = urbanistFontFamily,
                        color = Color(0xFF00A3FF)
                    )
                }
            }
        )
    }

    // Edit Dialog
    if (isEditing) {
        AlertDialog(
            onDismissRequest = { isEditing = false },
            title = {
                Text(
                    text = "Edit Task",
                    fontFamily = urbanistFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF00A3FF),
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = urbanistFontFamily
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editText.isNotBlank()) {
                            onEdit(editText)
                            isEditing = false
                        }
                    }
                ) {
                    Text(
                        "Save",
                        fontFamily = urbanistFontFamily,
                        color = Color(0xFF00A3FF)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isEditing = false }
                ) {
                    Text(
                        "Cancel",
                        fontFamily = urbanistFontFamily,
                        color = Color.Red
                    )
                }
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = if (item.isCompleted) Color(0xFFE0E0E0) else Color(0xFFF0F0F0),
                shape = MaterialTheme.shapes.small
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = MaterialTheme.shapes.small
            )
            .padding(16.dp)
    ) {
        // Checkbox for completion status
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = {
                onToggleComplete(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF00A3FF),
                uncheckedColor = Color.Gray
            ),
            modifier = Modifier.padding(end = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = SimpleDateFormat(
                    "HH:mm:aa, dd/MM/yyyy", // Menggunakan "MM" untuk bulan
                    Locale.ENGLISH
                ).format(item.createdAt),
                fontSize = 10.sp,
                color = Color.Gray,
                fontFamily = urbanistFontFamily,
                textDecoration = if (item.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
            Text(
                text = item.title,
                fontSize = 18.sp,
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = if (item.isCompleted) Color.Gray else Color.Black,
                textDecoration = if (item.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
        }

        // Edit Button
        IconButton(onClick = { isEditing = true }) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit",
                tint = Color(0xFF00A3FF)
            )
        }

        // Delete Button
        IconButton(onClick = { isDeleteConfirmationDialogVisible = true }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = Color.Red,
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ToDoListPagePreview() {
    ToDoListPage(todoViewModel = ToDoViewModel())
}

