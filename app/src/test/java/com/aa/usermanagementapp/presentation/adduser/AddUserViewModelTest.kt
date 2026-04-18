package com.aa.usermanagementapp.presentation.adduser

import app.cash.turbine.test
import com.aa.usermanagementapp.domain.usecase.InsertUserUseCase
import com.aa.usermanagementapp.util.FakeUserRepository
import com.aa.usermanagementapp.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddUserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeUserRepository
    private lateinit var viewModel: AddUserViewModel

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        viewModel = AddUserViewModel(
            insertUserUseCase = InsertUserUseCase(repository),
            validator = UserInputValidator(),
        )
    }

    // ─── Initial state ────────────────────────────────────────────────────────

    @Test
    fun initialState_allFieldsAreEmpty() {
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.age)
        assertEquals("", state.jobTitle)
        assertEquals("", state.gender)
    }

    @Test
    fun initialState_noErrorsAreShown() {
        val state = viewModel.uiState.value
        assertNull(state.nameError)
        assertNull(state.ageError)
        assertNull(state.jobTitleError)
        assertNull(state.genderError)
    }

    @Test
    fun initialState_formIsInvalid() {
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun initialState_isNotSaving() {
        assertFalse(viewModel.uiState.value.isSaving)
    }

    // ─── Field updates ────────────────────────────────────────────────────────

    @Test
    fun onNameChange_updatesNameInState() {
        viewModel.onNameChange("Alice")
        assertEquals("Alice", viewModel.uiState.value.name)
    }

    @Test
    fun onAgeChange_updatesAgeInState() {
        viewModel.onAgeChange("28")
        assertEquals("28", viewModel.uiState.value.age)
    }

    @Test
    fun onJobTitleChange_updatesJobTitleInState() {
        viewModel.onJobTitleChange("Designer")
        assertEquals("Designer", viewModel.uiState.value.jobTitle)
    }

    @Test
    fun onGenderChange_updatesGenderInState() {
        viewModel.onGenderChange("Female")
        assertEquals("Female", viewModel.uiState.value.gender)
    }

    // ─── Validation on change ─────────────────────────────────────────────────

    @Test
    fun onNameChange_withBlankInput_setsNameError() {
        viewModel.onNameChange("   ")
        assertNotNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun onNameChange_withValidInput_afterError_clearsNameError() {
        viewModel.onNameChange("")
        viewModel.onNameChange("Alice")
        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun onAgeChange_withNonNumericInput_setsAgeError() {
        viewModel.onAgeChange("abc")
        assertNotNull(viewModel.uiState.value.ageError)
    }

    @Test
    fun onAgeChange_withValidInput_afterError_clearsAgeError() {
        viewModel.onAgeChange("xyz")
        viewModel.onAgeChange("25")
        assertNull(viewModel.uiState.value.ageError)
    }

    @Test
    fun onJobTitleChange_withBlankInput_setsJobTitleError() {
        viewModel.onJobTitleChange("  ")
        assertNotNull(viewModel.uiState.value.jobTitleError)
    }

    // ─── isFormValid ──────────────────────────────────────────────────────────

    @Test
    fun isFormValid_whenAllFieldsAreCorrect_returnsTrue() {
        fillValidForm()
        assertTrue(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun isFormValid_whenNameIsBlank_returnsFalse() {
        fillValidForm()
        viewModel.onNameChange("")
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun isFormValid_whenAgeIsInvalid_returnsFalse() {
        fillValidForm()
        viewModel.onAgeChange("abc")
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun isFormValid_whenJobTitleIsBlank_returnsFalse() {
        fillValidForm()
        viewModel.onJobTitleChange("")
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun isFormValid_whenGenderIsNotSelected_returnsFalse() {
        viewModel.onNameChange("Alice")
        viewModel.onAgeChange("25")
        viewModel.onJobTitleChange("Designer")
        // gender intentionally not set
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    // ─── Save — happy path ────────────────────────────────────────────────────

    @Test
    fun onSaveClick_withValidForm_storesUserInRepository() = runTest {
        fillValidForm()
        viewModel.onSaveClick()
        assertEquals(1, repository.getUsers().first().size)
    }

    @Test
    fun onSaveClick_withValidForm_emitsSaveSuccessEvent() = runTest {
        fillValidForm()
        viewModel.events.test {
            viewModel.onSaveClick()
            assertEquals(AddUserEvent.SaveSuccess, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSaveClick_withValidForm_resetsFormToInitialState() = runTest {
        fillValidForm()
        viewModel.onSaveClick()
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.age)
        assertEquals("", state.jobTitle)
        assertEquals("", state.gender)
        assertFalse(state.isSaving)
    }

    @Test
    fun onSaveClick_storesCorrectUserData() = runTest {
        viewModel.onNameChange("Sara")
        viewModel.onAgeChange("22")
        viewModel.onJobTitleChange("QA Engineer")
        viewModel.onGenderChange("Female")

        viewModel.onSaveClick()

        val stored = repository.getUsers().first().first()
        assertEquals("Sara", stored.name)
        assertEquals(22, stored.age)
        assertEquals("QA Engineer", stored.jobTitle)
        assertEquals("Female", stored.gender)
    }

    // ─── Save — guard ─────────────────────────────────────────────────────────

    @Test
    fun onSaveClick_withInvalidForm_doesNotInsertIntoRepository() = runTest {
        // Default state is invalid — no fields filled
        viewModel.onSaveClick()
        assertTrue(repository.getUsers().first().isEmpty())
    }

    @Test
    fun onSaveClick_withInvalidForm_doesNotEmitEvent() = runTest {
        viewModel.events.test {
            viewModel.onSaveClick()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Save — failure path ──────────────────────────────────────────────────

    @Test
    fun onSaveClick_whenRepositoryFails_resetsSavingFlag() = runTest {
        repository.throwOnInsert = true
        fillValidForm()
        viewModel.onSaveClick()
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun onSaveClick_whenRepositoryFails_doesNotEmitSuccessEvent() = runTest {
        repository.throwOnInsert = true
        fillValidForm()
        viewModel.events.test {
            viewModel.onSaveClick()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSaveClick_whenRepositoryFails_formDataIsPreserved() = runTest {
        repository.throwOnInsert = true
        fillValidForm()
        viewModel.onSaveClick()
        // Form should NOT be reset on failure — user should not lose their input
        assertEquals("Alice", viewModel.uiState.value.name)
        assertEquals("25", viewModel.uiState.value.age)
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun fillValidForm() {
        viewModel.onNameChange("Alice")
        viewModel.onAgeChange("25")
        viewModel.onJobTitleChange("Designer")
        viewModel.onGenderChange("Female")
    }
}
