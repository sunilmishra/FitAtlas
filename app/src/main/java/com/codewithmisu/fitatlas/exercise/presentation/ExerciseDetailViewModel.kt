package com.codewithmisu.fitatlas.exercise.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithmisu.fitatlas.core.UiState
import com.codewithmisu.fitatlas.exercise.domain.ExerciseDetail
import com.codewithmisu.fitatlas.exercise.domain.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ExerciseDetail>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadExerciseDetail(exerciseId: String, forceRefresh: Boolean = false) {
        /// Check if we are already showing data for this exercise
        /// and if the current state is already Success.
        if (_uiState.value is UiState.Success) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.emit(UiState.Loading)
                val exerciseDetail = repository.getExerciseDetail(exerciseId, forceRefresh)
                if (exerciseDetail != null) {
                    _uiState.emit(UiState.Success(exerciseDetail))
                }
            } catch (e: Exception) {
                _uiState.emit(
                    UiState.Failure(
                        message = e.message ?: "Failed to load exercise detail for $exerciseId...",
                        throwable = e.cause
                    )
                )
            }
        }
    }
}