package com.aa.usermanagementapp.domain.usecase

import com.aa.usermanagementapp.util.FakeUserRepository
import com.aa.usermanagementapp.util.aUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteUserUseCaseTest {

    private lateinit var repository: FakeUserRepository
    private lateinit var useCase: DeleteUserUseCase

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        useCase = DeleteUserUseCase(repository)
    }

    @Test
    fun invoke_withExistingUser_removesUserFromRepository() = runTest {
        val user = aUser(id = 1)
        repository.setUsers(listOf(user))

        useCase(user)

        assertTrue(repository.getUsers().first().isEmpty())
    }

    @Test
    fun invoke_removesOnlyTheTargetUser() = runTest {
        val alice = aUser(id = 1, name = "Alice")
        val bob = aUser(id = 2, name = "Bob")
        repository.setUsers(listOf(alice, bob))

        useCase(alice)

        val remaining = repository.getUsers().first()
        assertEquals(1, remaining.size)
        assertEquals("Bob", remaining.first().name)
    }

    @Test
    fun invoke_withNonExistentUser_doesNotAffectRepository() = runTest {
        val existing = aUser(id = 1, name = "Alice")
        val phantom = aUser(id = 99, name = "Ghost")
        repository.setUsers(listOf(existing))

        useCase(phantom)

        val stored = repository.getUsers().first()
        assertEquals(1, stored.size)
        assertFalse(stored.any { it.name == "Ghost" })
    }
}
