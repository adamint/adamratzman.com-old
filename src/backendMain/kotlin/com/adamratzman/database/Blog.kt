package com.adamratzman.database

import com.adamratzman.services.BlogPost
import com.adamratzman.services.BlogPostComment
import com.adamratzman.utils.UpdateableWithFrontendObject
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jsoup.Jsoup
import java.text.DateFormat

object BlogPosts : IdTable<String>() {
    override val id = varchar("id", 250).entityId()
    override val primaryKey = PrimaryKey(id)

    val title = text("title")
    val author = reference("user", Users)
    val richText = text("richText")
    val creationMillis = long("creationMillis")
    val lastEditMillis = long("lastEditMillis").nullable()
    val deleted = bool("deleted")
}

class BlogPostEntity(id: EntityID<String>) : Entity<String>(id), UpdateableWithFrontendObject<BlogPostEntity, BlogPost> {
    companion object : EntityClass<String, BlogPostEntity>(BlogPosts)

    var title by BlogPosts.title
    var author by User referencedOn BlogPosts.author
    var richText by BlogPosts.richText
    var creationMillis by BlogPosts.creationMillis
    var lastEditMillis by BlogPosts.lastEditMillis
    var deleted by BlogPosts.deleted

    val categories by BlogPostCategoriesEntity referrersOn BlogPostCategories.post
    val comments by BlogPostCommentEntity referrersOn BlogPostComments.post

    override fun toFrontendObject(): BlogPost = BlogPost(
        id.value,
        title,
        author.username.value,
        categories.toList().map { it.category }.toMutableList(),
        richText,
        creationMillis,
        lastEditMillis,
        deleted,
        serverTimeString = DateFormat.getDateInstance().format(lastEditMillis ?: creationMillis),
        serverSnippet = Jsoup.parse(richText).text().take(50) + "..",
        serverCategoriesHtml = categories.toList().joinToString(", ") { "<a href='/blog?category=${it.category}'>${it.category}</a>" }
    )

    override fun getMutatingFunction(): BlogPostEntity.(BlogPost) -> Unit = { blogPost ->
        this.title = blogPost.title
        this.author = User.findById(blogPost.author)!!
        this.richText = blogPost.richText
        this.creationMillis = blogPost.creationMillis
        this.lastEditMillis = blogPost.lastEditMillis
        this.deleted = blogPost.deleted
        BlogPostCategoriesEntity.find { BlogPostCategories.post.eq(blogPost.id) }.forEach { it.delete() }
        blogPost.categories.forEach { category ->
            BlogPostCategoriesEntity.new {
                this.category = category
                this.post = this@BlogPostEntity
            }
        }
    }
}

object BlogPostCategories : IntIdTable() {
    val category = text("category")
    val post = reference("post", BlogPosts)
}

class BlogPostCategoriesEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, BlogPostCategoriesEntity>(BlogPostCategories)

    var category by BlogPostCategories.category
    var post by BlogPostEntity referencedOn BlogPostCategories.post
}


object BlogPostComments : IdTable<Int>() {
    override val id = integer("id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(id)

    val post = reference("blogPost", BlogPosts)
    val parentComment = reference("parentComment", BlogPostComments).nullable()
    val user = reference("username", Users)
    val deleted = bool("deleted")
    val content = text("content")
    val creationMillis = long("creationMillis")
    val lastEditMillis = long("lastEditMillis").nullable()

}


class BlogPostCommentEntity(id: EntityID<Int>) : Entity<Int>(id), UpdateableWithFrontendObject<BlogPostCommentEntity, BlogPostComment> {
    companion object : EntityClass<Int, BlogPostCommentEntity>(BlogPostComments)

    var post by BlogPostEntity referencedOn BlogPostComments.post
    var parentComment by BlogPostCategoriesEntity optionalReferencedOn BlogPostComments.parentComment
    var user by User referencedOn BlogPostComments.user
    var deleted by BlogPostComments.deleted
    var content by BlogPostComments.content
    var creationMillis by BlogPostComments.creationMillis
    var lastEditMillis by BlogPostComments.lastEditMillis

    override fun toFrontendObject(): BlogPostComment = BlogPostComment(
        id.value,
        post.id.value,
        this.parentComment?.id?.value,
        user.username.value,
        deleted,
        content,
        creationMillis,
        lastEditMillis
    )

    override fun getMutatingFunction(): BlogPostCommentEntity.(BlogPostComment) -> Unit = { blogPostComment ->
        this.post = BlogPostEntity.findById(blogPostComment.postId)!!
        this.parentComment = blogPostComment.parentCommentId?.let { BlogPostCategoriesEntity.findById(it) }
        this.user = User.findById(blogPostComment.username)!!
        this.deleted = blogPostComment.deleted
        this.content = blogPostComment.content
        this.creationMillis = blogPostComment.creationMillis
        this.lastEditMillis = blogPostComment.lastEditMillis
    }
}