package com.joshrojas.photosearch.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import com.joshrojas.photosearch.data.repository.FlickrRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repository: FlickrRepository) : ViewModel() {

    private val _voiceRecognitionState = MutableStateFlow(false)
    val voiceRecognition = _voiceRecognitionState.asStateFlow()

    val homeState: StateFlow<HomeState>
    val itemsState: Flow<PagingData<ItemResponse>>

    val action: (UIEvent) -> Unit

    init {
        val uiEventState = MutableSharedFlow<UIEvent>()

        val searchStartedFlow = uiEventState
            .filterIsInstance<UIEvent.SearchStarted>()
            .distinctUntilChanged()
            .onStart { emit(UIEvent.SearchStarted(false)) }

        val searchUpdateFlow = uiEventState
            .filterIsInstance<UIEvent.SearchUpdate>()
            .distinctUntilChanged()
            .onStart { emit(UIEvent.SearchUpdate("")) }

        val hasErrorFlow = MutableStateFlow(false)

        itemsState = searchUpdateFlow
            .flatMapLatest {
                repository
                    .getFeedByQuery(it.query)
                    .catch { hasErrorFlow.emit(true) }
            }
            .cachedIn(viewModelScope)


        homeState = combine(
            searchStartedFlow,
            searchUpdateFlow,
            hasErrorFlow
        ) { searchStarted, searchQuery, hasError->
            HomeState(
                searchQuery = searchQuery.query,
                isSearching = searchStarted.isStarted,
                hasError = hasError,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState()
        )

        action = { event: UIEvent ->
            viewModelScope.launch { uiEventState.emit(event) }
        }
    }

    fun updateVoiceRecognitionState(isEnabled: Boolean) {
        viewModelScope.launch {
            _voiceRecognitionState.emit(isEnabled)
        }
    }
}

data class HomeState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val hasError: Boolean = false,
)

sealed class UIEvent {
    data class SearchStarted(val isStarted: Boolean) : UIEvent()
    data class SearchUpdate(val query: String) : UIEvent()
}