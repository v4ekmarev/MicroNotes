package com.develop.micronotes

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform