package com.aa.usermanagementapp.domain.repository

import com.aa.usermanagementapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: User)
    suspend fun deleteUser(user: User)
    fun getUsers(): Flow<List<User>>
}
