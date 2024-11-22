package com.example.blogreadingapp.BlogApp

// Data class to represent a blog post response from the API
data class BlogResponse(
    val id: Int, // Unique ID of the post
    val date: String, // Post date
    val date_gmt: String, // GMT date of the post
    val guid: Guid, // GUID for the post
    val modified: String, // Date when the post was last modified
    val modified_gmt: String, // GMT date of last modification
    val slug: String, // URL slug for the post
    val status: String, // Status of the post (e.g., "publish")
    val type: String, // Type of post (e.g., "post")
    val link: String, // URL to the post
    val title: Title, // Title object containing the rendered title
    val content: Content // Content object containing the rendered content
)

// Nested data class for GUID of the post
data class Guid(
    val rendered: String // Rendered GUID as a string
)

// Nested data class for Title of the post
data class Title(
    val rendered: String // Rendered title as a string
)

// Nested data class for Content of the post
data class Content(
    val rendered: String // Rendered content as a string
)
