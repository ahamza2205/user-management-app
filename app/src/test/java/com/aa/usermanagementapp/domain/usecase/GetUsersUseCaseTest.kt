package com.aa.usermanagementapp.domain.usecase

import app.cash.turbine.test
import com.aa.usermanagementapp.util.FakeUserRepository
import com.aa.usermanagementapp.util.aUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUsersUseCaseTest {

    private lateinit var repository: FakeUserRepository
    private lateinit var useCase: GetUsersUseCase

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        useCase = GetUsersUseCase(repository)
    }

    @Test
    fun invoke_withEmptyRepository_emitsEmptyList() = runTest {
        useCase().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_withSeededUsers_emitsUserList() = runTest {
        val users = listOf(aUser(id = 1), aUser(id = 2, name = "Jane"))
        repository.setUsers(users)

        useCase().test {
            val emitted = awaitItem()
            assertEquals(2, emitted.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_emitsUpdatedListAfterInsert() = runTest {
        useCase().test {
            // Initial empty emission
            assertTrue(awaitItem().isEmpty())

            // Insert a user — Flow should emit an updated list
            repository.insertUser(aUser())
            assertEquals(1, awaitItem().size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_emitsCorrectUserData() = runTest {
        repository.setUsers(listOf(aUser(name = "Sara", age = 22, jobTitle = "QA")))

        useCase().test {
            val user = awaitItem().first()
            assertEquals("Sara", user.name)
            assertEquals(22, user.age)
            assertEquals("QA", user.jobTitle)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
