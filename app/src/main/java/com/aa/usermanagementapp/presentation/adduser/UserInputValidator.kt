package com.aa.usermanagementapp.presentation.adduser

import javax.inject.Inject

/**
 * Stateless validator for the Add User form fields.
 * Extracted from [AddUserViewModel] to make validation logic independently testable.
 */
class UserInputValidator @Inject constructor() {

    fun validateName(value: String): String? =
        "Name cannot be blank".takeIf { value.isBlank() }

    fun validateAge(value: String): String? = when {
        value.isBlank() -> "Age is required"
        value.toIntOrNull() == null -> "Enter a valid number"
        value.toInt() <= 0 -> "Age must be greater than 0"
        value.toInt() > 120 -> "Enter a realistic age"
        else -> null
    }

    fun validateJobTitle(value: String): String? =
        "Job title cannot be blank".takeIf { value.isBlank() }

    fun validateGender(value: String): String? =
        "Please select a gender".takeIf { value.isBlank() }
}
