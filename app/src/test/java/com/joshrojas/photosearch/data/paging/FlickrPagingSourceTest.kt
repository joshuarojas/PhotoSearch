package com.joshrojas.photosearch.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.joshrojas.photosearch.data.remote.FakeFlickrAPI
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.mock


class FlickrPagingSourceTest {

    private val mockItems
        get() = (1..20).map { mock<ItemResponse>() }

    private val fakeFlickrAPI = FakeFlickrAPI()

    @Test
    fun flickrPaging_SuccessInitialLoad() = runTest {
        val expectedResult = mockItems
        fakeFlickrAPI.setSuccessResponse()
        fakeFlickrAPI.addResults(expectedResult)
        val pagingSource = FlickrPagingSource(
            DEFAULT_SEARCH_QUERY,
            fakeFlickrAPI
        )

        val pager = TestPager(PagingConfig(20), pagingSource)
        val result = pager.refresh() as PagingSource.LoadResult.Page

        Assert.assertArrayEquals(result.data.toTypedArray(), expectedResult.toTypedArray())
    }

    @Test
    fun flickrPaging_SuccessConsecutiveLoad() = runTest {
        val expectedResult = mockItems
        fakeFlickrAPI.setSuccessResponse()
        fakeFlickrAPI.addResults(expectedResult)
        val pagingSource = FlickrPagingSource(
            DEFAULT_SEARCH_QUERY,
            fakeFlickrAPI
        )

        val pager = TestPager(PagingConfig(20), pagingSource)
        val result = with(pager) {
            refresh()
            append()
            append()
        } as PagingSource.LoadResult.Page

        Assert.assertArrayEquals(result.data.toTypedArray(), expectedResult.toTypedArray())
    }

    @Test
    fun flickrPaging_ErrorLoad() = runTest {
        fakeFlickrAPI.setErrorResponse()
        val pagingSource = FlickrPagingSource(
            DEFAULT_SEARCH_QUERY,
            fakeFlickrAPI
        )

        val pager = TestPager(PagingConfig(6), pagingSource)
        val result = pager.refresh() as? PagingSource.LoadResult.Error

        Assert.assertTrue(result is PagingSource.LoadResult.Error)
    }

    companion object {
        private const val DEFAULT_SEARCH_QUERY = ""
    }
}