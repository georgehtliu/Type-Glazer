package com.typeracer.data.model
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val resultID: Int,
    val user1ID: Int,
    val user2ID: Int,
    val user1WPM: Int,
    val user2WPM: Int
)