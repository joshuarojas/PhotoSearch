package com.joshrojas.photosearch.view.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import com.joshrojas.photosearch.data.repository.FlickrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: FlickrRepository) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    fun fetchFeed(query: String = "") {
        viewModelScope.launch {
            _homeState.emit(HomeState(isLoading = true))
            repository.getFeedByQuery(query)
                .catch {
                    Log.e("Error Flickr", it.message.orEmpty(), it)
                    _homeState.emit(HomeState(isLoading = false, error = it))
                }
                .collect { _homeState.emit(HomeState(isLoading = false, data = it)) }
        }
    }

    fun startSearch() {
        viewModelScope.launch {
            _searchState.emit(_searchState.value.copy(isSearching = true))
        }
    }

    fun updateSearch(searchQuery: String) {
        viewModelScope.launch {
            _searchState.emit(_searchState.value.copy(searchQuery = searchQuery))
        }
    }

    fun hideSearch() {
        viewModelScope.launch {
            _searchState.emit(_searchState.value.copy(isSearching = false))
        }
    }
}

data class SearchState(
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)

data class HomeState(
    val isLoading: Boolean = true,
    val data: List<ItemResponse> = emptyList(),
    val error: Throwable? = null
)