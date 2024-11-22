package com.example.blogreadingapp.BlogApp

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.core.text.HtmlCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogListScreen(navController: NavController, viewModel: BlogViewModel) {
    // Collecting the blog posts and loading states from the ViewModel
    val posts: List<BlogResponse> by viewModel.posts.collectAsState(initial = emptyList())
    val isLoading: Boolean by viewModel.isLoading.collectAsState() // State to track loading status
    val errorMessage: String? by viewModel.errorMessage.collectAsState() // Error state

    // Fetch posts when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchPostsFromApi(page = 1)
    }

    // State for lazy list (pagination support)
    val listState = rememberLazyListState()

    // Efficient Pagination Trigger to load more posts when scrolled to the bottom
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisibleItemIndex == totalItems - 1
        }
    }

    // Trigger loading more posts when the bottom of the list is reached
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMorePosts()
        }
    }

    // Scaffold structure to hold the content with top app bar
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Blog Posts", fontFamily = FontFamily.Serif, fontSize = 24.sp) })
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // Display error message if there's an error
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }

                // LazyColumn to display blog posts
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp) // Add spacing around the list
                ) {
                    // Iterate through posts and display each item
                    items(posts) { post ->
                        BlogItem(post = post, onClick = {
                            navController.navigate("blogDetail/${post.id}")
                        })
                    }

                    // Loading indicator at the bottom (when loading more posts)
                    item {
                        if (shouldLoadMore.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    // Show loading indicator if the posts are still loading initially
                    if (isLoading && posts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun BlogItem(post: BlogResponse, onClick: () -> Unit) {
    // Card displaying each blog post with title and content
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC4E0EF)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Clean Blog Title
            val cleanTitle = post.title.rendered?.toPlainText()?.takeIf { it.isNotBlank() } ?: "No Title"
            Text(
                text = cleanTitle,
                style = TextStyle(fontSize = 24.sp, lineHeight = 24.sp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.Black,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Clean Blog Content (preview)
            val cleanContent = post.content.rendered?.toPlainText()?.takeIf { it.isNotBlank() } ?: "No Content Available"
            Text(
                text = cleanContent,
                style = TextStyle(fontSize = 16.sp, lineHeight = 20.sp),
                fontFamily = FontFamily.Serif,
                color = Color.Black,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

// Extension function to convert HTML to plain text
fun String?.toPlainText(): String {
    return this?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim() } ?: ""
}