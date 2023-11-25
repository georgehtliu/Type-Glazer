package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InsertResultRequest(val fromRaceID: Int, val toRaceID: Int)