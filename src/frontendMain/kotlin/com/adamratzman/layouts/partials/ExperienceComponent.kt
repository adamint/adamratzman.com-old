package com.adamratzman.layouts.partials

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.models.ResumeExperienceWrapper
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addAttributes
import com.adamratzman.utils.addLineBreak
import com.adamratzman.utils.nameSetOf
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.FontStyle.ITALIC
import pl.treksoft.kvision.core.TextAlign.RIGHT
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.data.dataContainer
import pl.treksoft.kvision.html.*

class ExperienceComponent(val experienceWrapper: ResumeExperienceWrapper, parent: Container) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        div(classes = nameSetOf(MarginSmallBottom.asString)) {
            h4(
                classes = if (experienceWrapper.start != null) nameSetOf(MarginRemoveVertical, UkInline) else nameSetOf(
                    MarginRemoveVertical
                )
            ) {
                bold {
                    if (experienceWrapper.locationUrl != null) link(
                        label = experienceWrapper.name,
                        url = experienceWrapper.locationUrl,
                        classes = nameSetOf("bold", "link-color")
                    )
                    else +experienceWrapper.name
                }
                +", ${experienceWrapper.location}"

                if (experienceWrapper.incoming) {
                    +" (${experienceWrapper.incomingLabel ?: "Incoming"})"
                }
            }

            h4(classes = nameSetOf(MarginRemoveVertical, FloatRight)) {
                style { textAlign = RIGHT }
                if (experienceWrapper.start != null) {
                    +"${experienceWrapper.start} - ${experienceWrapper.end ?: "Present"}"
                }

                if (experienceWrapper.secondLineFloatedRight != null) {
                    addLineBreak()
                    textNode(content = experienceWrapper.secondLineFloatedRight, rich = true)
                }
            }

            dataContainer(experienceWrapper.experiences.toMutableList(), { experience, _, _ ->
                div {
                    h4(
                        classes = if (experienceWrapper.start == null && !experienceWrapper.incoming) nameSetOf(
                            MarginRemoveVertical,
                            UkInline,
                            MarginSmallLeft
                        )
                        else nameSetOf(MarginRemoveVertical)
                    ) {
                        style { fontStyle = ITALIC }

                        if (experience.nameTooltip != null) {
                            span(content = experience.name, classes = nameSetOf("dashed")) {
                                addAttributes(UkTooltipAttribute.asString to "title: ${experience.nameTooltip}")
                            }
                        } else +experience.name
                    }

                    if (experienceWrapper.start == null && experience.period != null) {
                        h4(content = experience.period, classes = nameSetOf(MarginRemoveVertical, FloatRight))
                    }

                    if (experience.bullets?.isNotEmpty() == true) {
                        ul(classes = nameSetOf(MarginRemoveVertical, MarginMediumLeft, "width-80")) {
                            dataContainer(experience.bullets.toMutableList(), { bullet, _, _ ->
                                li(content = bullet, classes = nameSetOf("rem-1-2"))
                            })
                        }
                    }

                }
            })
        }

    })