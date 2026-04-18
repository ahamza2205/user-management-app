package com.aa.usermanagementapp.data.repository

import com.aa.usermanagementapp.data.local.UserDao
import com.aa.usermanagementapp.data.mapper.toDomain
import com.aa.usermanagementapp.data.mapper.toEntity
import com.aa.usermanagementapp.domain.model.User
import com.aa.usermanagementapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
) : UserRepository {

    override suspend fun insertUser(user: User) {
        dao.insertUser(user.toEntity())
    }

    override fun getUsers(): Flow<List<User>> {
        return dao.getAllUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
