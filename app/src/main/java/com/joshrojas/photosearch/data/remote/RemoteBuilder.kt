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


object RemoteBuilder {

    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    private fun getRetrofitInstance(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .setDateFormat("yyy-MM-dd'T'HH:mm:ss'Z'")
            .registerTypeAdapter(AuthorResponse::class.java, AuthorTypeAdapter())
            .create()

        if (client == null) {
            client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = RemoteHelper.loggingLevel })
                .addInterceptor(FlickrResponseInterceptor())
                .build()
        }
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://www.flickr.com/services/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client!!)
                .build()
        }
        return retrofit!!
    }

    fun createFilckrAPI(): FlikrAPI = getRetrofitInstance().create(FlikrAPI::class.java)
}