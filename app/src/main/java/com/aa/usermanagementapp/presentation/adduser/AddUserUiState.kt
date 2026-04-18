package com.aa.usermanagementapp.presentation.adduser

data class AddUserUiState(
    val name: String = "",
    val nameError: String? = null,
    val age: String = "",
    val ageError: String? = null,
    val jobTitle: String = "",
    val jobTitleError: String? = null,
    val gender: String = "",
    val genderError: String? = null,
    val isSaving: Boolean = false,
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() && nameError == null &&
                age.isNotBlank() && ageError == null &&
                jobTitle.isNotBlank() && jobTitleError == null &&
                gender.isNotBlank() && genderError == null
}
