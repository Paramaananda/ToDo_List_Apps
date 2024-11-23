package com.example.todo_list_apps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo_list_apps.Pages.HomePage
import com.example.todo_list_apps.Pages.SigninPage
import com.example.todo_list_apps.Pages.SignUpPage

@Composable
fun Navigation( modifier: Modifier = Modifier, authViewModel: AuthViewModel){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            SigninPage(modifier, navController, authViewModel )
        }
        composable("register"){
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("home"){
            HomePage(modifier, navController, authViewModel)
        }
    })
}


