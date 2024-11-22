package com.example.blogreadingapp.BlogApp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Interface defining the Blog API service
interface BlogApiService {

    // Method to get blog posts, using pagination
    @GET("wp/v2/posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int, // Number of posts per page
        @Query("page") page: Int         // Current page number
    ): List<BlogResponse> // Returns a list of BlogResponse

    companion object {
        // Base URL for the WordPress blog API
        private const val BASE_URL = "https://blog.vrid.in/wp-json/"

        // Function to create and return a Retrofit instance for BlogApiService
        fun create(): BlogApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL) // Set base URL
                .addConverterFactory(GsonConverterFactory.create()) // Gson converter to parse JSON responses
                .build()
                .create(BlogApiService::class.java) // Create the service
        }
    }
}
