package com.adamratzman.layouts

import com.adamratzman.database.SiteManager
import com.adamratzman.database.SiteState
import kotlinx.browser.document
import org.w3c.dom.get
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.Align
import pl.treksoft.kvision.html.Div
import pl.treksoft.kvision.state.ObservableState
import pl.treksoft.kvision.state.bind

abstract class SiteStatefulComponent(
        content: String? = null,
        rich: Boolean = false,
        align: Align? = null,
        classes: Set<String> = setOf(),
        parent: Container,
        buildStatefulComponent: (Container.(SiteState) -> Unit)
) : AbstractCustomComponent<SiteState>(
        content,
        rich,
        align,
        classes,
        SiteManager.siteStore,
        parent,
        buildStatefulComponent = buildStatefulComponent
)

abstract class AbstractCustomComponent<S>(
        content: String? = null,
        rich: Boolean = false,
        align: Align? = null,
        classes: Set<String> = setOf(),
        bindState: ObservableState<S>? = null,
        parent: Container? = null,
        buildComponent: (Container.() -> Unit)? = null,
        buildStatefulComponent: (Container.(S) -> Unit)? = null
) : Div(content, rich, align, classes) {
    init {
        bindState?.let {
            bind(it, true) { state -> buildStatefulComponent?.invoke(this, state); if (state is SiteState) setTitle(state.view.name) }
        } ?: buildComponent?.invoke(this)
        parent?.add(this)
    }
}

fun setTitle(title: String, piped: Boolean = true) {
    val titleElement = document.getElementsByTagName("title")[0]!!
    titleElement.textContent = if (piped) "Adam Ratzman | $title" else title
}