package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InsertRaceRequest(val userID: Int, val textID: Int, val date: String, val wpm: Int)