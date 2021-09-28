package com.example.covid19tracker.repository

import com.example.covid19tracker.Utils.Resource
import com.example.covid19tracker.network.remote.api.CovidTrackerService
import com.example.covid19tracker.network.remote.response.Countries
import com.example.covid19tracker.network.remote.response.CountriesItem
import com.example.covid19tracker.network.remote.response.CovidData
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 18-09-2021.
 */

@ActivityScoped
class CovidTrackerRepository @Inject constructor(
    private val covidAPI: CovidTrackerService
) {
    /**
     * Fetch Covid data from a specific date.
     */
    suspend fun getCovidDataHistoricalDataFromApi(
        countryName: String,
        from: String,
        to: String,
    ): List<CovidData>? = covidAPI.getAllCovidHistoricalData(countryName, from, to)

    /**
     * Fetch all the countries from the API.
     */
    suspend fun getCountriesFromApi(): List<CountriesItem> = covidAPI.getCountries()
}