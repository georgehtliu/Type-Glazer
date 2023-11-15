package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class deleteChallengeRequest(val challengeID: Int)