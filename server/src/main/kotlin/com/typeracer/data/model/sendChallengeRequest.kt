package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SendChallengeRequest(val fromUserID: Int, val toUsername: String, val textID: Int, val raceID: Int)