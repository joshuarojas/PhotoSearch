package com.joshrojas.photosearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.joshrojas.photosearch.data.remote.FlickrAPI
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import retrofit2.HttpException

class FlickrPagingSource(
    private val search: String,
    private val api: FlickrAPI,
) : PagingSource<Int, ItemResponse>() {
    override fun getRefreshKey(state: PagingState<Int, ItemResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemResponse> {
        val page = params.key ?: 1
        val response = api.getFeedByQuery(query = search)
        return if (response.isSuccessful) {
            response.body()?.let {
                LoadResult.Page(
                    it.items,
                    prevKey = if (page == 1) null else page.minus(1),
                    nextKey = if (it.items.isEmpty()) null else page.plus(1)
                )
            } ?: run {
                LoadResult.Error(HttpException(response))
            }
        } else {
            LoadResult.Error(HttpException(response))
        }
    }
}

