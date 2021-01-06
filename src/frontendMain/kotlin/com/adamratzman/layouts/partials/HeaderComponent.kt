package com.adamratzman.layouts.partials

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.*
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.utils.*
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.UikitName.Icon
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.*

class HeaderComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    header {
        nav(classes = nameSetOf(NavbarContainer, MarginLargeRight, NavbarTransparent, MarginSmallTop, MarginSmallBottom)) {
            if (!isMobile()) addCssClasses(MarginLargeLeft)
            else addCssClasses(MarginMediumLeft)
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

                        li(classes = nameSetOf(UkNavDivider.asString))

                        if (!state.isLoggedIn()) {
                            li(classes = if (LoginPage == state.view) nameSetOf(Active, "link-color") else null) {
                                link(label = "Log in/Register", url = LoginPage.devOrProdUrl())
                            }
                        } else {
                            li {
                                link(label = "My Profile", url = ProfilePage.devOrProdUrl())
                            }
                            li {
                                link(label = "Log out", url = LogoutPage.devOrProdUrl())
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

                    if (!state.isLoggedIn()) {
                        li {
                            div(classes = nameSetOf(NavbarItem, "bold", "rem-1")) {
                                link(label = "Log in/Register", url = LoginPage.devOrProdUrl(), classes = nameSetOf("black"))
                            }
                        }
                    } else {
                        li {
                            div(classes = nameSetOf(NavbarItem, "bold", "rem-1")) {
                                link(label = "My Profile", url = ProfilePage.devOrProdUrl(), classes = nameSetOf("black"))
                            }
                        }
                        li {
                            div(classes = nameSetOf(NavbarItem, "bold", "rem-1")) {
                                link(label = "Log out", classes = nameSetOf("black")) {
                                    onClick {
                                        state.clientSideData = null
                                        SiteManager.redirectToUrl(LogoutPage.devOrProdUrl())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})