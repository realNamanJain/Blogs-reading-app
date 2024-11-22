package com.example.blogreadingapp.BlogApp

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.tasks.await

// Repository class for fetching blog posts from Firestore or API
class BlogRepository(private val firestore: FirebaseFirestore, private val blogApiService: BlogApiService) {
    private var lastVisible: DocumentSnapshot? = null // To handle pagination in Firestore

    init {
        // Enable offline persistence for Firestore
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Allow Firestore to cache data locally
            .build()
    }

    // Fetch blog posts from Firestore cache (offline-first approach)
    suspend fun getPostsFromFirestore(): List<BlogResponse> {
        return try {
            // Firestore will return cached data if the device is offline
            val snapshot = firestore.collection("blog_posts")
                .get()
                .await()

            // Map documents to BlogResponse objects
            snapshot.documents.mapNotNull { it.toObject(BlogResponse::class.java) }
        } catch (e: Exception) {
            Log.e("BlogRepository", "Error fetching posts from Firestore cache", e)
            emptyList() // Return an empty list in case of failure
        }
    }

    // Fetch blog posts from the API and cache them in Firestore
    suspend fun getPostsFromApi(page: Int): List<BlogResponse> {
        return try {
            // Fetch posts from the API
            val posts = blogApiService.getPosts(perPage = 10, page = page)
            Log.d("BlogRepository", "Fetched posts from API: $posts")

            // Cache the fetched posts in Firestore
            posts.forEach { post ->
                firestore.collection("blog_posts")
                    .document(post.id.toString()) // Document ID based on post ID
                    .set(post) // Save post data
                    .await()
            }

            posts // Return the fetched posts
        } catch (e: Exception) {
            Log.e("BlogRepository", "Error fetching posts from API", e)
            emptyList() // Return an empty list in case of failure
        }
    }

    // Load more posts with pagination from Firestore
    suspend fun loadMorePostsFromFirestore(): List<BlogResponse> {
        return try {
            // Firestore query to fetch posts after the last visible document for pagination
            val query = firestore.collection("blog_posts")
                .apply { lastVisible?.let { startAfter(it) } }
                .limit(10)

            val snapshot = query.get().await()
            val newPosts = snapshot.documents.mapNotNull { it.toObject(BlogResponse::class.java) }
            lastVisible = snapshot.documents.lastOrNull() // Update the last visible document for pagination

            newPosts // Return the newly fetched posts
        } catch (e: Exception) {
            Log.e("BlogRepository", "Error loading more posts from Firestore", e)
            emptyList() // Return an empty list in case of failure
        }
    }

    // Force Firestore to fetch from cache (offline mode) even when network is available
    suspend fun getPostsFromFirestoreOffline(): List<BlogResponse> {
        return try {
            // Set Firestore to use only the cached data
            val snapshot = firestore.collection("blog_posts")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(BlogResponse::class.java) }
        } catch (e: Exception) {
            Log.e("BlogRepository", "Error fetching posts from Firestore cache (offline mode)", e)
            emptyList() // Return an empty list in case of failure
        }
    }
}
