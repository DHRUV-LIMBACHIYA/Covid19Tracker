package com.example.covid19tracker.network.remote.api

import com.example.covid19tracker.network.remote.response.Countries
import com.example.covid19tracker.network.remote.response.CovidData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Dhruv Limbachiya on 14-09-2021.
 */

interface CovidTrackerService {

    @GET("country/{country_name}")
    suspend fun getCovidDataByCountry(
        @Path("country_name") countryName : String
    ) : List<CovidData>?

    @GET("country/{country_name}")
    suspend fun getAllCovidHistoricalData(
        @Path("country_name") countryName : String,
        @Query("from") fromDate : String,
        @Query("to") toDate : String,
    ) : List<CovidData>?

    @GET("countries")
    suspend fun getCountries() : List<Countries>


}