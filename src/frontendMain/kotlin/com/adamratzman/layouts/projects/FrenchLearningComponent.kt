package com.adamratzman.layouts.projects

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.setTitle
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.FontStyle.ITALIC
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.*

class FrenchLearningComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = {
    div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom, WidthTwoThirds, MarginAuto)) {
        div(classes = nameSetOf(UkInline))
        div(classes = nameSetOf(UkInline.asString)) {
            h2(content = "Learning French", classes = nameSetOf(MarginSmallBottom, "moderate-bold"))
            h3(content = "A short (and  in-progress) list of French learning resources", classes = nameSetOf(MarginRemoveTop, "light"))
        }
        div()

        div(classes = nameSetOf(UkInline))
        h3(content = "I'd like to learn about..", classes = nameSetOf(UkInline, MarginMediumBottom, "moderate-bold"))
        div()


        div(classes = nameSetOf(UkInline))
        div(classes = nameSetOf(UkInline.asString)) {
            p {
                +"Present Tense "
                span {
                    style { fontStyle = ITALIC }
                    +"-er"
                }
                +" Verbs → "
                link(label = "(pdf)", url = "/static/files/Present%20Tense%20-er%20Verb%20Changes.pdf", classes = nameSetOf("link-color"))
            }

            p {
                +"Relative Pronouns → "
                link(label = "(pdf)", url = "/static/files/Relative%20Pronouns.pdf", classes = nameSetOf("link-color"))
            }
        }

    }
})