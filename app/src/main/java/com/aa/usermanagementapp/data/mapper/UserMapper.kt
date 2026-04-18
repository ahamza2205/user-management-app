package com.aa.usermanagementapp.data.mapper

import com.aa.usermanagementapp.data.local.UserEntity
import com.aa.usermanagementapp.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    age = age,
    jobTitle = jobTitle,
    gender = gender,
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    age = age,
    jobTitle = jobTitle,
    gender = gender,
)
