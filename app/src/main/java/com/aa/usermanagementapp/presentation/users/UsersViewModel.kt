package com.aa.usermanagementapp.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.usermanagementapp.domain.model.User
import com.aa.usermanagementapp.domain.usecase.DeleteUserUseCase
import com.aa.usermanagementapp.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) : ViewModel() {

    private val _pendingDeleteUser = MutableStateFlow<User?>(null)

    val uiState: StateFlow<UsersUiState> = combine(
        getUsersUseCase(),
        _pendingDeleteUser,
    ) { users, pendingDeleteUser ->
        if (users.isEmpty()) UsersUiState.Empty
        else UsersUiState.Success(users = users, pendingDeleteUser = pendingDeleteUser)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UsersUiState.Loading,
    )

    private val _events = MutableSharedFlow<UsersEvent>()
    val events: SharedFlow<UsersEvent> = _events.asSharedFlow()

    fun onDeleteRequest(user: User) {
        _pendingDeleteUser.value = user
    }

    fun onDeleteDismissed() {
        _pendingDeleteUser.value = null
    }

    fun onDeleteConfirmed() {
        val user = _pendingDeleteUser.value ?: return
        _pendingDeleteUser.value = null
        viewModelScope.launch {
            runCatching { deleteUserUseCase(user) }
                .onSuccess { _events.emit(UsersEvent.UserDeleted) }
        }
    }
}
