package com.aa.usermanagementapp.presentation.users

sealed interface UsersEvent {
    data object UserDeleted : UsersEvent
}
