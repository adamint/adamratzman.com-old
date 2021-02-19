package com.adamratzman.services

import kotlinx.serialization.Serializable
import pl.treksoft.kvision.annotations.KVService

@KVService
interface IBlogService {
    suspend fun getBlogPost(id: String): BlogPost
    suspend fun getBlogPosts(filterCategories: List<String>): List<BlogPost>
    suspend fun getCommentsForBlogPost(postId: String): List<BlogPostComment>
    suspend fun createOrUpdateBlogPost(blogPost: BlogPost): Boolean
    suspend fun deleteBlogPost(id: String, shouldDelete: Boolean): Boolean
    suspend fun createOrUpdateBlogPostComment(blogPostComment: BlogPostComment): Boolean
    suspend fun deleteBlogPostComment(id: Int): Boolean
}

@Serializable
data class BlogPost(
    val id: String,
    val title: String,
    val author: String,
    val categories: MutableList<String>,
    var richText: String,
    val creationMillis: Long,
    var lastEditMillis: Long? = null,
    var deleted: Boolean = false,
    var serverTimeString: String? = null,
    var serverSnippet: String? = null,
    var serverCategoriesHtml: String? = null
)

@Serializable
data class BlogPostComment(
    val id: Int,
    val postId: String,
    val parentCommentId: Int? = null,
    val username: String,
    var deleted: Boolean = false,
    var content: String,
    val creationMillis: Long,
    var lastEditMillis: Long? = null
)