package com.aa.usermanagementapp.presentation.adduser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.usermanagementapp.domain.model.User
import com.aa.usermanagementapp.domain.usecase.InsertUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(
    private val insertUserUseCase: InsertUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUserUiState())
    val uiState: StateFlow<AddUserUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddUserEvent>()
    val events: SharedFlow<AddUserEvent> = _events.asSharedFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = validateName(value)) }
    }

    fun onAgeChange(value: String) {
        _uiState.update { it.copy(age = value, ageError = validateAge(value)) }
    }

    fun onJobTitleChange(value: String) {
        _uiState.update { it.copy(jobTitle = value, jobTitleError = validateJobTitle(value)) }
    }

    fun onGenderChange(value: String) {
        _uiState.update { it.copy(gender = value, genderError = null) }
    }

    fun onSaveClick() {
        if (!_uiState.value.isFormValid || _uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val state = _uiState.value
            runCatching {
                insertUserUseCase(
                    User(
                        name = state.name.trim(),
                        age = state.age.trim().toInt(),
                        jobTitle = state.jobTitle.trim(),
                        gender = state.gender,
                    )
                )
            }.onSuccess {
                _uiState.value = AddUserUiState()
                _events.emit(AddUserEvent.SaveSuccess)
            }.onFailure {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun validateName(value: String): String? =
        "Name cannot be blank".takeIf { value.isBlank() }

    private fun validateAge(value: String): String? = when {
        value.isBlank() -> "Age is required"
        value.toIntOrNull() == null -> "Enter a valid number"
        value.toInt() <= 0 -> "Age must be greater than 0"
        value.toInt() > 120 -> "Enter a realistic age"
        else -> null
    }

    private fun validateJobTitle(value: String): String? =
        "Job title cannot be blank".takeIf { value.isBlank() }
}
