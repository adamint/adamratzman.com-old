package com.adamratzman.layouts

import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.toDevOrProdUrl
import io.kvision.core.Container
import io.kvision.data.dataContainer
import io.kvision.html.*

class ProjectsHomeComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    val projectCategories = state.interactives
            .sortedBy { it.name }
            .groupBy { it.category }.toList()
            .sortedBy { it.first }.asReversed()

    div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom, MarginAuto, WidthTwoThirds)) {
        div {
            h2(content = "Projects", classes = nameSetOf(MarginSmallBottom, "moderate-bold"))
            h3(content = "An incomplete list of online projects and utilities I've created.", classes = nameSetOf("light", MarginRemoveTop))
        }

        div {
            projectCategories.forEach { (category, projects) ->
                h2(content = category.toString(), classes = nameSetOf(UkInline, MarginSmallBottom, MarginRemoveTop, "moderate-bold"))
                dataContainer(projects.toMutableList(), { project, _, _ ->
                    div(classes = nameSetOf(MarginSmallBottom.asString)) {
                        h4(classes = nameSetOf(MarginRemoveVertical.asString)) {
                            link(label = project.name, url = project.url.toDevOrProdUrl())
                        }
                        p(content = project.description, rich = true, classes = nameSetOf(MarginRemoveVertical))
                    }
                })
            }
        }
    }

})