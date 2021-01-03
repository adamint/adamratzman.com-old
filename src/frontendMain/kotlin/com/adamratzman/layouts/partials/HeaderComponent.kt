package com.adamratzman.layouts.partials

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.utils.*
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.UikitName.Icon
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.*

class HeaderComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    header {
        nav(classes = nameSetOf(NavbarContainer, MarginLargeRight, NavbarTransparent, MarginLargeLeft, MarginSmallTop, MarginSmallBottom)) {
            addUikitAttributes(NavbarAttribute)

            div(classes = nameSetOf(NavbarLeft.asString)) {
                ul(classes = nameSetOf(NavbarNav.asString)) {
                    li {
                        div(classes = nameSetOf(NavbarItem, "super-bold", "rem-1")) {
                            link(label = "Adam Ratzman", url = "/".toDevOrProdUrl(), classes = nameSetOf("black", "disable-link-highlighting"))
                        }
                    }
                }
            }

            div(classes = nameSetOf(NavbarRight, HiddenMedium)) {
                link(label = "", url = "#offcanvas", classes = nameSetOf(NavbarToggle, NavbarToggleIcon, Icon)) {
                    addUikitAttributes(NavbarToggleIconAttribute, ToggleAttribute)
                }
            }

            div(classes = nameSetOf(HiddenMedium.asString)) {
                id = "offcanvas"
                addAttributes(OffCanvasAttribute to "overlay: true")
                div(classes = nameSetOf(OffCanvasBar, Flex, FlexColumn)) {
                    button(classes = nameSetOf(OffCanvasClose, "black"), text = "") {
                        addUikitAttributes(CloseAttribute)
                    }

                    ul(state = state.accessibleNavbarPages, classes = nameSetOf(UkNav, UkNavPrimary, MarginMediumTop)) { pages ->
                        pages.filter { it.view != null }.forEach { navbarPage ->
                            li(classes = if (navbarPage.view?.isSameView(state.view) == true) nameSetOf(Active, "link-color") else null) {
                                link(label = navbarPage.name, url = navbarPage.url)
                            }
                        }


                        li(classes = nameSetOf(UkNavDivider.asString))

                        pages.filter { it.view == null }.forEach { navbarPage ->
                            li {
                                link(label = "", url = navbarPage.url) {
                                    span { addAttributes(IconAttribute to "icon: link") }
                                    +navbarPage.name.prependSpace()
                                }
                            }
                        }
                    }
                }
            }

            div(classes = nameSetOf(VisibleMedium, NavbarRight)) {
                ul(state = state.accessibleNavbarPages, classes = nameSetOf(NavbarNav.asString)) { pages ->
                    pages.forEach { navbarPage ->
                        li {
                            div(classes = nameSetOf(NavbarItem, "bold", "rem-1")) {
                                if (navbarPage.icon == null) link(label = navbarPage.name, url = navbarPage.url, classes = nameSetOf("black"))
                                else {
                                    link(label = "", url = navbarPage.url, classes = nameSetOf(MarginSmallRight.asString)) {
                                        image(src = navbarPage.icon)
                                    }
                                    link(label = navbarPage.name, url = navbarPage.url, classes = nameSetOf("black"))
                                }
                            }

                        }
                    }

                }
            }
        }
    }
})