package com.joshrojas.photosearch.data.remote

import com.joshrojas.photosearch.data.remote.response.FlickrResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrAPI {

    @GET("feeds/photos_public.gne")
    suspend fun getFeedByQuery(
        @Query("format") format: String = "json",
        @Query("tagmode") mode: String = "any",
        @Query("tags") query: String = "",
    ): Response<FlickrResponse>
}