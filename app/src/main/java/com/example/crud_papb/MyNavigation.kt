package com.example.crud_papb

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crud_papb.pages.Homepage
import com.example.crud_papb.pages.LoginPage
import com.example.crud_papb.pages.SignupPage

@Composable
fun MyNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier, navController, authViewModel )
        }
        composable("signup"){
            SignupPage(modifier, navController, authViewModel )
        }
        composable("home"){
            Homepage(modifier, navController, authViewModel )
        }
    })
}