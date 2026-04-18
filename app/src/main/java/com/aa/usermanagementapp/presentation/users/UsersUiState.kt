package com.aa.usermanagementapp.presentation.users

import com.aa.usermanagementapp.domain.model.User

sealed interface UsersUiState {
    data object Loading : UsersUiState
    data object Empty : UsersUiState
    data class Success(val users: List<User>) : UsersUiState
}
