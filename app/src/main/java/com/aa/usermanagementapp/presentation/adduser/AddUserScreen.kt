package com.aa.usermanagementapp.presentation.adduser

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val GENDER_OPTIONS = listOf("Male", "Female", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    onNavigateToUsers: () -> Unit,
    viewModel: AddUserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddUserEvent.SaveSuccess -> onNavigateToUsers()
            }
        }
    }

    val nameFocus = remember { FocusRequester() }
    val ageFocus = remember { FocusRequester() }
    val jobTitleFocus = remember { FocusRequester() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add User") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "User Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                isError = uiState.nameError != null,
                supportingText = { Text(uiState.nameError ?: "") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { ageFocus.requestFocus() },
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocus),
            )

            OutlinedTextField(
                value = uiState.age,
                onValueChange = viewModel::onAgeChange,
                label = { Text("Age") },
                leadingIcon = { Icon(Icons.Outlined.Cake, contentDescription = null) },
                isError = uiState.ageError != null,
                supportingText = { Text(uiState.ageError ?: "") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { jobTitleFocus.requestFocus() },
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(ageFocus),
            )

            OutlinedTextField(
                value = uiState.jobTitle,
                onValueChange = viewModel::onJobTitleChange,
                label = { Text("Job Title") },
                leadingIcon = { Icon(Icons.Outlined.Work, contentDescription = null) },
                isError = uiState.jobTitleError != null,
                supportingText = { Text(uiState.jobTitleError ?: "") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() },
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(jobTitleFocus),
            )

            GenderSelector(
                selectedGender = uiState.gender,
                onGenderSelected = viewModel::onGenderChange,
                error = uiState.genderError,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::onSaveClick,
                enabled = uiState.isFormValid && !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                AnimatedContent(
                    targetState = uiState.isSaving,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "save_button_content",
                ) { saving ->
                    if (saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(
                            text = "Save User",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderSelector(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodySmall,
            color = if (error != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(start = 4.dp),
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            GENDER_OPTIONS.forEachIndexed { index, gender ->
                SegmentedButton(
                    selected = selectedGender == gender,
                    onClick = { onGenderSelected(gender) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = GENDER_OPTIONS.size,
                    ),
                    label = { Text(gender) },
                )
            }
        }

        AnimatedVisibility(visible = error != null) {
            Text(
                text = error.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
    }
}
