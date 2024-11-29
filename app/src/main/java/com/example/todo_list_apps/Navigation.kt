package com.example.todo_list_apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo_list_apps.Pages.HomePage
import com.example.todo_list_apps.Pages.SigninPage
import com.example.todo_list_apps.Pages.SignUpPage
import com.example.todo_list_apps.Pages.ToDoListPage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation( modifier: Modifier = Modifier, authViewModel: AuthViewModel, todoViewModel: ToDoViewModel){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            SigninPage(modifier, navController, authViewModel )
        }
        composable("register"){
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("home"){
            ToDoListPage(todoViewModel, navController, authViewModel)
        }
    })
}


