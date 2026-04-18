package com.aa.usermanagementapp.util

import com.aa.usermanagementapp.domain.model.User
import com.aa.usermanagementapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory fake implementation of [UserRepository] for use in unit tests.
 *
 * Prefer this over a mock for repository tests — it provides realistic Flow behaviour
 * (emits updates automatically) without coupling tests to internal call sequences.
 */
class FakeUserRepository : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())

    /** Set to true to simulate a database error on the next insert. */
    var throwOnInsert = false

    /** Set to true to simulate a database error on the next delete. */
    var throwOnDelete = false

    override suspend fun insertUser(user: User) {
        if (throwOnInsert) throw RuntimeException("Simulated insert failure")
        _users.update { current ->
            current + user.copy(id = current.size + 1)
        }
    }

    override suspend fun deleteUser(user: User) {
        if (throwOnDelete) throw RuntimeException("Simulated delete failure")
        _users.update { current -> current.filter { it.id != user.id } }
    }

    override fun getUsers(): Flow<List<User>> = _users.asStateFlow()

    /** Convenience — seed the repository with a known list before a test starts. */
    fun setUsers(users: List<User>) {
        _users.value = users
    }
}
