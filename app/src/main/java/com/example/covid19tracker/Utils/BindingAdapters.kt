package com.example.covid19tracker.Utils

import android.R
import android.graphics.Color
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.covid19tracker.network.remote.response.CountriesItem
import com.example.covid19tracker.network.remote.response.CovidData
import com.example.covid19tracker.ui.adapter.CovidTrackerSparkAdapter
import com.robinhood.spark.SparkView

@BindingAdapter("setDataSource")
fun AppCompatSpinner.setDataSource(data: List<CountriesItem>?) {
    data?.let {
        val arrayAdapter = ArrayAdapter(this.context, R.layout.simple_spinner_item, it)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = arrayAdapter
    }
}

@BindingAdapter(value = ["setData", "setAdapter"])
fun SparkView.setAdapter(data: List<CovidData>?, adapter: CovidTrackerSparkAdapter?) {
    this.isScrubEnabled = true // Scrubable.
    this.lineWidth = 2f

    adapter?.let {
        this.lineColor = when(it.metric) {
            Metric.CONFIRMED -> Color.YELLOW
            Metric.RECOVERED -> Color.GREEN
            Metric.DEATH -> Color.RED
        }

        data?.let { item ->
            it.setCovidData(item.toMutableList())
            this.adapter = adapter
            it.notifyDataSetChanged()
        }
    }
}

