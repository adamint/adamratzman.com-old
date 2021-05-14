package com.adamratzman.layouts

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.*
import com.adamratzman.database.isDevServer
import com.adamratzman.services.*
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addCssClasses
import com.adamratzman.utils.getSearchParams
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.showDefaultErrorToast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import io.kvision.core.Container
import io.kvision.core.UNIT.perc
import io.kvision.core.UNIT.rem
import io.kvision.core.style
import io.kvision.form.FormMethod
import io.kvision.form.formPanel
import io.kvision.form.text.Text
import io.kvision.form.text.TextInputType
import io.kvision.html.*
import io.kvision.remote.Credentials
import io.kvision.remote.ServiceException

class LoginComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = {
    div(classes = nameSetOf(MarginAuto, WidthTwoThirds)) {
        div(classes = nameSetOf(MarginMediumTop)) {

            h2(classes = nameSetOf("moderate-bold", MarginRemoveBottom)) {
                style { fontSize = 2.5 to rem }
                +"Log in"
            }
            p {
                +"Need to create an account? "
                link(label = "Register →", url = RegisterPage.devOrProdUrl(), className = "link-color")
            }

            formPanel<Credentials>(className = MarginMediumTop.asString, method = FormMethod.POST, action = "/login") {
                val usernameComponent = Text(label = "Username", name = "username").apply {
                    style { width = 80 to perc }
                    addCssClasses(UkInline, MarginSmallBottom)
                    placeholder = "Enter username.."
                    input.addCssClasses(UkInput, UkInline)
                }

                val passwordComponent = Text(label = "Password", name = "password", type = TextInputType.PASSWORD).apply {
                    style { width = 80 to perc }
                    addCssClasses(UkInline, MarginMediumBottom)
                    placeholder = "Enter password.."
                    input.addCssClasses(UkInput, UkInline)
                }

                div {
                    add(
                        Credentials::username,
                        usernameComponent,
                        required = true,
                        requiredMessage = "You must enter a username.",
                        validatorMessage = { usernameRequirementValidationErrorMessage }
                    ) { it.getValue()?.let { username -> doesUsernameMeetRequirements(username) } ?: false }

                    add(
                        Credentials::password,
                        passwordComponent,
                        required = true,
                        requiredMessage = "You must enter a password.",
                        validatorMessage = { "You must enter a password." }
                    ) { it.getValue() != null }
                }

                button("Log in", classes = nameSetOf("uk-button")) {
                    onClick {
                        disabled = true
                        if (!validate()) {
                            disabled = false
                            form.clearData()
                            return@onClick
                        }
                        val formJQuery: dynamic = this@formPanel.getElementJQuery() ?: return@onClick

                        if (!isDevServer) {
                            formJQuery.submit()
                            return@onClick
                        }
                        else {
                            GlobalScope.launch {
                                try {
                                    val data = getData()
                                    if (AuthenticationServiceFrontend.login(data.username!!, data.password!!)) {
                                        logInClientSide()
                                    }
                                } catch (exception: ServiceException) {
                                    exception.showDefaultErrorToast()
                                }
                            }
                        }
                    }
                }

                if (getSearchParams().has("error")) {
                    usernameComponent.validatorError = "Invalid username or password supplied."
                    passwordComponent.validatorError = "Invalid username or password supplied."
                }

            }
        }

    }
})

class RegisterComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginAuto, WidthTwoThirds)) {
        div(classes = nameSetOf(MarginMediumTop)) {

            h2(classes = nameSetOf("moderate-bold", MarginRemoveBottom)) {
                style { fontSize = 2.5 to rem }
                +"Register"
            }
            p {
                +"Creating an account lets you comment on blog posts and will let you save project content."
            }

            formPanel<Credentials>(className = MarginMediumTop.asString) {
                val usernameComponent = Text(label = "Username", name = "username").apply {
                    style { width = 80 to perc }
                    addCssClasses(UkInline, MarginSmallBottom)
                    placeholder = "Enter the username you'd like.."
                    input.addCssClasses(UkInput, UkInline)
                }

                val passwordComponent = Text(label = "Password", name = "password", type = TextInputType.PASSWORD).apply {
                    style { width = 80 to perc }
                    addCssClasses(UkInline, MarginMediumBottom)
                    placeholder = "Enter the password you'd like.."
                    input.addCssClasses(UkInput, UkInline)
                }

                div {
                    add(
                        Credentials::username,
                        usernameComponent,
                        required = true,
                        requiredMessage = "You must enter a username.",
                        validatorMessage = { usernameRequirementValidationErrorMessage }
                    ) { it.getValue()?.let { username -> doesUsernameMeetRequirements(username) } ?: false }

                    add(
                        Credentials::password,
                        passwordComponent,
                        required = true,
                        requiredMessage = "You must enter a password.",
                        validatorMessage = { passwordRequirementValidationErrorMessage }
                    ) { it.getValue()?.let { username -> doesPasswordMeetRequirements(username) } ?: false }
                }

                button("Register", classes = nameSetOf("uk-button")) {
                    onClick {
                        disabled = true
                        if (!validate()) {
                            disabled = false
                            form.clearData()
                            return@onClick
                        }
                        val credentials = form.getData()

                        GlobalScope.launch {
                            try {
                                if (AuthenticationServiceFrontend.register(credentials.username!!, credentials.password!!)) {
                                    val clientSideData = AuthenticationServiceFrontend.getClientSideData()
                                    state.clientSideData = clientSideData
                                    SiteManager.redirectToUrl(ProfilePage.devOrProdUrl())
                                }
                            } catch (exception: ServiceException) {
                                disabled = false
                                form.clearData()
                                usernameComponent.validatorError = exception.message
                                passwordComponent.validatorError = exception.message
                            }
                        }
                    }
                }
            }
        }

    }
})

fun logInClientSide() {
    GlobalScope.launch {
        try {
            val clientSideData = AuthenticationServiceFrontend.getClientSideData()
            SiteManager.siteStore.getState().clientSideData = clientSideData
            SiteManager.redirectBack(defaultUrl = ProfilePage.devOrProdUrl())
            SiteManager.redirectToUrl(ProfilePage.devOrProdUrl())
        } catch (exception: ServiceException) {
            SiteManager.redirectToUrl(LoginPage.devOrProdUrl())
        }
    }
}