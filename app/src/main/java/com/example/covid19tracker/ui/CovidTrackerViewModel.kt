package com.example.covid19tracker.ui

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covid19tracker.Utils.Constants.MAX_DATE
import com.example.covid19tracker.Utils.ObservableString
import com.example.covid19tracker.Utils.Resource
import com.example.covid19tracker.network.remote.response.CountriesItem
import com.example.covid19tracker.network.remote.response.CovidData
import com.example.covid19tracker.repository.CovidTrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private var _countriesResponse = MutableLiveData<Resource<List<CountriesItem>>>()
    val countriesResponse: LiveData<Resource<List<CountriesItem>>> = _countriesResponse

    // LiveData for holding resource of covid historical data.
    private var _covidHistoricalDataResponse = MutableLiveData<Resource<List<CovidData>>>()
    val covidHistoricalData: LiveData<Resource<List<CovidData>>> = _covidHistoricalDataResponse

    init {
        getAllCountriesFromRepo()
    }

    /**
     * Get all the countries from the repository
     */
    private fun getAllCountriesFromRepo() = viewModelScope.launch {
        _countriesResponse.postValue(Resource.Loading())
        try {
            val response = covidTrackerRepository.getCountriesFromApi()
            _countriesResponse.postValue(Resource.Success(response))
        } catch (e: Exception) {
            _countriesResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    /**
     * Get Covid Historical data.
     */
    fun getCovidHistoricalDataFromRepo(
        countryName: String,
        fromDate: String = "",
        toDate: String = MAX_DATE
    ) {
        _covidHistoricalDataResponse.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = covidTrackerRepository.getCovidDataHistoricalDataFromApi(
                    countryName,
                    fromDate,
                    toDate
                )
                if (response != null && response.isNotEmpty()) {
                    _covidHistoricalDataResponse.postValue(Resource.Success(response))
                } else {
                    _covidHistoricalDataResponse.postValue(Resource.Error("An unknown error occured!"))
                }

            } catch (e: Exception) {
                _covidHistoricalDataResponse.postValue(Resource.Error(e.message.toString()))
            }
        }
    }
}