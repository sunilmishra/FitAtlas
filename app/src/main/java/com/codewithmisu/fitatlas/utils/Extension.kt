package com.codewithmisu.fitatlas.utils

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}