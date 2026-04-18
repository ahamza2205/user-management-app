package com.aa.usermanagementapp.data.mapper

import com.aa.usermanagementapp.data.local.UserEntity
import com.aa.usermanagementapp.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {

    // ─── Entity → Domain ──────────────────────────────────────────────────────

    @Test
    fun toDomain_mapsAllFieldsCorrectly() {
        val entity = UserEntity(
            id = 7,
            name = "Sara Ahmed",
            age = 29,
            jobTitle = "Product Manager",
            gender = "Female",
        )

        val domain = entity.toDomain()

        assertEquals(
            User(id = 7, name = "Sara Ahmed", age = 29, jobTitle = "Product Manager", gender = "Female"),
            domain,
        )
    }

    @Test
    fun toDomain_preservesId() {
        val entity = UserEntity(id = 42, name = "X", age = 1, jobTitle = "Y", gender = "Male")
        assertEquals(42, entity.toDomain().id)
    }

    // ─── Domain → Entity ──────────────────────────────────────────────────────

    @Test
    fun toEntity_mapsAllFieldsCorrectly() {
        val user = User(
            id = 3,
            name = "Khalid Omar",
            age = 35,
            jobTitle = "DevOps Engineer",
            gender = "Male",
        )

        val entity = user.toEntity()

        assertEquals(
            UserEntity(id = 3, name = "Khalid Omar", age = 35, jobTitle = "DevOps Engineer", gender = "Male"),
            entity,
        )
    }

    @Test
    fun toEntity_preservesId() {
        val user = User(id = 99, name = "X", age = 1, jobTitle = "Y", gender = "Female")
        assertEquals(99, user.toEntity().id)
    }

    // ─── Round-trip ───────────────────────────────────────────────────────────

    @Test
    fun toDomain_thenToEntity_returnsSameEntity() {
        val original = UserEntity(
            id = 5,
            name = "Lena Müller",
            age = 31,
            jobTitle = "UX Designer",
            gender = "Female",
        )

        val roundTripped = original.toDomain().toEntity()

        assertEquals(original, roundTripped)
    }

    @Test
    fun toEntity_thenToDomain_returnsSameUser() {
        val original = User(
            id = 12,
            name = "Omar Farouq",
            age = 44,
            jobTitle = "CTO",
            gender = "Male",
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
