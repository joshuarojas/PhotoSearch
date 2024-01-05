package com.joshrojas.photosearch.data.remote

import com.google.gson.GsonBuilder
import com.joshrojas.photosearch.RemoteHelper
import com.joshrojas.photosearch.data.remote.interceptor.FlickrResponseInterceptor
import com.joshrojas.photosearch.data.remote.response.AuthorResponse
import com.joshrojas.photosearch.data.remote.type.AuthorTypeAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteBuilder {

    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    private fun getRetrofitInstance(): Retrofit {
        if (client == null) {
            client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = RemoteHelper.loggingLevel
                })
                .addInterceptor(FlickrResponseInterceptor())
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
        }
        if (retrofit == null) {
            val gson = GsonBuilder()
                .setLenient()
                .setDateFormat("yyy-MM-dd'T'HH:mm:ss'Z'")
                .registerTypeAdapter(AuthorResponse::class.java, AuthorTypeAdapter())
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl("https://www.flickr.com/services/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client!!)
                .build()
        }
        return retrofit!!
    }

    fun createFilckrAPI(): FlickrAPI = getRetrofitInstance().create(FlickrAPI::class.java)
}