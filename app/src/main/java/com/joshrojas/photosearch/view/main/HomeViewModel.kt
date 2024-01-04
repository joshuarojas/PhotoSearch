package com.joshrojas.photosearch.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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

    private val _itemsState = MutableStateFlow<PagingData<ItemResponse>>(PagingData.empty())
    val itemsState = _itemsState.asStateFlow()

    private val _voiceSearchEnabled = MutableStateFlow(true)
    val voiceSearchEnabled = _voiceSearchEnabled.asStateFlow()

    fun fetchFeed() {
        viewModelScope.launch {
            repository.getFeedByQuery()
                .cachedIn(viewModelScope)
                .catch { _homeState.emit(HomeState(isLoading = false, error = it)) }
                .collect {
                    _homeState.emit(HomeState(isLoading = false))
                    _itemsState.emit(it)
                }
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        viewModelScope.launch {
            _searchState.emit(_searchState.value.copy(searchQuery = searchQuery))
            repository.getFeedByQuery(searchQuery.replace(" ", ","))
                .cachedIn(viewModelScope)
                .catch { _homeState.emit(HomeState(isLoading = false, error = it)) }
                .collect {
                    _homeState.emit(HomeState(isLoading = false))
                    _itemsState.emit(it)
                }
        }
    }

    fun startSearch() {
        viewModelScope.launch {
            _searchState.emit(_searchState.value.copy(isSearchBoxShown = true))
        }
    }

    fun isVoiceSearchEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            _voiceSearchEnabled.emit(isEnabled)
        }
    }
}

data class SearchState(
    val isSearchBoxShown: Boolean = false,
    val searchQuery: String = "",
)

data class HomeState(
    val isLoading: Boolean = true,
    val error: Throwable? = null
)