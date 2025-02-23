package com.example.passporter.presentation.feature.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.usecase.border.GetBorderPointDetailsUseCase
import com.example.passporter.presentation.util.ResultUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BorderDetailsViewModel @Inject constructor(
    private val getBorderPointDetailsUseCase: GetBorderPointDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val borderId: String = checkNotNull(savedStateHandle["borderId"])

    private val _state = MutableStateFlow<BorderDetailsState>(BorderDetailsState.Loading)
    val state: StateFlow<BorderDetailsState> = _state.asStateFlow()

    init {
        loadBorderPoint()
    }

    fun refresh() {
        loadBorderPoint()
    }

    private fun loadBorderPoint() {
        viewModelScope.launch {
            _state.value = BorderDetailsState.Loading
            when (val result = getBorderPointDetailsUseCase(borderId)) {
                is ResultUtil.Success -> {
                    _state.value = BorderDetailsState.Success(result.data)
                }
                is ResultUtil.Error -> {
                    _state.value = BorderDetailsState.Error(
                        result.exception.message ?: "Failed to load border point details"
                    )
                }
            }
        }
    }

    fun retry() {
        loadBorderPoint()
    }
}

sealed class BorderDetailsState {
    data object Loading : BorderDetailsState()
    data class Success(val borderPoint: BorderPoint) : BorderDetailsState()
    data class Error(val message: String) : BorderDetailsState()
}