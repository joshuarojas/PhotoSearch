package com.joshrojas.photosearch.data.repository

import com.joshrojas.photosearch.data.remote.FlikrAPI
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

class FlickrRepository(
    private val api: FlikrAPI,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {

    suspend fun getFeedByQuery(query: String): Flow<List<ItemResponse>> = flow {
        val response = api.getFeedByQuery(query = query)
        if (response.isSuccessful) {
            response.body()?.let { emit(it.items) } ?: run { throw HttpException(response) }
        } else {
            throw HttpException(response)
        }
    }.flowOn(coroutineContext)
}