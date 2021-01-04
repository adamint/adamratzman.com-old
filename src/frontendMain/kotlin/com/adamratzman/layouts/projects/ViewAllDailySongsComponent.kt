package com.adamratzman.layouts.projects

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.LogoutPage
import com.adamratzman.database.View.ViewDailySongPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.services.DailySongServiceFrontend
import com.adamratzman.services.UserRole
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.UikitName.Icon
import com.adamratzman.utils.addAttributes
import com.adamratzman.utils.addUikitAttributes
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.perc
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.*

class ViewAllDailySongsComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
        div(classes = nameSetOf(WidthTwoThirds, MarginAuto)) {
            GlobalScope.launch {
                val dailySongs = DailySongServiceFrontend
                    .getAllDays()

                h2(content = "Daily Song Recommendation", classes = nameSetOf(MarginRemoveBottom, "moderate-bold"))
                goBackToProjectHome()

                div(classes = nameSetOf(GridSmall, ChildWidthOneHalf.medium, MarginMediumTop, MarginMediumBottom)) {
                    addUikitAttributes(UkGridAttribute)

                    dailySongs.forEach { dailySong ->
                        val date = dailySong.date
                        div {
                            div(classes = nameSetOf(UkCard, UkCardDefault, UkCardBody)) {
                                h3(className = UkCardTitle.asString) {
                                    link(
                                        label = "${date.monthNumber + 1}/${date.dayOfMonth}/${date.year.toString().takeLast(2)}",
                                        url = ViewDailySongPage(date).devOrProdUrl()
                                    )
                                    if (state.clientSideData?.role == UserRole.ADMIN) {
                                        link(label = " ", classes = nameSetOf(Icon, MarginSmallLeft)) {
                                            addAttributes(IconAttribute to "close")
                                            onClick {
                                                GlobalScope.launch {
                                                    if (DailySongServiceFrontend.deleteDay(date)) {
                                                        window.location.reload()
                                                    } else {
                                                        SiteManager.redirectToUrl(LogoutPage.devOrProdUrl())
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                iframe(src = "https://open.spotify.com/embed/track/${dailySong.trackId}") {
                                    style { width = 100 to perc; height = 235 to px; }
                                }
                            }
                        }
                    }
                }

                removeLoadingSpinner(state)
            }
        }
    }

})