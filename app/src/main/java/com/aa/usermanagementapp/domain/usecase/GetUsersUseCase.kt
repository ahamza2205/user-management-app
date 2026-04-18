package com.aa.usermanagementapp.domain.usecase

import com.aa.usermanagementapp.domain.model.User
import com.aa.usermanagementapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<List<User>> {
        return repository.getUsers()
    }
}
