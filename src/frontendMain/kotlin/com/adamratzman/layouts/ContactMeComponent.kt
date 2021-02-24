package com.adamratzman.layouts

import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import io.kvision.core.Container
import io.kvision.core.UNIT.rem
import io.kvision.core.style
import io.kvision.html.*

class ContactMeComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = {
    div(classes = nameSetOf(WidthTwoThirds, MarginAuto, MarginMediumTop, PaddingSmall, PaddingRemoveHorizontal)) {
        h2(classes = nameSetOf(MarginRemoveBottom, "light")) {
            style { fontSize = 2.5 to rem }
            +"So, you'd like to contact me. Here's how."
        }

        p(classes = nameSetOf(MarginMediumBottom, MarginSmallTop, "rem-1-2")) {
            +"Recruiters, you may be interested in my "
            link(label = "LinkedIn →", url = "https://linkedin.com/in/aratzman", classes = nameSetOf("light"))
        }

        div(classes = nameSetOf(MarginMediumBottom.asString)) {
            h3(content = "Personal/Professional Inquiries", classes = nameSetOf(MarginSmallBottom, "bold", "solid-border-sm"))
            h4(classes = nameSetOf(MarginRemoveVertical.asString)) {
                +"My skills include application and web development, as well as DevOps and technical writing"
                +"I am currently "
                span(content = "not open", classes = nameSetOf("dashed"))
                +" to considering job and/or business opportunities related to these."
            }
            h4(classes = nameSetOf(MarginSmallTop.asString)) {
                +"Please contact me by email at "
                link(label = "adam@adamratzman.com", url = "mailto:adam@adamratzman.com")
                +"."
            }
        }
    }
})