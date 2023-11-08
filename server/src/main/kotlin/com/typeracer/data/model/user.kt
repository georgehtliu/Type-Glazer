package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(val userID: Int, val username: String, val email: String, val password: String)