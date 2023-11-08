package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Race(val raceID: Int, val textID: Int, val startTime: String, val endTime: String)