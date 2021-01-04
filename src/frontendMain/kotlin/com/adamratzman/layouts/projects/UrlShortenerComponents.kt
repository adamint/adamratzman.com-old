package com.adamratzman.layouts.projects

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.*
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.services.*
import com.adamratzman.services.ShortenedUrlDto
import com.adamratzman.utils.*
import com.adamratzman.utils.UikitName.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.TextDecoration
import pl.treksoft.kvision.core.TextDecorationLine
import pl.treksoft.kvision.core.UNIT.perc
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.form.check.checkBox
import pl.treksoft.kvision.form.formPanel
import pl.treksoft.kvision.form.text.TextInputType
import pl.treksoft.kvision.form.text.text
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.hPanel
import pl.treksoft.kvision.remote.ServiceException
import kotlin.random.Random

class UrlShortenerHomePageComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = {
    div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
        h2(content = "URL Shortener", classes = nameSetOf(MarginRemoveBottom, TextCenter, "moderate-bold"))
        p(classes = nameSetOf(MarginSmallTop, MarginMediumBottom, TextCenter, "light")) {
            goBackToProjectHome()
        }

        div(classes = nameSetOf(MarginAuto, MarginSmallBottom, WidthOneHalf)) {
            h5(content = "Ever wanted your URL shortener to randomly rickroll you?", classes = nameSetOf(MarginMediumBottom, "light"))

            div {
                addBootstrap()
                h3(content = "Shorten a URL")

                val inputWidths = 100 to perc
                formPanel<ShortenedUrlDto>(classes = nameSetOf(MarginMediumBottom.asString)) {
                    add(
                        ShortenedUrlDto::url,
                        text(type = TextInputType.URL, label = "URL to shorten").withPlaceholderAndMaxWidth(inputWidths, "Enter URL here"),
                        required = true,
                        requiredMessage = "This field is required",
                        validatorMessage = { "Please enter a valid URL, beginning with http:// or https://" }
                    ) { urlText -> urlText.getValue()?.contains("http") }

                    add(
                        ShortenedUrlDto::path,
                        text(label = "Custom link path (optional)").withPlaceholderAndMaxWidth(inputWidths, "Enter path"),
                        required = false,
                        validatorMessage = { "The path length must be between 1 and $shortenedUrlPathMaxLength alphanumeric characters" }
                    ) { pathText ->
                        pathText.getValue() == null || pathText.getValue()
                            ?.let { it.length in 0..shortenedUrlPathMaxLength && it.all { char -> char.isAlphanumeric() } } == true
                    }

                    add(
                        ShortenedUrlDto::rickrollAllowed,
                        checkBox(label = "Add 40% rickroll chance")
                    )

                    hPanel(spacing = 10) {
                        button("Generate") {
                            onClick {
                                if (!this@formPanel.validate()) return@onClick
                                GlobalScope.launch {
                                    try {
                                        val generatedShortenedUrl = UrlShortenerServiceFrontend.insertShortenedUrl(this@formPanel.getData())
                                        SiteManager.redirectToUrl(UrlShortenerViewSingleShortenedLink(generatedShortenedUrl.path).devOrProdUrl())
                                    } catch (exception: ServiceException) {
                                        exception.showDefaultErrorToast("URL creation error")
                                    }
                                }
                            }
                        }
                    }
                }

                h3(content = "Get info for a shortened URL")
                formPanel<FindUrlShortenerInfoForm>(classes = nameSetOf(MarginMediumBottom.asString)) {
                    add(
                        FindUrlShortenerInfoForm::url,
                        text(type = TextInputType.URL, label = "Shortened link").withPlaceholderAndMaxWidth(75 to perc, "Enter URL here"),
                        required = true,
                        requiredMessage = "This field is required",
                        validatorMessage = { "Please enter a valid shortened URL." }
                    ) { urlText ->
                        urlText.getValue()?.let { Regex(".+/u/(.+)").matchEntire(it)?.groupValues?.getOrNull(1) } != null
                    }

                    hPanel(spacing = 10) {
                        button("Find") {
                            onClick {
                                if (!this@formPanel.validate()) return@onClick
                                GlobalScope.launch {
                                    try {
                                        val path = Regex(".+/u/(.+)").matchEntire(this@formPanel.getData().url!!)!!.groupValues[1]
                                        val shortenedUrl = UrlShortenerServiceFrontend.getShortenedUrl(path)
                                        SiteManager.redirectToUrl(UrlShortenerViewSingleShortenedLink(shortenedUrl.path).devOrProdUrl())
                                    } catch (exception: ServiceException) {
                                        this@formPanel.getControl(FindUrlShortenerInfoForm::url)!!.validatorError =
                                            "Please enter a valid shortened URL."
                                    }
                                }
                            }
                        }
                    }
                }
            }

            p {
                +"See "
                link(label = "all shortened links →", url = UrlShortenerViewAllShortenedLinks.devOrProdUrl(), classes = nameSetOf("link-color"))
            }

        }
    }
})

class UrlShortenerViewAllShortenedLinksComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
        h2(content = "URL Shortener", classes = nameSetOf(MarginRemoveBottom, TextCenter, "moderate-bold"))
        p(classes = nameSetOf(MarginSmallTop, MarginMediumBottom, TextCenter, "light")) {
            goBackToProjectHome()
            +" or "
            link(label = "go back →", url = UrlShortenerHomePage.devOrProdUrl(), classes = nameSetOf("link-color"))
            +" to the shortener homepage."
        }

        div(classes = nameSetOf(MarginAuto, MarginSmallBottom, WidthOneHalf)) {
            h5(content = "All shortened URLs", classes = nameSetOf(MarginMediumBottom, "light"))

            ul {
                GlobalScope.launch {
                    UrlShortenerServiceFrontend.getShortenedUrls().forEach { shortenedUrl ->
                        li {
                            link(
                                label = "/${shortenedUrl.path}",
                                url = UrlShortenerViewSingleShortenedLink(shortenedUrl.path).devOrProdUrl(),
                                classes = nameSetOf("link-color")
                            )
                            +": leads to "
                            link(label = shortenedUrl.url, url = shortenedUrl.url, classes = nameSetOf("link-color"))
                            +" (rickroll chance enabled: "
                            span(content = if (shortenedUrl.rickrollAllowed) "yes" else "no") {
                                style { textDecoration = TextDecoration(line = TextDecorationLine.UNDERLINE) }
                            }
                            +")"
                        }
                    }
                    removeLoadingSpinner(state)
                }
            }
        }
    }
})

class UrlShortenerViewSingleShortenedLinkComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    state.view as UrlShortenerViewSingleShortenedLink
    div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
        GlobalScope.launch {
            try {
                val shortenedUrl = UrlShortenerServiceFrontend.getShortenedUrl(state.view.path)
                val urlPath = UrlShortenerRedirectToShortenedLink(shortenedUrl.path).devOrProdUrl()
                h2(content = "URL Shortener - $urlPath", classes = nameSetOf(MarginRemoveBottom, TextCenter, "moderate-bold"))
                p(classes = nameSetOf(MarginSmallTop, MarginMediumBottom, TextCenter, "light")) {
                    goBackToProjectHome()
                    +" or "
                    link(label = "go back →", url = UrlShortenerHomePage.devOrProdUrl(), classes = nameSetOf("link-color"))
                    +" to the shortener homepage."
                }

                div(classes = nameSetOf(MarginAuto, MarginSmallBottom, WidthOneHalf)) {

                    h4(classes = nameSetOf(MarginMediumBottom, "light")) {
                        +"URL: "
                        link(label = "${SiteManager.domain}$urlPath", url = urlPath, classes = nameSetOf("link-color"))
                    }

                    div {
                        h3(classes = nameSetOf(MarginRemoveBottom, "moderate-bold")) {
                            +"Links to: "
                            link(label = shortenedUrl.url, url = shortenedUrl.url, classes = nameSetOf("link-color"))
                        }
                        h3(classes = nameSetOf(MarginRemoveBottom, MarginRemoveTop, "moderate-bold")) {
                            +"Path: "
                            link(label = urlPath, url = urlPath, classes = nameSetOf("link-color"))
                        }
                        h3(classes = nameSetOf(MarginRemoveTop, "moderate-bold")) {
                            +"Rickroll chance: "
                            if (shortenedUrl.rickrollAllowed) +"40%" else +"0%"
                        }
                    }
                }

                removeLoadingSpinner(state)
            } catch (exception: ServiceException) {
                // url wasn't found
                SiteManager.redirect(UrlShortenerHomePage)
            }
        }
    }
})

class UrlShortenerRedirectToShortenedLinkComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    state.view as UrlShortenerRedirectToShortenedLink
    GlobalScope.launch {
        try {
            val shortenedUrl = UrlShortenerServiceFrontend.getShortenedUrl(state.view.path)
            if (shortenedUrl.rickrollAllowed && Random.nextInt(10) in 0..3) SiteManager.redirectToUrl("https://www.dafk.net/what/")
            else SiteManager.redirectToUrl(shortenedUrl.url)
        } catch (exception: ServiceException) {
            // url wasn't found
            SiteManager.redirectToUrl(UrlShortenerHomePage.devOrProdUrl())
        }
    }
})


@Serializable
data class FindUrlShortenerInfoForm(
    val url: String? = null
)