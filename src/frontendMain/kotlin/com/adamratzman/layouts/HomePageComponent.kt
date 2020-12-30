package com.adamratzman.layouts

import com.adamratzman.layouts.partials.ExperienceComponent
import com.adamratzman.layouts.partials.TechnicalSkillsComponent
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.toDevOrProdUrl
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.rem
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.*

class HomePageComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    setTitle("Home")

    div(classes = nameSetOf(MarginMediumTop, "left-20", "right-20")) {
        h2(classes = nameSetOf(MarginRemoveBottom, "light")) {
            style { fontSize = 2.5 to rem }
            +"Hi. I'm "
            span(content = "Adam Ratzman", className = "dashed")
            +", a junior at Indiana University Bloomington"
        }
        p(classes = nameSetOf(MarginMediumBottom, MarginSmallTop, "rem-1-2")) {
            +"You can read below to learn more about me or see some of my "
            link(label = "interactive projects →", url = "/interactives".toDevOrProdUrl(), className = "light")
        }

        // about
        div(className = MarginMediumBottom.asString) {
            h3(content = "About me", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))
            h4(classes = nameSetOf(MarginRemoveTop, MarginSmallBottom, "experience-width")) {
                +"I'm a "
                span(content = "senior*", className = "dashed") {
                    setAttribute(
                            "uk-tooltip",
                            """title: My class status and graduation date are slightly complicated. 
                                    |I began college Fall 2019, but I am officially a senior and will finish all 
                                    |graduation requirements next semester (Spring 2021). However, I have decided 
                                    |to take an extra year (academic year 2021) to study abroad and pursue other 
                                    |academic interests, as I have no intention in graduating in 2 years. I will 
                                    |be starting a one year accelerated Master's in Computer Science at IU Fall 2022, 
                                    |and will graduate in May 2023, simultaneously receiving an MS in Computer Science 
                                    |and BS in Computer Science with Honors.
                                """.trimMargin().replace("\n", "")
                    )
                }
                +" studying Computer Science, Cognitive Science, and French. I build software and distributed systems, tools, and APIs."
            }
            h4(classes = nameSetOf(MarginRemoveTop, "experience-width")) {
                +"Currently, I'm a Software Engineering (SWE) Intern at the Naval Surface Warfare Center in Crane, Indiana."
                +" I am a former SWE intern at Microsoft and E-gineering, and will be returning to Microsoft as a summer 2021 SWE intern."
            }
        }
        // education
        div(className = MarginMediumBottom.asString) {
            h3(content = "Education", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.educationExperience.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }

        // work experience
        div(className = MarginMediumBottom.asString) {
            h3(content = "Work Experience", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.workExperience.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }

        // technical skills
        TechnicalSkillsComponent(this)

        // selected projects
        div(className = MarginMediumBottom.asString) {
            h3(content = "Selected Projects", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.selectedProjects.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }

        // student involvement
        div(className = MarginMediumBottom.asString) {
            h3(content = "Student Involvement", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))

            state.studentInvolvement.forEach { experienceWrapper ->
                ExperienceComponent(experienceWrapper, this)
            }
        }
    }
})