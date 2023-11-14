package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Race(val raceID: Int, val userID: Int, val textID: Int, val date: String, val wpm: Int)