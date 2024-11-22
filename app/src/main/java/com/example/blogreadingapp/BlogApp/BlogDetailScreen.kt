package com.example.blogreadingapp.BlogApp

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars

@Composable
fun BlogDetailScreen(blogId: Int, viewModel: BlogViewModel) {
    // Collecting the posts from ViewModel's StateFlow
    val posts = viewModel.posts.collectAsState(initial = emptyList()).value

    // Find the post by ID
    val post = posts.find { it.id == blogId }

    // If the post is found, show it in a WebView
    post?.let {
        WebViewScreen(url = it.link)
    }
}

@Composable
fun WebViewScreen(url: String) {
    // Box that adjusts content to account for the status bar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars) // Automatically adjusts for the status bar height
    ) {
        // WebView embedded in AndroidView for loading blog content
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        // Enable JavaScript for dynamic content
                        javaScriptEnabled = true

                        // Adjust layout to fit content to screen
                        loadWithOverviewMode = true
                        useWideViewPort = true

                        // Enable zoom controls for better user control
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false

                        // Enable smooth scrolling and DOM storage
                        domStorageEnabled = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }

                    // Prevent content clipping by scrolling to the top after loading
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.scrollTo(0, 0) // Ensure the content starts at the top
                        }
                    }

                    // Load the provided URL
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}