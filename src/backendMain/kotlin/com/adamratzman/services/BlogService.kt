package com.adamratzman.services

import com.adamratzman.database.*
import com.google.inject.Inject
import io.ktor.application.ApplicationCall
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Op.Companion
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import pl.treksoft.kvision.remote.ServiceException

actual class BlogService : IBlogService {
    @Inject
    lateinit var call: ApplicationCall

    override suspend fun getBlogPost(id: String): BlogPost {
        return transaction { BlogPostEntity.findById(id)?.toFrontendObject() ?: throw ServiceException("Blog post not found") }
    }

    override suspend fun getBlogPosts(filterCategories: List<String>): List<BlogPost> {
        return transaction {
            BlogPostEntity
                .all()
                .filter { !it.deleted }
                .filter { entity -> if (filterCategories.isEmpty()) true else entity.categories.toList().any { it.category in filterCategories } }
                .distinctBy { it.id.value }
                .map { it.toFrontendObject() }
        }.apply { println(this) }
    }

    override suspend fun getCommentsForBlogPost(postId: String): List<BlogPostComment> {
        return transaction {
            BlogPostEntity.findById(postId)?.comments?.map { it.toFrontendObject() } ?: throw ServiceException("Blog post not found")
        }
    }

    override suspend fun createOrUpdateBlogPost(blogPost: BlogPost): Boolean {
        transaction {
            val user = call.sessions.get<UserPrincipal>()?.getUser() ?: throw ServiceException("You are not logged in.")
            if (user.role != UserRole.Admin) throw ServiceException("You do not have permissions to create this post.")
            val existingBlogPostEntity = BlogPostEntity.findById(blogPost.id)
            @Suppress("IfThenToElvis")
            if (existingBlogPostEntity != null) {
                existingBlogPostEntity.mutate(blogPost)
            } else {
                BlogPostEntity.new(blogPost.id) {
                    this.mutate(blogPost)
                }
            }
        }
        return true
    }

    override suspend fun deleteBlogPost(id: String, shouldDelete: Boolean): Boolean {
        transaction {
            val user = call.sessions.get<UserPrincipal>()?.getUser() ?: throw ServiceException("You are not logged in.")
            if (user.role != UserRole.Admin) throw ServiceException("You do not have permissions to delete this post.")
            val existingBlogPostEntity = BlogPostEntity.findById(id) ?: throw ServiceException("Blog post not found")
            existingBlogPostEntity.deleted = shouldDelete
        }
        return true
    }

    override suspend fun createOrUpdateBlogPostComment(blogPostComment: BlogPostComment): Boolean {
        transaction {
            val user = call.sessions.get<UserPrincipal>()?.getUser() ?: throw ServiceException("You are not logged in.")
            val existingBlogPostCommentEntity = BlogPostCommentEntity.findById(blogPostComment.id)
            if (existingBlogPostCommentEntity != null && user.role != UserRole.Admin && existingBlogPostCommentEntity.user != user) {
                throw ServiceException("You do not have permissions to create/update this comment.")
            }

            @Suppress("IfThenToElvis")
            if (existingBlogPostCommentEntity != null) {
                existingBlogPostCommentEntity.mutate(blogPostComment)
            } else {
                BlogPostCommentEntity.new(blogPostComment.id) {
                    this.mutate(blogPostComment)
                }
            }
        }
        return true
    }

    override suspend fun deleteBlogPostComment(id: Int): Boolean {
        transaction {
            val user = call.sessions.get<UserPrincipal>()?.getUser() ?: throw ServiceException("You are not logged in.")
            if (user.role != UserRole.Admin) throw ServiceException("You do not have permissions to delete this post.")
            val existingBlogPostCommentEntity = BlogPostCommentEntity.findById(id) ?: throw ServiceException("Blog post comment not found")
            existingBlogPostCommentEntity.deleted = true
        }
        return true
    }
}