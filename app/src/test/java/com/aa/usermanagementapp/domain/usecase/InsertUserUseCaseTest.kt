package com.aa.usermanagementapp.domain.usecase

import com.aa.usermanagementapp.util.FakeUserRepository
import com.aa.usermanagementapp.util.aUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InsertUserUseCaseTest {

    private lateinit var repository: FakeUserRepository
    private lateinit var useCase: InsertUserUseCase

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        useCase = InsertUserUseCase(repository)
    }

    @Test
    fun invoke_withValidUser_storesUserInRepository() = runTest {
        val user = aUser(name = "Jane Smith")

        useCase(user)

        val stored = repository.getUsers().first()
        assertEquals(1, stored.size)
        assertEquals("Jane Smith", stored.first().name)
    }

    @Test
    fun invoke_calledTwice_storesBothUsers() = runTest {
        useCase(aUser(id = 1, name = "Alice"))
        useCase(aUser(id = 2, name = "Bob"))

        val stored = repository.getUsers().first()
        assertEquals(2, stored.size)
    }

    @Test
    fun invoke_preservesAllUserFields() = runTest {
        val user = aUser(name = "Ali Hassan", age = 25, jobTitle = "Designer", gender = "Male")

        useCase(user)

        val stored = repository.getUsers().first().first()
        assertEquals("Ali Hassan", stored.name)
        assertEquals(25, stored.age)
        assertEquals("Designer", stored.jobTitle)
        assertEquals("Male", stored.gender)
    }
}
