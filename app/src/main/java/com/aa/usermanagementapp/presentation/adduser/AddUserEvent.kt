package com.aa.usermanagementapp.presentation.adduser

sealed interface AddUserEvent {
    data object SaveSuccess : AddUserEvent
    data object SaveError : AddUserEvent
}
