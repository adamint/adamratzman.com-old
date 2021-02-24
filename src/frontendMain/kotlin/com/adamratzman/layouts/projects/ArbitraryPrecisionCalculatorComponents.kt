package com.adamratzman.layouts.projects

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.services.CalculatorServiceFrontend
import com.adamratzman.services.defaultCalculatorPrecision
import com.adamratzman.utils.*
import com.adamratzman.utils.UikitName.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.core.FontStyle.ITALIC
import pl.treksoft.kvision.core.UNIT.perc
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.form.check.checkBox
import pl.treksoft.kvision.form.formPanel
import pl.treksoft.kvision.form.spinner.spinner
import pl.treksoft.kvision.form.text.text
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.modal.Modal
import pl.treksoft.kvision.remote.ServiceException

class ArbitraryPrecisionCalculatorComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
        h2(content = "Arbitrary* Precision Calculator", classes = nameSetOf(MarginRemoveBottom, TextCenter, "moderate-bold"))
        p(classes = nameSetOf(MarginSmallTop, MarginSmallBottom, TextCenter, "light")) {
            goBackToProjectHome()
        }
        GlobalScope.launch {
            val initialRadix = 10
            val constants = CalculatorServiceFrontend.getCalculatorConstants(defaultCalculatorPrecision, initialRadix)
            val functionsGroups = CalculatorServiceFrontend.getCalculatorFunctions()

            p(classes = nameSetOf(MarginSmallTop, MarginMediumBottom, TextCenter, "light")) {
                +"See what "
                link(label = "functions", classes = nameSetOf("bold", "link-color")) {
                    onClick {
                        val modal = Modal("Available Functions")
                        functionsGroups.keys.sortedBy { it.name }.forEach { group ->
                            val functions = functionsGroups.getValue(group)
                            modal.add(h4(content = group.name, classes = nameSetOf("light")))
                            modal.add(ul {
                                functions.forEach { function ->
                                    li {
                                        bold {
                                            +function.token
                                            if (function.aliases.isNotEmpty()) +" [${function.aliases.joinToString(", ")}]"
                                        }
                                        function.functionOverloadStrings.forEach { overload ->
                                            addLineBreak()
                                            +"${function.token}($overload)"
                                        }
                                    }
                                }
                            })
                        }

                        modal.addButton(Button("Close").apply {
                            onClick { modal.hide() }
                        })
                        modal.show()
                    }
                }
                +" and "
                link(label = "constants", classes = nameSetOf("bold", "link-color")) {
                    onClick {
                        val modal = Modal("Available Constants")
                        modal.add(ul {
                            constants.forEach { constant ->
                                li {
                                    bold(constant.name)
                                    +": ${constant.value}"
                                }
                            }
                        })
                        modal.add(p {
                            +"Precision is calculated based on the optional "
                            span(content = "precision") {
                                style { fontStyle = ITALIC }
                            }
                            +" option (default is $defaultCalculatorPrecision digits)"
                        })
                        modal.addButton(Button("Close").apply {
                            onClick { modal.hide() }
                        })
                        modal.show()
                    }
                }
                +" are available."
            }

            div(classes = nameSetOf(MarginAuto, MarginSmallBottom, WidthOneHalf)) {
                var results = 0
                val computationResults = Div {
                    h3(content = "Enter an expression below..", classes = nameSetOf("light"))
                }
                val output = div(classes = nameSetOf(MarginAuto, MarginSmallBottom, HeightMedium, "overflow-scroll-y")) {
                    style {
                        border = Border(1 to px, BorderStyle.DOTTED, Color.name(Col.BLACK))
                    }
                    add(computationResults)
                }


                div {
                    addBootstrap()
                    formPanel<CalculatorParametersForm>(classes = nameSetOf(MarginMediumBottom.asString)) {
                        add(
                            CalculatorParametersForm::input,
                            text(label = "Input").withPlaceholderAndMaxWidth(100 to perc, "Type a math expression here.."),
                            required = true,
                            requiredMessage = "Expression input is required.",
                            validatorMessage = { "Please enter a math expression." }
                        ) { input -> input.getValue()?.isNotBlank() == true }

                        button("Calculate", classes = nameSetOf(MarginSmallBottom.asString)) {
                            onClick {
                                if (this@formPanel.validate()) {
                                    val data = this@formPanel.getData()
                                    GlobalScope.launch {
                                        try {
                                            val input = data.input!!
                                            val result =
                                                CalculatorServiceFrontend.calculate(input, data.precision, data.radix, data.useRadians)
                                            if (results == 0) computationResults.removeAll()
                                            results++
                                            output.h4(classes = nameSetOf(MarginRemoveTop, MarginSmallBottom, "light")) {
                                                +"$input: $result"
                                                if (data.radix != 10) +" (base ${data.radix})"
                                            }
                                        } catch (exception: ServiceException) {
                                            exception.showDefaultErrorToast("Error evaluating expression")
                                        }
                                    }
                                }
                            }
                        }

                        add(
                            CalculatorParametersForm::radix,
                            spinner(label = "Result radix (Base)", value = 10, min = 2, max = 36) {
                                style { width = 100 to px }
                            }
                        )

                        add(
                            CalculatorParametersForm::precision,
                            spinner(label = "Precision", min = 1, max = 1000) {
                                style {
                                    width = 100 to px
                                    placeholder = defaultCalculatorPrecision.toString()
                                }
                            }
                        )

                        add(
                            CalculatorParametersForm::useRadians,
                            checkBox(label = "Use Radians?").apply { removeAbcCheckbox() }
                        )
                    }
                }
            }

            removeLoadingSpinner(state)
        }
    }
})

@Serializable
data class CalculatorParametersForm(
    val input: String? = null,
    val precision: Int = defaultCalculatorPrecision,
    val radix: Int = 10,
    val useRadians: Boolean = false
)