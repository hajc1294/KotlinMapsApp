package com.jeanca.mapsapp.api

import com.google.gson.GsonBuilder
import com.jeanca.mapsapp.commons.Constants
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

class Provider {

    companion object {

        private var converterFactory: GsonConverterFactory? = null

        fun getHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor {
                val builder = it.request().newBuilder()
                it.proceed(builder.build())
            }.build()

        val converter: GsonConverterFactory
            get() {
                if (converterFactory == null) {
                    converterFactory = GsonConverterFactory
                        .create(GsonBuilder().setLenient().disableHtmlEscaping().create())
                }
                return converterFactory!!
            }
    }
}

object ApiProvider {

    var url: HttpUrl = HttpUrl.get(Constants.BASE_URL)

    fun provider(): ApiService = Retrofit.Builder()
        .baseUrl(url)
        .client(Provider.getHttpClient())
        .addConverterFactory(Provider.converter)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)
}