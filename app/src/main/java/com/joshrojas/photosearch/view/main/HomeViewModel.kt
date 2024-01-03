package com.joshrojas.photosearch.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import com.joshrojas.photosearch.data.repository.FlickrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class HomeViewModel(private val repository: FlickrRepository) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    fun fetchFeed() {
        viewModelScope.launch {
            repository.getFeedByQuery()
                .catch { _homeState.emit(HomeState(isLoading = false, error = it)) }
                .collect { _homeState.emit(HomeState(isLoading = false, data = it)) }
        }
    }

    fun startSearch() {
        viewModelScope.launch {
            _searchState.emit(_searchState.value.copy(isSearching = true))
            _searchState
                .debounce(700)
                .distinctUntilChanged { old, new -> old.searchQuery == new.searchQuery }
                .collect { search ->
                    _homeState.emit(HomeState())
                    repository.getFeedByQuery(search.searchQuery.replace(" ", ","))
                        .flowOn(Dispatchers.IO)
                        .catch { _homeState.emit(HomeState(isLoading = false, error = it)) }
                        .collect { _homeState.emit(HomeState(isLoading = false, data = it)) }
                }
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        viewModelScope.launch {
            _searchState.emit(_searchState.value.copy(searchQuery = searchQuery))
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