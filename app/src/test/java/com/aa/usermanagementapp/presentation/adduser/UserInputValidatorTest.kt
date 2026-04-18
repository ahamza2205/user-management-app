package com.aa.usermanagementapp.presentation.adduser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UserInputValidatorTest {

    private lateinit var validator: UserInputValidator

    @Before
    fun setUp() {
        validator = UserInputValidator()
    }

    // ─── Name ────────────────────────────────────────────────────────────────

    @Test
    fun validateName_withEmptyString_returnsError() {
        assertNotNull(validator.validateName(""))
    }

    @Test
    fun validateName_withBlankWhitespace_returnsError() {
        assertNotNull(validator.validateName("   "))
    }

    @Test
    fun validateName_withValidName_returnsNull() {
        assertNull(validator.validateName("John Doe"))
    }

    @Test
    fun validateName_withSingleCharacter_returnsNull() {
        assertNull(validator.validateName("J"))
    }

    // ─── Age ─────────────────────────────────────────────────────────────────

    @Test
    fun validateAge_withEmptyString_returnsError() {
        assertNotNull(validator.validateAge(""))
    }

    @Test
    fun validateAge_withNonNumericInput_returnsError() {
        assertNotNull(validator.validateAge("abc"))
    }

    @Test
    fun validateAge_withDecimalInput_returnsError() {
        assertNotNull(validator.validateAge("25.5"))
    }

    @Test
    fun validateAge_withZero_returnsError() {
        assertNotNull(validator.validateAge("0"))
    }

    @Test
    fun validateAge_withNegativeValue_returnsError() {
        assertNotNull(validator.validateAge("-1"))
    }

    @Test
    fun validateAge_withValueAbove120_returnsError() {
        assertNotNull(validator.validateAge("121"))
    }

    @Test
    fun validateAge_withBoundaryValue1_returnsNull() {
        assertNull(validator.validateAge("1"))
    }

    @Test
    fun validateAge_withBoundaryValue120_returnsNull() {
        assertNull(validator.validateAge("120"))
    }

    @Test
    fun validateAge_withTypicalValue_returnsNull() {
        assertNull(validator.validateAge("28"))
    }

    // ─── Job Title ───────────────────────────────────────────────────────────

    @Test
    fun validateJobTitle_withEmptyString_returnsError() {
        assertNotNull(validator.validateJobTitle(""))
    }

    @Test
    fun validateJobTitle_withBlankWhitespace_returnsError() {
        assertNotNull(validator.validateJobTitle("   "))
    }

    @Test
    fun validateJobTitle_withValidTitle_returnsNull() {
        assertNull(validator.validateJobTitle("Software Engineer"))
    }

    // ─── Gender ──────────────────────────────────────────────────────────────

    @Test
    fun validateGender_withEmptyString_returnsError() {
        assertNotNull(validator.validateGender(""))
    }

    @Test
    fun validateGender_withMale_returnsNull() {
        assertNull(validator.validateGender("Male"))
    }

    @Test
    fun validateGender_withFemale_returnsNull() {
        assertNull(validator.validateGender("Female"))
    }

    // ─── Error messages ──────────────────────────────────────────────────────

    @Test
    fun validateName_withBlankInput_returnsExpectedMessage() {
        assertEquals("Name cannot be blank", validator.validateName(""))
    }

    @Test
    fun validateAge_withEmptyInput_returnsExpectedMessage() {
        assertEquals("Age is required", validator.validateAge(""))
    }

    @Test
    fun validateAge_withNonNumeric_returnsExpectedMessage() {
        assertEquals("Enter a valid number", validator.validateAge("abc"))
    }

    @Test
    fun validateAge_withZero_returnsExpectedMessage() {
        assertEquals("Age must be greater than 0", validator.validateAge("0"))
    }

    @Test
    fun validateJobTitle_withBlankInput_returnsExpectedMessage() {
        assertEquals("Job title cannot be blank", validator.validateJobTitle(""))
    }

    @Test
    fun validateGender_withEmptyInput_returnsExpectedMessage() {
        assertEquals("Please select a gender", validator.validateGender(""))
    }
}
