package com.example.todo_list_apps.Pages

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.todo_list_apps.AuthViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_list_apps.Authstate
import com.example.todo_list_apps.ui.theme.urbanistFontFamily
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
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
    // State untuk daftar tugas dan input pengguna
    var newTask by remember { mutableStateOf(TextFieldValue("")) }
    val taskList = remember { mutableStateListOf<String>() }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TaskFlow",
                        fontFamily = urbanistFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    TextButton(onClick = {
                        authViewModel?.signout()
                        navController?.navigate("signin")
                    }) {
                        Text(
                            text = "Sign Out",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Your Tasks",
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Input tugas baru
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                BasicTextField(
                    value = newTask,
                    onValueChange = { newTask = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(56.dp)
                        .background(
                            Color.LightGray,
                            MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.Black,
                        fontFamily = urbanistFontFamily
                    )
                )
                Button(
                    onClick = {
                        if (newTask.text.isNotBlank()) {
                            taskList.add(newTask.text)
                            newTask = TextFieldValue("")
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(text = "Add")
                }
            }

            // Daftar tugas
            if (taskList.isEmpty()) {
                Text(
                    text = "You don't have any tasks yet.",
                    fontFamily = urbanistFontFamily,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray
                )
            } else {
                LazyColumn {
                    items(taskList.size) { index ->
                        TaskItem(
                            task = taskList[index],
                            onDelete = { taskList.removeAt(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: String, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF0F0F0), shape = MaterialTheme.shapes.small)
            .padding(16.dp)
    ) {
        Text(
            text = task,
            fontFamily = urbanistFontFamily,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    HomePage()
}