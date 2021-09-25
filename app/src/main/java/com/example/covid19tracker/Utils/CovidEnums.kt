package com.example.covid19tracker.Utils

/**
 * Created by Dhruv Limbachiya on 25-09-2021.
 */

// Enum class for covid data metrics.
enum class Metric {
   CONFIRMED,
   RECOVERED,
   DEATH
}

// Enum class for covid prehistorical time scale.
enum class TimeScale(val noOfDays: Int) {
    WEEK(7),
    MONTH(30),
    MAX(-1)
}