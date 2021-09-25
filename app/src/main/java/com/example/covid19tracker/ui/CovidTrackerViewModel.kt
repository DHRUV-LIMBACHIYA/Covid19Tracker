package com.example.covid19tracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covid19tracker.Utils.Constants.MAX_DATE
import com.example.covid19tracker.Utils.ObservableString
import com.example.covid19tracker.Utils.Resource
import com.example.covid19tracker.network.remote.response.Countries
import com.example.covid19tracker.network.remote.response.CountriesItem
import com.example.covid19tracker.network.remote.response.CovidData
import com.example.covid19tracker.repository.CovidTrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 18-09-2021.
 */

@HiltViewModel
class CovidTrackerViewModel @Inject constructor(
    private val covidTrackerRepository: CovidTrackerRepository
) : ViewModel() {

    //country name observable for all api call.
    val observableCountryName = ObservableString()

    // LiveData for holding resource of countries.
    private var _countries = MutableLiveData<Resource<List<CountriesItem>>>()
    val countries: LiveData<Resource<List<CountriesItem>>> = _countries

    // LiveData for holding resource of covid data.
    private var _covidDataByCountry = MutableLiveData<Resource<List<CovidData>>>()
    val covidDataByCountry: LiveData<Resource<List<CovidData>>> = _covidDataByCountry

    // LiveData for holding resource of covid historical data.
    private var _covidHistoricalData = MutableLiveData<Resource<List<CovidData>>>()
    val covidHistoricalData: LiveData<Resource<List<CovidData>>> = _covidHistoricalData

    init {
        getAllCountriesFromRepo()
    }

    /**
     * Get all the countries from the repository
     */
    fun getAllCountriesFromRepo() = viewModelScope.launch {
       _countries.value = covidTrackerRepository.getCountriesFromApi()
    }

    /**
     * Get Covid Data country wise.
     */
    fun getCovidDataByCountryFromRepo(countryName: String) = viewModelScope.launch {
        _covidDataByCountry.value = covidTrackerRepository.getCovidDataByCountryFromApi(countryName)
    }

    /**
     * Get Covid Historical data.
     */
    fun getCovidHistoricalDataFromRepo(countryName: String,fromDate: String = "",toDate: String = MAX_DATE) = viewModelScope.launch {
        _covidHistoricalData.value = covidTrackerRepository.getCovidDataHistoricalDataFromApi(countryName,fromDate,toDate)
    }
}