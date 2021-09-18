package com.example.covid19tracker.repository

import com.example.covid19tracker.Utils.Resource
import com.example.covid19tracker.network.remote.api.CovidTrackerService
import com.example.covid19tracker.network.remote.response.Countries
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
     * Fetch Covid data by country name.
     */
    suspend fun getCovidDataByCountryFromApi(countryName: String): Resource<List<CovidData>> {
        return try {
            val response = covidAPI.getCovidDataByCountry(countryName)
            response?.let {
                return Resource.Success(it)
            }
            return Resource.Error("An unknown error occurred!")
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    /**
     * Fetch Covid data from a specific date.
     */
    suspend fun getCovidDataHistoricalDataFromApi(
        countryName: String,
        from: String,
        to: String,
    ): Resource<List<CovidData>> = try {
        val response = covidAPI.getAllCovidHistoricalData(countryName, from, to)
        response?.let {
            return Resource.Success(it)
        }
        Resource.Error("An unknown error occurred!")
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    /**
     * Fetch all the countries from the API.
     */
    suspend fun getCountriesFromApi(): Resource<List<Countries>> = try {
        val response = covidAPI.getCountries()
        Resource.Success(response)
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }
}