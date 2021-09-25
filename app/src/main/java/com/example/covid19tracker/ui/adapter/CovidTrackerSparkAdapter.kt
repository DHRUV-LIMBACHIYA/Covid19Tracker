package com.example.covid19tracker.ui.adapter

import android.graphics.RectF
import com.example.covid19tracker.Utils.Metric
import com.example.covid19tracker.Utils.TimeScale
import com.example.covid19tracker.network.remote.response.CovidData
import com.robinhood.spark.SparkAdapter

/**
 * Created by Dhruv Limbachiya on 25-09-2021.
 */

class CovidTrackerSparkAdapter() : SparkAdapter(){

    var metric = Metric.RECOVERED
    var timeScale = TimeScale.MAX

    var data = mutableListOf<CovidData>()

    override fun getCount(): Int = data.size

    override fun getItem(index: Int): Any = data[index]

    override fun getY(index: Int): Float {
        val currentData = data[index]
       return when(metric) {
            Metric.CONFIRMED -> currentData.Confirmed.toFloat()
            Metric.RECOVERED -> currentData.Recovered.toFloat()
            Metric.DEATH -> currentData.Deaths.toFloat()
        }
    }

    fun setCovidData(data: MutableList<CovidData>) {
        this.data.clear()
        this.data = data
    }
    override fun getDataBounds(): RectF {
        val bounds = super.getDataBounds()
        if (timeScale != TimeScale.MAX) {
            bounds.left = count - timeScale.noOfDays.toFloat()
        }
        return bounds
    }
}