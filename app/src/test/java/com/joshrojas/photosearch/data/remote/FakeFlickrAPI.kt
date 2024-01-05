package com.joshrojas.photosearch.data.remote

import com.joshrojas.photosearch.data.remote.response.FlickrResponse
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeFlickrAPI : FlickrAPI {

    private var resultItems = listOf<ItemResponse>()
    private var shouldReturnError = false
    private val result
        get() = if (shouldReturnError) {
            Response.error(500, "{}".toResponseBody("text/plain".toMediaType()))
        } else {
            Response.success(FlickrResponse(items = resultItems))
        }

    override suspend fun getFeedByQuery(
        format: String,
        mode: String,
        query: String
    ): Response<FlickrResponse> = result

    fun addResults(items: List<ItemResponse>) {
        resultItems = items
    }

    fun setErrorResponse() {
        shouldReturnError = true
    }

    fun setSuccessResponse() {
        shouldReturnError = false
    }
}