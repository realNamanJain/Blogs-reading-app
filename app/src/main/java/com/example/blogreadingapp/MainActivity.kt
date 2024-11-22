package com.example.blogreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.blogreadingapp.BlogApp.BlogApiService
import com.example.blogreadingapp.BlogApp.BlogAppNavHost
import com.example.blogreadingapp.BlogApp.BlogRepository
import com.example.blogreadingapp.BlogApp.BlogViewModel
import com.example.blogreadingapp.ui.theme.BlogReadingAppTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private val firestore = FirebaseFirestore.getInstance() // Initialize FirebaseFirestore
    private val blogApiService = BlogApiService.create() // Initialize BlogApiService (Assuming a create method exists for the service)
    private val blogRepository = BlogRepository(firestore, blogApiService) // Pass them to the BlogRepository

    private val viewModel: BlogViewModel by viewModels {
        BlogViewModelFactory(blogRepository) // Pass the BlogRepository to the ViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            BlogAppNavHost(navController, viewModel)
        }
    }
}

class BlogViewModelFactory(private val blogRepository: BlogRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BlogViewModel(blogRepository) as T
    }
}

