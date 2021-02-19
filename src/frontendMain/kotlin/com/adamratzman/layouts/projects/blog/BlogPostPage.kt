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
    /*<div class="uk-margin-small-top uk-section uk-section-large uk-padding-remove-bottom">
     {{#with post}}
         <div class="uk-margin-auto uk-text-left uk-width-3-5">
             <h1 class="uk-margin-small-bottom">{{title}}</h1>
  {{#each sections}}
                {{#if title}}<h3 class="bold">{{title}}</h3>{{/if}}
                {{#each paragraphs}}
                    <p>{{& text}}</p>
                {{/each}}
            {{/each}}

            {{#if referenceInfo}}
                {{#with referenceInfo}}
                    <h3 class="bold uk-margin-remove-bottom">References</h3>
                    <p class="uk-margin-remove-top">Citation style: <a href="{{& style.infoUrl}}">{{style}}</a></p>
                    <ol style="padding-left: 10px;">
                        {{#each references}}
                            <li style="margin-bottom: 4px;" id="citation-{{@index_1}}">{{& citationFinal}}</li>
                        {{/each}}
                    </ol>
                {{/with}}
            {{/if}}
 // more
         </div>
     {{/with}}
     <br/>
     <br/>
 </div>

     */
    GlobalScope.launch {
        val post = BlogServiceFrontend.blogService.getBlogPost(id.trim())
        setTitle("Blog - ${post.title}")
        try {
            div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
                div(classes = nameSetOf(MarginAuto, TextLeft, if (!isMobile()) WidthThreeFifths else "")) {
                    h1(classes = nameSetOf(MarginSmallBottom), content=post.title)
                    p(classes = nameSetOf("time", MarginRemoveTop, MarginRemoveBottom), content = "Published ${post.serverTimeString}")
                    p(classes = nameSetOf("time", MarginRemoveTop, MarginMediumBottom)) {
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