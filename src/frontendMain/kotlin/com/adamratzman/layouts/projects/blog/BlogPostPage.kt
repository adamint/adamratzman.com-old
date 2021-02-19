package com.adamratzman.layouts.projects.blog

import com.adamratzman.database.View.ViewBlogHomePage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.setTitle
import com.adamratzman.services.BlogServiceFrontend
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.isMobile
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import com.adamratzman.utils.showDefaultErrorToast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.h1
import pl.treksoft.kvision.html.link
import pl.treksoft.kvision.html.p
import pl.treksoft.kvision.remote.ServiceException

class BlogPostPage(id: String, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    GlobalScope.launch {
        val post = BlogServiceFrontend.blogService.getBlogPost(id.trim())
        setTitle("Blog - ${post.title}")
        try {
            div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
                div(classes = nameSetOf(MarginAuto, TextLeft, if (!isMobile()) WidthThreeFifths else WidthThreeFourths)) {
                    h1(classes = nameSetOf(MarginSmallBottom), content = post.title)
                    p(
                        classes = nameSetOf("time", MarginRemoveTop, MarginRemoveBottom),
                        content = "Published ${post.serverTimeString} by Adam Ratzman"
                    )
                    p(classes = nameSetOf("time", MarginRemoveTop, MarginSmallBottom)) {
                        +"Go back to "
                        link(label = "blog home →", ViewBlogHomePage(listOf()).devOrProdUrl())
                    }
                    div(content = post.richText, rich = true)
                }
            }

            removeLoadingSpinner(state)
        } catch (e: ServiceException) {
            e.showDefaultErrorToast("Unable to load blog post")
        }
    }
})