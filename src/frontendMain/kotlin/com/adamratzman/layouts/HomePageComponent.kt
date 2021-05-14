package com.adamratzman.layouts

import com.adamratzman.layouts.partials.ExperienceComponent
import com.adamratzman.layouts.partials.TechnicalSkillsComponent
import com.adamratzman.utils.UikitName.MarginAuto
import com.adamratzman.utils.UikitName.MarginMediumBottom
import com.adamratzman.utils.UikitName.MarginMediumTop
import com.adamratzman.utils.UikitName.MarginRemoveBottom
import com.adamratzman.utils.UikitName.MarginRemoveTop
import com.adamratzman.utils.UikitName.MarginSmallBottom
import com.adamratzman.utils.UikitName.MarginSmallTop
import com.adamratzman.utils.UikitName.WidthTwoThirds
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.toDevOrProdUrl
import io.kvision.core.Container
import io.kvision.core.UNIT.rem
import io.kvision.core.style
import io.kvision.html.div
import io.kvision.html.h2
import io.kvision.html.h3
import io.kvision.html.h4
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.html.span

class HomePageComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginMediumTop, WidthTwoThirds, MarginAuto)) {
        h2(classes = nameSetOf(MarginRemoveBottom, "light")) {
            style { fontSize = 2.5 to rem }
            +"Hi. I'm "
            span(content = "Adam Ratzman", classes = nameSetOf("dashed"))
            +", a graduate student at Indiana University Bloomington"
        }
        p(classes = nameSetOf(MarginMediumBottom, MarginSmallTop, "rem-1-2")) {
            +"You can read below to learn more about me or see some of my "
            link(label = "interactive projects →", url = "/interactives".toDevOrProdUrl(), classes = nameSetOf("light"))
        }

        // about
        div(classes = nameSetOf(MarginMediumBottom.asString)) {
            h3(content = "About me", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))
            h4(classes = nameSetOf(MarginRemoveTop, MarginSmallBottom, "experience-width")) {
                +"I'm a "
                span(content = "graduate student*", classes = nameSetOf("dashed")) {
                    setAttribute(
                        "uk-tooltip",
                        """title: I will graduate in Spring 2022 with an MS in Computer Science.
                                """.trimMargin().replace("\n", "")
                    )
                }
                +" studying Computer Science. I build software and distributed systems, tools, and APIs."
            }
            h4(classes = nameSetOf(MarginRemoveTop, "experience-width")) {
                +"Currently, I'm a Software Engineering (SWE) Intern at the Naval Surface Warfare Center in Crane, Indiana."
                +" I am a former software engineering intern at E-gineering and current intern at Microsoft."
            }
        }
        // education
        div(classes = nameSetOf(MarginMediumBottom.asString)) {
            h3(content = "Education", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.educationExperience.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }

        // work experience
        div(classes = nameSetOf(MarginMediumBottom.asString)) {
            h3(content = "Work Experience", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.workExperience.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }

        // technical skills
        TechnicalSkillsComponent(this)

        // selected projects
        div(classes = nameSetOf(MarginMediumBottom.asString)) {
            h3(content = "Selected Projects", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.selectedProjects.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }

        // student involvement
        div(classes = nameSetOf(MarginMediumBottom.asString)) {
            h3(content = "Student Involvement", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.studentInvolvement.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }
    }
})