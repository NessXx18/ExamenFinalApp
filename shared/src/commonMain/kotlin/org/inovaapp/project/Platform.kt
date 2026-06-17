package org.inovaapp.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform