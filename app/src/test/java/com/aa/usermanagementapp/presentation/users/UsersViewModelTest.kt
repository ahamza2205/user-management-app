package com.aa.usermanagementapp.presentation.users

import app.cash.turbine.test
import com.aa.usermanagementapp.domain.usecase.DeleteUserUseCase
import com.aa.usermanagementapp.domain.usecase.GetUsersUseCase
import com.aa.usermanagementapp.util.FakeUserRepository
import com.aa.usermanagementapp.util.MainDispatcherRule
import com.aa.usermanagementapp.util.aUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    /**
     * Activates the [stateIn(WhileSubscribed)] upstream so that [viewModel.uiState.value]
     * reflects the latest state throughout the test.
     *
     * This is the pattern recommended by Google for testing StateFlows created with
     * [SharingStarted.WhileSubscribed] — the backgroundScope collector is cancelled
     * automatically when the test ends.
     */
    private fun TestScope.collectUiState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    // ─── List state ───────────────────────────────────────────────────────────

    @Test
    fun uiState_withEmptyRepository_isEmptyState() = runTest {
        collectUiState()
        assertEquals(UsersUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun uiState_withUsers_isSuccessState() = runTest {
        repository.setUsers(listOf(aUser(id = 1), aUser(id = 2, name = "Jane")))
        collectUiState()
        val state = viewModel.uiState.value
        assertTrue(state is UsersUiState.Success)
        assertEquals(2, (state as UsersUiState.Success).users.size)
    }

    @Test
    fun uiState_updatesWhenUserIsAdded() = runTest {
        collectUiState()
        assertEquals(UsersUiState.Empty, viewModel.uiState.value)

        repository.insertUser(aUser())

        assertTrue(viewModel.uiState.value is UsersUiState.Success)
        assertEquals(1, (viewModel.uiState.value as UsersUiState.Success).users.size)
    }

    @Test
    fun uiState_successState_containsCorrectUserData() = runTest {
        repository.setUsers(listOf(aUser(name = "Sara", age = 22, jobTitle = "QA")))
        collectUiState()

        val user = (viewModel.uiState.value as UsersUiState.Success).users.first()
        assertEquals("Sara", user.name)
        assertEquals(22, user.age)
        assertEquals("QA", user.jobTitle)
    }

    // ─── Delete request ───────────────────────────────────────────────────────

    @Test
    fun onDeleteRequest_setsUserAsPendingDelete() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))
        collectUiState()

        viewModel.onDeleteRequest(user)

        val state = viewModel.uiState.value as UsersUiState.Success
        assertNotNull(state.pendingDeleteUser)
        assertEquals(user.id, state.pendingDeleteUser?.id)
    }

    @Test
    fun onDeleteRequest_pendingDeleteUser_matchesRequestedUser() = runTest {
        val alice = aUser(id = 1, name = "Alice")
        val bob = aUser(id = 2, name = "Bob")
        repository.setUsers(listOf(alice, bob))
        collectUiState()

        viewModel.onDeleteRequest(bob)

        val state = viewModel.uiState.value as UsersUiState.Success
        assertEquals("Bob", state.pendingDeleteUser?.name)
    }

    // ─── Delete dismiss ───────────────────────────────────────────────────────

    @Test
    fun onDeleteDismissed_clearsPendingDeleteUser() = runTest {
        val user = aUser()
        repository.setUsers(listOf(user))
        collectUiState()
        viewModel.onDeleteRequest(user)

        viewModel.onDeleteDismissed()

        val state = viewModel.uiState.value as UsersUiState.Success
        assertNull(state.pendingDeleteUser)
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
        collectUiState()
        viewModel.onDeleteRequest(user)

        viewModel.onDeleteConfirmed()

        // After confirm, pending is cleared regardless of the resulting list state
        val pending = (viewModel.uiState.value as? UsersUiState.Success)?.pendingDeleteUser
        assertNull(pending)
    }

    // ─── Guard & failure ──────────────────────────────────────────────────────

    @Test
    fun onDeleteConfirmed_withNoPendingUser_doesNotDeleteFromRepository() = runTest {
        repository.setUsers(listOf(aUser()))

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
