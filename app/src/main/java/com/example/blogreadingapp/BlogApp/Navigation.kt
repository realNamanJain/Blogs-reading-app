package com.example.blogreadingapp.BlogApp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

@Composable
fun BlogAppNavHost(navController: NavHostController, viewModel: BlogViewModel) {
    // Navigation host with the start destination as blogList screen
    NavHost(navController = navController, startDestination = "blogList") {
        // Blog List Screen route
        composable("blogList") {
            BlogListScreen(navController = navController, viewModel = viewModel)
        }
        // Blog Detail Screen route, dynamically fetching blogId
        composable("blogDetail/{blogId}") { backStackEntry ->
            val blogId = backStackEntry.arguments?.getString("blogId")?.toInt() ?: 0
            BlogDetailScreen(blogId = blogId, viewModel = viewModel)
        }
    }
}