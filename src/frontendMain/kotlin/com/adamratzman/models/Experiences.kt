package com.adamratzman.models

data class ResumeExperienceWrapper(
        val name: String,
        val location: String,
        val start: String?,
        val end: String?,
        val experiences: List<ResumeExperience> = listOf(),
        val locationUrl: String? = null,
        val secondLineFloatedRight: String? = null,
        val incoming: Boolean = false,
        val incomingLabel: String? = null
)

data class ResumeExperience(
        val name: String,
        val bullets: List<String>?,
        val period: String? = null,
        val nameTooltip: String? = null
)

fun getEducationExperience() = listOf(
        ResumeExperienceWrapper(
                "Indiana University Bloomington",
                "Bloomington, IN",
                "August 2021",
                "May 2022",
                listOf(
                        ResumeExperience(
                                "MS in Computer Science",
                                null
                        )
                )
        ),
        ResumeExperienceWrapper(
                "Indiana University Bloomington",
                "Bloomington, IN",
                "August 2019",
                "May 2021",
                listOf(
                        ResumeExperience(
                                "Bachelor of Science in Computer Science",
                                listOf(
                                        "Honors: Hudson & Holland Scholar, Founders Scholar, Provost’s Scholarship, Hutton Honors College.",
                                        "Part of the BSMS Computer Science program."
                                )
                        )
                ),
                secondLineFloatedRight = "GPA: <u>3.94/4.00</u>"
        )
)

fun getWorkExperience() = mutableListOf(
        ResumeExperienceWrapper(
                "Crane Naval Surface Warfare Center",
                "Crane, IN",
                "August 2020",
                null,
                listOf(
                        ResumeExperience(
                                "Software Engineer Intern",
                                listOf("Cleared role.")
                        )
                ),
                incoming = false,
        ),
        ResumeExperienceWrapper(
                "Microsoft Corporation",
                "Redmond, WA (Remote)",
                "May 2020",
                null,
                listOf(
                        ResumeExperience(
                                "Software Engineer Intern (SWE), Microsoft Azure Service Fabric Atlas",
                                listOf(
                                        "Developed a POC extension to the Atlas RP (formerly Azure Service Fabric Mesh) layer in Microsoft Azure Service Fabric",
                                        "Collaborated in a remote environment to meet deadline"
                                ),
                                nameTooltip = "Because of the covid-19 pandemic, this internship was completely remote."
                        )
                )
        ),
        ResumeExperienceWrapper(
                "Indiana University Bloomington",
                "Bloomington, IN",
                "November 2019",
                "May 2020",
                listOf(
                        ResumeExperience(
                                "Undergraduate Instructor, CSCI-C322 Object-Oriented Software Systems, Pf. Dan-Adrian German",
                                listOf(
                                        "Instructed in the C322 upper-level computer science course due to demonstrated excellence in Java and software development"
                                )
                        )
                )
        ),
        ResumeExperienceWrapper(
                "E-gineering",
                "Indianapolis, IN",
                null,
                null,
                listOf(
                        ResumeExperience(
                                "Software Engineering Intern",
                                listOf(
                                        "Collaborated with Product Owner to identify and implement website enhancements",
                                        "Implemented automated email and phone notifications for users",
                                        "Dockerized application for simplified local development",
                                        "Found and eliminated technical debt and legacy security vulnerabilities"
                                ),
                                "June 2019 - August 2019"
                        ),
                        ResumeExperience(
                                "DevOps Intern",
                                listOf(
                                        "Dockerized client’s Java-based applications",
                                        "Deployed client applications on POC Kubernetes clusters and presented solutions " +
                                                "to meet client’s goal of eliminating physical data centers"
                                ),
                                "May 2018 - August 2018"
                        )
                )
        ),
        ResumeExperienceWrapper(
                "Chick-Fil-A",
                "Westfield, IN",
                "November 2017",
                "May 2018",
                listOf(
                        ResumeExperience(
                                "Team Member",
                                null
                        )
                )
        )
)

fun getStudentInvolvement() = mutableListOf(
        ResumeExperienceWrapper(
                "Indiana University Student Body Congress",
                "Bloomington, IN",
                "July 2020",
                null,
                listOf(
                        ResumeExperience(
                                "Chief Technology Officer",
                                listOf("Developing Ktor web applications for IUSG")
                        )
                )
        ),
        ResumeExperienceWrapper(
                "Indiana University Student Body Congress",
                "Bloomington, IN",
                "October 2019",
                "May 2020",
                listOf(
                        ResumeExperience(
                                "Congressional Representative, School of Informatics, Computing, and Engineering",
                                listOf(
                                        "Advocated for issues important to the School of Informatics, Computing, and Engineering",
                                        "Served on the standing Education Committee with the goal of increasing cohesion between IUSG and Indiana University"
                                )
                        )
                )
        )
)

fun getSelectedProjects() = mutableListOf(
        ResumeExperienceWrapper(
                "AP Calculus (Calculus I) Review Site",
                "Carmel, IN",
                "May 2019",
                "June 2019",
                listOf(
                        ResumeExperience(
                                "Creator",
                                listOf(
                                        "Created a comprehensive review site for calculus theorems, derivatives, and integrals, with options to " +
                                                "test yourself with randomly-generated relevant problems"
                                )
                        )
                ),
                locationUrl = "https://ap-calculus-review-sohalski.herokuapp.com"
        ),
        ResumeExperienceWrapper(
                "Spotify Web API - Kotlin",
                "Github",
                "September 2017",
                null,
                listOf(
                        ResumeExperience(
                                "Creator, Maintainer",
                                listOf(
                                        "Created a modern, asynchronous Kotlin MPP library for the Spotify Web, Web Playback, and Auth APIs with 90+ stars",
                                        "Developed using TDD and Agile methods"
                                )
                        )
                ),
                locationUrl = "https://github.com/adamint/spotify-web-api-kotlin"
        ),
        ResumeExperienceWrapper(
                "Ardent",
                "Indianapolis, IN",
                "January 2015",
                "May 2018",
                listOf(
                        ResumeExperience(
                                "Founder",
                                listOf(
                                        "Developed a popular, internationalized, comprehensive Discord bot",
                                        "Directed a distributed team of 2 developers and 9 translators localizing into 11 languages",
                                        "Oversaw consistent monthly user and revenue growth, with profitability within 3 months of launch"
                                )
                        )
                )
        )
)