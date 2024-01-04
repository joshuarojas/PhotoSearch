package com.joshrojas.photosearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.joshrojas.photosearch.data.paging.FlickrPagingSource
import com.joshrojas.photosearch.data.remote.FlikrAPI
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

class FlickrRepository(
    private val api: FlikrAPI,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {

    fun getFeedByQuery(query: String = ""): Flow<PagingData<ItemResponse>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { FlickrPagingSource(query, api) }
        ).flow.flowOn(coroutineContext)
}