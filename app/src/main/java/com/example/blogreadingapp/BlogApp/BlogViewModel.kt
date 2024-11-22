package com.example.blogreadingapp.BlogApp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel to manage blog posts data and loading state
class BlogViewModel(private val blogRepository: BlogRepository) : ViewModel() {
    private val _posts = MutableStateFlow<List<BlogResponse>>(emptyList()) // State for blog posts
    val posts: StateFlow<List<BlogResponse>> = _posts

    private val _isLoading = MutableStateFlow(false) // State for loading indicator
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null) // State for error messages
    val errorMessage: StateFlow<String?> = _errorMessage

    private var currentPage = 1 // Page number for pagination

    init {
        fetchPostsFromCache() // Fetch cached posts when the ViewModel is initialized
    }

    // Fetch blog posts from Firestore cache
    private fun fetchPostsFromCache() {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true while fetching data
            try {
                val cachedPosts = blogRepository.getPostsFromFirestore() // Get cached posts
                _posts.value = cachedPosts // Update posts with cached data
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch cached posts: ${e.message}"
            } finally {
                _isLoading.value = false // Stop loading once data is fetched
            }
        }
    }

    // Fetch posts from API
    fun fetchPostsFromApi(page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true while fetching data
            try {
                val fetchedPosts = blogRepository.getPostsFromApi(page) // Get posts from API
                _posts.value = fetchedPosts // Update posts with fetched data
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch posts: ${e.message}"
            } finally {
                _isLoading.value = false // Stop loading once data is fetched
            }
        }
    }

    // Load more posts with pagination
    fun loadMorePosts() {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true while fetching more data
            try {
                currentPage++ // Increment page number for pagination
                val newPosts = blogRepository.getPostsFromApi(currentPage) // Fetch more posts from API
                _posts.value = _posts.value + newPosts // Append new posts to existing list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load more posts: ${e.message}"
            } finally {
                _isLoading.value = false // Stop loading once data is fetched
            }
        }
    }
}
