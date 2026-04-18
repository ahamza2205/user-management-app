package com.aa.usermanagementapp.util

import com.aa.usermanagementapp.domain.model.User

/**
 * Factory function for creating [User] instances in tests.
 * All parameters have sensible defaults — override only what is relevant to each test.
 */
fun aUser(
    id: Int = 1,
    name: String = "John Doe",
    age: Int = 30,
    jobTitle: String = "Software Engineer",
    gender: String = "Male",
) = User(id = id, name = name, age = age, jobTitle = jobTitle, gender = gender)
