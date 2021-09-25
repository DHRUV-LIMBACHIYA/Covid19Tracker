package com.example.covid19tracker.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.covid19tracker.R
import com.example.covid19tracker.Utils.Resource
import com.example.covid19tracker.network.remote.response.Countries
import com.example.covid19tracker.network.remote.response.CountriesItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var mBinding: com.example.covid19tracker.databinding.ActivityMainBinding
    private val mViewModel: CovidTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        observeLiveData()
    }

    private fun observeLiveData() {
        mViewModel.countries.observe(this) { countryResource ->
            when(countryResource) {
                is Resource.Success -> {
                    countryResource.data?.let {
                        setDataSourceToSpinner(countryResource.data.sortedBy { it.Country })
                    }
                }
            }
        }
    }

    private fun setDataSourceToSpinner(data: List<CountriesItem>) {
        mBinding.spinnerCountry.attachDataSource(data)
        mBinding.spinnerCountry.setOnSpinnerItemSelectedListener { _, _, position, _ ->
            mViewModel.getCovidDataByCountryFromRepo(data[position].Slug)
        }
    }
}