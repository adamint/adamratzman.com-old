package com.adamratzman.layouts.projects.conversion

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.services.BaseConverterServiceFrontend
import com.adamratzman.utils.*
import com.adamratzman.utils.UikitName.*
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.form.formPanel
import pl.treksoft.kvision.form.select.select
import pl.treksoft.kvision.form.text.text
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.hPanel
import pl.treksoft.kvision.remote.ServiceException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class BaseConversionComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
        h2(content = "Base Conversion Tool", classes = nameSetOf(MarginRemoveBottom, TextCenter, "moderate-bold"))
        p(classes = nameSetOf(MarginSmallTop, MarginMediumBottom, TextCenter, "light")) {
            goBackToProjectHome()
        }

        div(classes = nameSetOf(MarginAuto, MarginSmallBottom, WidthTwoThirds)) {
            h3(content = "I want to convert...", classes = nameSetOf(MarginMediumBottom.asString))

            div {
                addBootstrap()
                GlobalScope.launch {
                    delay(2000)
                    val range = BaseConverterServiceFrontend.getBasesRange()
                    lateinit var outputElement: Tag
                    formPanel<BaseConverterForm>(classes = nameSetOf(MarginMediumBottom.asString)) {
                        add(
                            BaseConverterForm::numberAsString,
                            text(label = "Enter number..").apply { placeholder = "Number" },
                            required = true,
                            requiredMessage = "This field is required",
                            validatorMessage = { "Please enter a valid number" }
                        ) { num -> num.getValue()?.let { it.isNotBlank() && it.matches("[a-zA-Z0-9.,]+") } ?: false }

                        add(
                            BaseConverterForm::baseFrom,
                            select(
                                value = "Select base",
                                label = "From base:",
                                options = "Select base".asDuplicatedPair() plusList (range.low..range.high).map { it.toString() to it.toString() }
                            ),
                            required = true,
                            requiredMessage = "This field is required",
                            validatorMessage = { "Please enter a valid number" }
                        ) { num -> num.getValue()?.toIntOrNull()?.let { it in range.low..range.high } ?: false }

                        add(
                            BaseConverterForm::baseTo,
                            select(
                                value = "Select base",
                                label = "To base:",
                                options = "Select base".asDuplicatedPair() plusList (range.low..range.high).map { it.toString() to it.toString() }
                            ),
                            required = true,
                            requiredMessage = "This field is required",
                            validatorMessage = { "Please enter a valid number" }
                        ) { num -> num.getValue()?.toIntOrNull()?.let { it in range.low..range.high } ?: false }

                        hPanel(spacing = 10) {
                            button("Submit") {
                                onClick {
                                    if (!this@formPanel.validate()) return@onClick
                                    val data = this@formPanel.getData()
                                    val baseFrom = data.baseFrom!!.toInt()
                                    val baseTo = data.baseTo!!.toInt()
                                    val numberAsString = data.numberAsString!!

                                    GlobalScope.launch {
                                        try {
                                            val convertedNumber = BaseConverterServiceFrontend.convert(baseFrom, baseTo, numberAsString)
                                            outputElement.apply {
                                                removeAll()
                                                bold(content = "Result: ")
                                                span(convertedNumber)
                                            }
                                        } catch (exception: ServiceException) {
                                            exception.showDefaultErrorToast("Conversion Error")
                                        }
                                    }
                                }
                            }
                            button("Inverse to/from", style = ButtonStyle.LIGHT) {
                                onClick {
                                    val data = this@formPanel.getData()
                                    this@formPanel.setData(data.copy(baseFrom = data.baseTo, baseTo = data.baseFrom))
                                }
                            }
                        }
                    }
                    outputElement = h5 {}
                    removeLoadingSpinner(state)
                }
            }
        }
    }
})

@Serializable
data class BaseConverterForm(
    val baseFrom: String? = null,
    val baseTo: String? = null,
    val numberAsString: String? = null
)