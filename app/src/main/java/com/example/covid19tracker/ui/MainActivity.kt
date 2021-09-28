package com.example.covid19tracker.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.covid19tracker.R
import com.example.covid19tracker.Utils.*
import com.example.covid19tracker.databinding.ActivityMainBinding
import com.example.covid19tracker.network.remote.response.CountriesItem
import com.example.covid19tracker.network.remote.response.CovidData
import com.example.covid19tracker.ui.adapter.CovidTrackerSparkAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var numOfCases: Int = 0

    var lastCovidData: CovidData? = null

    lateinit var mBinding: ActivityMainBinding

    private val mViewModel: CovidTrackerViewModel by viewModels()

    private var countryName: String = ""

    @Inject
    lateinit var mCovidAdapter: CovidTrackerSparkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mBinding.sparkAdapter = mCovidAdapter

        observeLiveData()

        eventListeners()
    }

    /**
     * Observe the changes in the LiveData.
     */
    private fun observeLiveData() {
        mViewModel.countriesResponse.observe(this) { countryResource ->
            when (countryResource) {
                is Resource.Success -> {
                    countryResource.data?.let {
                        setDataSourceToSpinner(countryResource.data.sortedBy { it.Country })
                    }
                    hideProgressBar()
                    hideNoRecord()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    showNoRecord()
                }
            }
        }

        mViewModel.covidHistoricalData.observe(this) { covidData ->
            when (covidData) {
                is Resource.Success -> {
                    covidData.data?.let {
                        displaySparkLine(covidData.data)
                    }
                    hideProgressBar()
                    hideNoRecord()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    showNoRecord()
                }
            }
        }
    }

    /**
     * Function responsible for listening events.
     */
    private fun eventListeners() {
        mBinding.rgMetricSelection.setOnCheckedChangeListener { radioGroup, id ->
            val color: Int

            // Update covid metric in adapter.
            mCovidAdapter.metric = when (id) {
                R.id.rd_confirmed -> {
                    numOfCases = lastCovidData?.Confirmed ?: 0
                    color = Color.YELLOW
                    Metric.CONFIRMED
                }
                R.id.rd_recoverd -> {
                    numOfCases = lastCovidData?.Recovered ?: 0
                    color = Color.GREEN
                    Metric.RECOVERED
                }
                R.id.rd_deaths -> {
                    numOfCases = lastCovidData?.Deaths ?: 0
                    color = Color.RED
                    Metric.DEATH
                }
                else -> {
                    numOfCases = lastCovidData?.Confirmed ?: 0
                    color = Color.GREEN
                    Metric.CONFIRMED
                }
            }

            // Assign appropriate color for spark view
            mBinding.sparkview.lineColor = color

            // Format the covid numbers and display in ticker textview
            mBinding.tickerViewNumbers.text = NumberFormat.getInstance().format(numOfCases)

            mCovidAdapter.notifyDataSetChanged()
        }

        mBinding.rgTimeSelection.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.rd_max -> updateTimeScale(TimeScale.MAX)
                R.id.rd_one_month -> updateTimeScale(TimeScale.MONTH)
                R.id.rd_one_week -> updateTimeScale(TimeScale.WEEK)
                else -> TimeScale.MAX
            }
        }

        mBinding.sparkview.setScrubListener { item ->
            if (item is CovidData) {
                updateDateAndNumbers(item)
            }
        }
    }

    /**
     * Display a spark line using covid data.
     */
    private fun displaySparkLine(covidData: List<CovidData>?) {
        mBinding.covidDataList = covidData!!

        if (covidData.isNotEmpty()) {
            lastCovidData = covidData.last() // Last(Recent) covid data.
            lastCovidData?.let {
                updateInfo(it)
            }
        } else {
            mBinding.tvDate.text = resources.getString(R.string.text_no_record_found)
            mBinding.tickerViewNumbers.text = "0"
        }
    }

    /**
     * Function will display recent date.
     */
    private fun updateInfo(lastData: CovidData) {
        updateDateAndNumbers(lastData)
    }

    /**
     * Update Date and TickerView TextViews data according to Metric selected.
     */
    private fun updateDateAndNumbers(item: CovidData) {
        val numCases = when (mCovidAdapter.metric) {
            Metric.CONFIRMED -> item.Confirmed
            Metric.RECOVERED -> item.Recovered
            Metric.DEATH -> item.Deaths
        }
        mBinding.tickerViewNumbers.text = NumberFormat.getInstance().format(numCases)
        mBinding.tvDate.text = convertDateStringIntoString(item.Date)
    }

    /**
     * Method will update time scale value and call api method with selected time scale.
     */
    private fun updateTimeScale(timescale: TimeScale) {
        when (timescale) {
            TimeScale.MAX -> mViewModel.getCovidHistoricalDataFromRepo(countryName)
            TimeScale.MONTH -> mViewModel.getCovidHistoricalDataFromRepo(
                countryName,
                fromDate = getDayAgo(30),
                toDate = getCurrentDate()
            )
            TimeScale.WEEK -> mViewModel.getCovidHistoricalDataFromRepo(
                countryName,
                fromDate = getDayAgo(7),
                toDate = getCurrentDate()
            )
        }
    }

    /**
     * Set data in spinner
     */
    private fun setDataSourceToSpinner(data: List<CountriesItem>) {
        mBinding.countries = data
        mBinding.spinnerCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    v: View?,
                    position: Int,
                    id: Long
                ) {
                    countryName = data[position].Country // Currently selected country
                    mViewModel.getCovidHistoricalDataFromRepo(countryName)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) { /* NO OP */ }
            }
    }


    /**
     * Methods to show/hide progress bar & No Record TextView.
     */
    private fun showProgressBar() {
        mBinding.progressBar.isVisible = true
    }

    private fun hideProgressBar() {
        mBinding.progressBar.isVisible = false
    }

    private fun showNoRecord() {
        clearExistingData()
        mBinding.tvNoRecordFound.isVisible = true
    }

    private fun hideNoRecord() {
        mBinding.tvNoRecordFound.isVisible = false
    }

    /**
     * It will clear all the previous data displayed in views.
     */
    private fun clearExistingData() {
        mCovidAdapter.setCovidData(mutableListOf())
        mCovidAdapter.notifyDataSetChanged()
        mBinding.tickerViewNumbers.text = "0"
        mBinding.tvDate.text = resources.getString(R.string.text_no_record_found)
    }

}