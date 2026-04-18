package com.aa.usermanagementapp.domain.validation

import javax.inject.Inject

/**
 * Stateless validator for the Add User form fields.
 * Lives in the domain layer because validation rules (field formats, acceptable ranges)
 * are business rules — independent of any UI framework or presentation concern.
 */
class UserInputValidator @Inject constructor() {

    fun validateName(value: String): String? =
        "Name cannot be blank".takeIf { value.isBlank() }

    fun validateAge(value: String): String? {
        if (value.isBlank()) return "Age is required"
        val age = value.toIntOrNull() ?: return "Enter a valid number"
        if (age <= 0) return "Age must be greater than 0"
        if (age > 120) return "Enter a realistic age"
        return null
    }

    fun validateJobTitle(value: String): String? =
        "Job title cannot be blank".takeIf { value.isBlank() }

    fun validateGender(value: String): String? =
        "Please select a gender".takeIf { value.isBlank() }
}
