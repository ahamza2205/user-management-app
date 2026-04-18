package com.aa.usermanagementapp.presentation.users

import app.cash.turbine.test
import com.aa.usermanagementapp.domain.usecase.DeleteUserUseCase
import com.aa.usermanagementapp.domain.usecase.GetUsersUseCase
import com.aa.usermanagementapp.util.FakeUserRepository
import com.aa.usermanagementapp.util.MainDispatcherRule
import com.aa.usermanagementapp.util.aUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeUserRepository
    private lateinit var viewModel: UsersViewModel

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        viewModel = UsersViewModel(
            getUsersUseCase = GetUsersUseCase(repository),
            deleteUserUseCase = DeleteUserUseCase(repository),
        )
    }

    // ─── List state ───────────────────────────────────────────────────────────

    @Test
    fun uiState_withEmptyRepository_emitsEmptyState() = runTest {
        viewModel.uiState.test {
            skipItems(1) // Loading
            assertEquals(UsersUiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiState_withUsers_emitsSuccessState() = runTest {
        repository.setUsers(listOf(aUser(id = 1), aUser(id = 2, name = "Jane")))

        viewModel.uiState.test {
            skipItems(1) // Loading
            val state = awaitItem()
            assertTrue(state is UsersUiState.Success)
            assertEquals(2, (state as UsersUiState.Success).users.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiState_emitsUpdatedSuccessWhenUserIsAdded() = runTest {
        viewModel.uiState.test {
            skipItems(1) // Loading
            assertEquals(UsersUiState.Empty, awaitItem())

            // Insert a user after observation starts
            repository.insertUser(aUser())

            val state = awaitItem()
            assertTrue(state is UsersUiState.Success)
            assertEquals(1, (state as UsersUiState.Success).users.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiState_successState_containsCorrectUserData() = runTest {
        repository.setUsers(listOf(aUser(name = "Sara", age = 22, jobTitle = "QA")))

        viewModel.uiState.test {
            skipItems(1)
            val user = (awaitItem() as UsersUiState.Success).users.first()
            assertEquals("Sara", user.name)
            assertEquals(22, user.age)
            assertEquals("QA", user.jobTitle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Delete request ───────────────────────────────────────────────────────

    @Test
    fun onDeleteRequest_setsUserAsPendingDelete() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))

        viewModel.uiState.test {
            skipItems(1)          // Loading
            awaitItem()           // Success(pendingDeleteUser = null)

            viewModel.onDeleteRequest(user)

            val state = awaitItem() as UsersUiState.Success
            assertNotNull(state.pendingDeleteUser)
            assertEquals(user.id, state.pendingDeleteUser?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onDeleteRequest_pendingDeleteUser_matchesRequestedUser() = runTest {
        val alice = aUser(id = 1, name = "Alice")
        val bob = aUser(id = 2, name = "Bob")
        repository.setUsers(listOf(alice, bob))

        viewModel.uiState.test {
            skipItems(1)
            awaitItem() // Success

            viewModel.onDeleteRequest(bob)

            val state = awaitItem() as UsersUiState.Success
            assertEquals("Bob", state.pendingDeleteUser?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Delete dismiss ───────────────────────────────────────────────────────

    @Test
    fun onDeleteDismissed_clearsPendingDeleteUser() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()                  // Success(pending = null)
            viewModel.onDeleteRequest(user)
            awaitItem()                  // Success(pending = user)

            viewModel.onDeleteDismissed()

            val state = awaitItem() as UsersUiState.Success
            assertNull(state.pendingDeleteUser)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onDeleteDismissed_doesNotRemoveUserFromRepository() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))

        viewModel.onDeleteRequest(user)
        viewModel.onDeleteDismissed()

        assertEquals(1, repository.getUsers().first().size)
    }

    // ─── Delete confirmed ─────────────────────────────────────────────────────

    @Test
    fun onDeleteConfirmed_removesUserFromRepository() = runTest {
        val user = aUser(id = 1)
        repository.setUsers(listOf(user))
        viewModel.onDeleteRequest(user)

        viewModel.onDeleteConfirmed()

        assertTrue(repository.getUsers().first().isEmpty())
    }

    @Test
    fun onDeleteConfirmed_removesOnlyTheTargetUser() = runTest {
        val alice = aUser(id = 1, name = "Alice")
        val bob = aUser(id = 2, name = "Bob")
        repository.setUsers(listOf(alice, bob))
        viewModel.onDeleteRequest(alice)

        viewModel.onDeleteConfirmed()

        val remaining = repository.getUsers().first()
        assertEquals(1, remaining.size)
        assertEquals("Bob", remaining.first().name)
    }

    @Test
    fun onDeleteConfirmed_emitsUserDeletedEvent() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))
        viewModel.onDeleteRequest(user)

        viewModel.events.test {
            viewModel.onDeleteConfirmed()
            assertEquals(UsersEvent.UserDeleted, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onDeleteConfirmed_clearsPendingDeleteUser() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()                    // Success(pending = null)
            viewModel.onDeleteRequest(user)
            awaitItem()                    // Success(pending = user)

            viewModel.onDeleteConfirmed()

            // Next emission: pending cleared (happens before repo delete)
            // Then: either Empty or Success with updated list
            // We only care that pendingDeleteUser is gone
            val afterConfirm = awaitItem()
            val pending = (afterConfirm as? UsersUiState.Success)?.pendingDeleteUser
            assertNull(pending)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Delete — guard & failure ─────────────────────────────────────────────

    @Test
    fun onDeleteConfirmed_withNoPendingUser_doesNotDeleteFromRepository() = runTest {
        repository.setUsers(listOf(aUser()))
        // onDeleteRequest is NOT called — pendingDeleteUser is null

        viewModel.onDeleteConfirmed()

        assertEquals(1, repository.getUsers().first().size)
    }

    @Test
    fun onDeleteConfirmed_withNoPendingUser_doesNotEmitEvent() = runTest {
        viewModel.events.test {
            viewModel.onDeleteConfirmed()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onDeleteConfirmed_whenRepositoryFails_doesNotEmitEvent() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))
        repository.throwOnDelete = true
        viewModel.onDeleteRequest(user)

        viewModel.events.test {
            viewModel.onDeleteConfirmed()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
