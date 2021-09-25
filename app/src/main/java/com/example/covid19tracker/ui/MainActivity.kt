package com.example.covid19tracker.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.covid19tracker.R
import com.example.covid19tracker.Utils.*
import com.example.covid19tracker.network.remote.response.CountriesItem
import com.example.covid19tracker.network.remote.response.CovidData
import com.example.covid19tracker.ui.adapter.CovidTrackerSparkAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var numOfCases: Int = 0
    lateinit var lastCovidData: CovidData
    lateinit var mBinding: com.example.covid19tracker.databinding.ActivityMainBinding

    private val mViewModel: CovidTrackerViewModel by viewModels()

    @Inject
    lateinit var mCovidAdapter: CovidTrackerSparkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        observeLiveData()
        eventListeners()
    }

    /**
     * Function responsible for listening events.
     */
    private fun eventListeners() {
        mBinding.rgMetricSelection.setOnCheckedChangeListener { radioGroup, id ->
            mCovidAdapter.metric = when (id) {
                R.id.rd_confirmed -> {
                    numOfCases = lastCovidData.Confirmed
                    Metric.CONFIRMED
                }
                R.id.rd_recoverd -> {
                    numOfCases = lastCovidData.Recovered
                    Metric.RECOVERED
                }
                R.id.rd_deaths -> {
                    numOfCases = lastCovidData.Deaths
                    Metric.DEATH
                }
                else -> {
                    numOfCases = lastCovidData.Confirmed
                    Metric.RECOVERED
                }
            }

            // Format the covid numbers and display in ticker textview
            mBinding.tickerViewNumbers.text = NumberFormat.getInstance().format(numOfCases)

            mCovidAdapter.notifyDataSetChanged()
        }

        mBinding.rgTimeSelection.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.rd_max -> updateCovidDataByTimeScale(TimeScale.MAX)
                R.id.rd_one_month -> updateCovidDataByTimeScale(TimeScale.MONTH)
                R.id.rd_one_week -> updateCovidDataByTimeScale(TimeScale.WEEK)
                else -> TimeScale.MAX
            }
        }
    }

    private fun updateCovidDataByTimeScale(timescale: TimeScale) {
        when (timescale) {
            TimeScale.MAX -> mViewModel.getCovidHistoricalDataFromRepo(mViewModel.observableCountryName.trimmed)
            TimeScale.MONTH -> mViewModel.getCovidHistoricalDataFromRepo(
                mViewModel.observableCountryName.trimmed,
                fromDate = getCurrentDate(),
                toDate = getDayAgo(30)
            )
            TimeScale.WEEK -> mViewModel.getCovidHistoricalDataFromRepo(
                mViewModel.observableCountryName.trimmed,
                fromDate = getCurrentDate(),
                toDate = getDayAgo(7)
            )
        }
    }

    /**
     * Observe the changes in the LiveData.
     */
    private fun observeLiveData() {
        mViewModel.countries.observe(this) { countryResource ->
            when (countryResource) {
                is Resource.Success -> {
                    countryResource.data?.let {
                        setDataSourceToSpinner(countryResource.data.sortedBy { it.Country })
                    }
                }
                is Resource.Loading -> {
                    // Todo : Show progressbar.
                }
                is Resource.Error -> {
                    // Todo : Show error.
                }
            }
        }

        mViewModel.covidHistoricalData.observe(this) { covidData ->
            when (covidData) {
                is Resource.Success -> {
                    covidData.data?.let {
                        displaySparkLine(covidData.data)
                    }
                }
                is Resource.Loading -> {
                    // Todo : Show progressbar.
                }
                is Resource.Error -> {
                    // Todo : Show error.
                }
            }
        }
    }

    /**
     * Display a spark line using covid data.
     */
    private fun displaySparkLine(covidData: List<CovidData>?) {
        mCovidAdapter.setCovidData(covidData!!.toMutableList())
        mBinding.sparkview.adapter = mCovidAdapter
        mCovidAdapter.notifyDataSetChanged()

        if(covidData.isNotEmpty()) {
            lastCovidData = covidData.last() // Last(Recent) covid data.
            updateDate(lastCovidData)
        } else {
            mBinding.tvDate.text = "No record"
        }
    }

    /**
     * Function will display recent date.
     */
    private fun updateDate(lastData: CovidData) {
        mBinding.tvDate.text = convertDateStringIntoString(lastData.Date)
    }

    /**
     * Set data in spinner
     */
    private fun setDataSourceToSpinner(data: List<CountriesItem>) {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spinnerCountry.adapter = arrayAdapter

        mBinding.spinnerCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    v: View?,
                    position: Int,
                    id: Long
                ) {
                    val currentCountry = data[position].Country // Currently selected country
                    mViewModel.observableCountryName.set(currentCountry)
                    mViewModel.getCovidHistoricalDataFromRepo(currentCountry)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // NO OP
                }
            }
    }
}