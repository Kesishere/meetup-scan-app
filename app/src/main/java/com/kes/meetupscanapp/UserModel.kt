package com.kes.meetupscanapp

data class UserModel(
    val user: User,
    val valid: Boolean,
    val lastCheck: LastCheck
)