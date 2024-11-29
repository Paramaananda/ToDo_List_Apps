package com.example.todo_list_apps.Pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.todo_list_apps.AuthViewModel
import com.example.todo_list_apps.Authstate
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.todo_list_apps.R
import com.example.todo_list_apps.ToDo
import com.example.todo_list_apps.ToDoViewModel
import com.example.todo_list_apps.ui.theme.urbanistFontFamily

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
    var inputText by remember { mutableStateOf("") }

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
                Divider(
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
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        shape = RoundedCornerShape(10.dp),
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
                            todoViewModel.addToDo(inputText)
                            inputText = ""
                        },
//                modifier = Modifier.height(56.dp)
                    ) {
                        Text(text = "Add",
                            fontFamily = urbanistFontFamily)
                    }
                }
                todoList?.let {
                    LazyColumn(
                        content = {
                            itemsIndexed(it) { index: Int, item: ToDo ->
                                ToDoItem(item = item, onDelete = {
                                    todoViewModel.deleteToDo(item.id)
                                })
                            }
                        }
                    )
                } ?: Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "No items yet",
                    fontSize = 16.sp,
                    fontFamily = urbanistFontFamily
                )
            }
        }
    }
}

@Composable
fun ToDoItem(item: ToDo, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF0F0F0), shape = MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = MaterialTheme.shapes.small
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = SimpleDateFormat(
                    "HH:mm:aa, dd/mm/yyyy",
                    Locale.ENGLISH
                ).format(item.createdAt),
                fontSize = 10.sp,
                color = Color.Gray,
                fontFamily = urbanistFontFamily
            )
            Text(
                text = item.title,
                fontSize = 18.sp,
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
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

