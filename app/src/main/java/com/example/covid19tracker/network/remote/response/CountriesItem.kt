package com.example.covid19tracker.network.remote.response

data class CountriesItem(
    val Country: String,
    val ISO2: String,
    val Slug: String
) {
    override fun toString(): String {
        return Country
    }
}