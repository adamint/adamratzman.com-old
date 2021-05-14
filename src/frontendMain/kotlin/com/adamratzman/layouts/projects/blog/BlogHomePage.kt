package com.adamratzman.layouts.projects.blog

import com.adamratzman.database.View.ViewBlogPostPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.setTitle
import com.adamratzman.services.BlogServiceFrontend
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.getSearchParams
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import io.kvision.core.Container
import io.kvision.core.FontStyle.ITALIC
import io.kvision.core.style
import io.kvision.html.*

class BlogHomePage(filterCategories: List<String>, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    val all = getSearchParams().get("all") == "true"

    div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
        div(classes = nameSetOf(TextCenter)) {
            h2(classes = nameSetOf("moderate-bold", MarginSmallBottom)) {
                if (filterCategories.isEmpty()) {
                    if (all) +"Blog - all posts"
                    else +"Blog"
                } else {
                    textNode("Posts tagged ")
                    span(classes = nameSetOf("link-color"), content = filterCategories.joinToString(","))
                }
            }
            if (!all) p {
                +"View "
                link(label = "all posts →", "/blog?all=true")
            }
        }

        GlobalScope.launch {
            val posts = BlogServiceFrontend.blogService.getBlogPosts(filterCategories)
            div(classes = nameSetOf(TextCenter, MarginMediumBottom)) {
                posts.forEach { post ->
                    div(classes = nameSetOf(MarginAuto, TextLeft, WidthOneHalf)) {
                        h3(classes = nameSetOf(UkCardTitle, MarginMediumTop, MarginRemoveBottom)) {
                            link(label = post.title, url = ViewBlogPostPage(post.id).devOrProdUrl())
                        }
                        p(classes = nameSetOf("time", MarginRemoveTop), content = post.serverTimeString)
                        p(content = post.serverSnippet, rich = true)
                        p {
                            +"Categories: "
                            textNode(content = post.serverCategoriesHtml ?: "", rich = true)
                        }
                    }
                }
            }

            removeLoadingSpinner(state)
        }
    }
})