package com.example.covid19tracker.di.module

import android.util.Log
import com.example.covid19tracker.Utils.Constants
import com.example.covid19tracker.network.remote.api.CovidTrackerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Dhruv Limbachiya on 14-09-2021.
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideHttpLogger() = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.d("CovidLogger", message)
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideHttpClient(interceptor: HttpLoggingInterceptor) = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
        readTimeout(Constants.NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
        writeTimeout(Constants.NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
    }.build()

    @Singleton
    @Provides
    fun provideCovidApi(okHttpClient: OkHttpClient) = Retrofit.Builder().apply {
        baseUrl("BuildConfig.COVID_BASE_URL")
        addConverterFactory(GsonConverterFactory.create())
        client(okHttpClient)
    }.build().create(CovidTrackerService::class.java)
}