package com.adamratzman.models

import com.adamratzman.utils.getRandomColor

data class Project(val name: String, val codeCategories: String, val description: String, val url: String) {
    val color: String get() = getRandomColor().asString()
}

fun getPastProjects() = listOf(
        Project(
                "AP Calculus Review Site",
                "Handlebars, CSS, JavaScript, Kotlin, LaTEX",
                "A comprehensive review site for calculus theorems, derivatives, and integrals",
                "http://ap-calculus-review-sohalski.herokuapp.com"
        ),
        Project(
                "Project Euler Kotlin", "Competitions, Kotlin", "Solutions to the first ~60 Project Euler problems, all " +
                "implemented with Kotlin", "https://github.com/adamint/project-euler-kotlin"
        ),
        Project(
                "Ardent",
                "Discord, Website, Bot, Kotlin",
                "(Discontinued) An open-source, fast, stylish, and modern Discord game engine & administration tool",
                "https://github.com/ArdentDevelopers/Ardent-2018"
        ),
        Project(
                "Octagonapp", "Aggregator, API, Kotlin", "(Discontinued) A news aggregator and consumable REST API.",
                "https://github.com/adamint/octagonapp"
        )
)

fun getPresentProjects() = listOf(
        Project(
                "Spotify Kotlin Wrapper", "HTTP, API, Kotlin", "A wrapper around the Spotify Web API, written in Kotlin.",
                "https://github.com/adamint/spotify-web-api-kotlin"
        ),
        Project(
                "adamratzman.com", "Handlebars, CSS, Kotlin, JavaScript",
                "This site. I'm currently working on updating it with useful interactive projects!",
                "https://github.com/adamint/adamratzman.com"
        )
)

data class InteractiveProject(val name: String, val description: String, val url: String, val category: ProjectCategory) {
        val color: String get() = getRandomColor().asString()
}

enum class ProjectCategory {
        POLITICS,
        UTILITIES,
        SCHOOL,
        SPOTIFY;

        override fun toString() = name.toLowerCase()
}

fun getInteractives() = listOf(
        InteractiveProject(
                "French Resources & Learning Tools",
                "Some useful links & applications to help you learn french",
                "/projects/french",
                ProjectCategory.SCHOOL
        ),
        InteractiveProject(
                "Arbitrary Precision Calculator",
                "For when you need <b>really</b> big numbers",
                "/projects/calculator",
                ProjectCategory.UTILITIES
        ),
        InteractiveProject(
                "URL Shortener (Rick Astley edition)",
                "URL Shortener.. with a twist",
                "/shortener",
                ProjectCategory.UTILITIES
        ),
        InteractiveProject(
                "Base Converter",
                "Easily convert between different base systems",
                "/projects/conversion/base-converter",
                ProjectCategory.UTILITIES
        ),
        InteractiveProject(
                "AP Calculus Interactive Review Site",
                "A comprehensive review site for calculus theorems, derivatives, and integrals, with options to " +
                        "test yourself with randomly-generated relevant problems",
                "http://ap-calculus-review-sohalski.herokuapp.com",
                ProjectCategory.SCHOOL
        ),
        InteractiveProject(
                "Spotify Playlist Creator",
                "Allows you to create a Spotify playlist specifically tailored to your tastes",
                "/projects/spotify/recommend",
                ProjectCategory.SPOTIFY
        ),
        InteractiveProject(
                "Spotify Genre List",
                "See all available Spotify genres",
                "/projects/spotify/genres/list",
                ProjectCategory.SPOTIFY
        )
).toMutableSet()
