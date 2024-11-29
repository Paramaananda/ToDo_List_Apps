package com.example.todo_list_apps.Pages

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.todo_list_apps.AuthViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.todo_list_apps.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null
) {
    val authState = authViewModel?.authState?.observeAsState()

//    LaunchedEffect(authState?.value) {
//        when (authState?.value) {
//            is Authstate.Unauthenticated -> navController?.navigate("login")
//            else -> Unit
//        }
//    }

    // State untuk data tugas
    var newTask by remember { mutableStateOf(TextFieldValue("")) }
    val taskList = remember { mutableStateListOf<String>() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    navController?.navigate("signin")
                }) {
                    Text(
                        text = "Sign Out",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
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
                        color = Color.Black
                    )

                    Text(
                        text = currentState.email ?: "User",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(30.dp))

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
                    OutlinedTextField(
                        value = newTask,
                        onValueChange = { newTask = it },
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF00A3FF),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Gray
                        ),
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
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
    HomePage(
        navController = null,
        authViewModel = null
    )
}