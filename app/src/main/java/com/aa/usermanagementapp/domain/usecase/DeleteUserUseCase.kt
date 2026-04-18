package com.aa.usermanagementapp.domain.usecase

import com.aa.usermanagementapp.domain.model.User
import com.aa.usermanagementapp.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(user: User) {
        repository.deleteUser(user)
    }
}
