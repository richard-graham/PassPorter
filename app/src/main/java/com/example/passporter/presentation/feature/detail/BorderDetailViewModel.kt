package com.example.passporter.presentation.feature.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.User
import com.example.passporter.domain.usecase.border.GetBorderPointDetailsUseCase
import com.example.passporter.domain.usecase.border.GetUserByIdUseCase
import com.example.passporter.presentation.util.ResultUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BorderDetailsViewModel @Inject constructor(
    private val getBorderPointDetailsUseCase: GetBorderPointDetailsUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
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

            // First, load the border point
            when (val borderPointResult = getBorderPointDetailsUseCase(borderId)) {
                is ResultUtil.Success -> {
                    val borderPoint = borderPointResult.data

                    // Fetch creator and last updater user details concurrently
                    val createdByUserDeferred = async {
                        getUserByIdUseCase(borderPoint.createdBy)
                    }

                    val lastUpdatedByUserDeferred = borderPoint.lastUpdatedBy?.let { userId ->
                        async { getUserByIdUseCase(userId) }
                    }

                    // Resolve user details
                    val createdByUser = createdByUserDeferred.await().let {
                        when (it) {
                            is ResultUtil.Success -> it.data
                            is ResultUtil.Error -> null
                        }
                    }

                    val lastUpdatedByUser = lastUpdatedByUserDeferred?.await()?.let {
                        when (it) {
                            is ResultUtil.Success -> it.data
                            is ResultUtil.Error -> null
                        }
                    }

                    // Update state with border point and user details
                    _state.value = BorderDetailsState.Success(
                        borderPoint = borderPoint,
                        createdByUser = createdByUser,
                        lastUpdatedByUser = lastUpdatedByUser
                    )
                }
                is ResultUtil.Error -> {
                    _state.value = BorderDetailsState.Error(
                        borderPointResult.exception.message ?: "Failed to load border point details"
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
    data class Success(
        val borderPoint: BorderPoint,
        val createdByUser: User? = null,
        val lastUpdatedByUser: User? = null
    ) : BorderDetailsState()
    data class Error(val message: String) : BorderDetailsState()
}