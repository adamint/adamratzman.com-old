package com.adamratzman.layouts

import com.adamratzman.layouts.partials.TechnicalSkillsComponent
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.rem
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.*

class PortfolioComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginAuto, WidthTwoThirds)) {
        div(classes = nameSetOf(MarginMediumTop)) {

            h2(classes = nameSetOf("light", MarginMediumBottom)) {
                style { fontSize = 2.5 to rem }
                +"Here are some things I've done."
            }

            div(classes = nameSetOf(MarginMediumBottom.asString)) {
                id = "selected-projects"

                h3(content = "Selected Projects", classes = nameSetOf("bold", "solid-border-sm", MarginSmallBottom))

                listOf(
                    "current" to state.currentProjects,
                    "past" to state.pastProjects
                ).forEach { pair ->
                    val name = pair.first
                    val projects = pair.second

                    div(classes = nameSetOf(MarginMediumBottom.asString)) {
                        h3(content = "$name projects", classes = nameSetOf(UkInline, MarginSmallBottom, MarginRemoveTop, "moderate-bold"))

                        projects.forEach { project ->
                            div(classes = nameSetOf(MarginSmallBottom.asString)) {
                                h4(classes = nameSetOf(MarginRemoveVertical.asString)) {
                                    link(label = project.name, target = "_blank", url = project.url)
                                }
                                h5(content = project.description, classes = nameSetOf(MarginRemoveVertical.asString))
                                h5(classes = nameSetOf(MarginRemoveVertical.asString)) {
                                    bold(content = "Categories:")
                                    +" ${project.codeCategories}"
                                }
                            }
                        }
                    }
                }
            }
        }

        TechnicalSkillsComponent(this)
    }

})