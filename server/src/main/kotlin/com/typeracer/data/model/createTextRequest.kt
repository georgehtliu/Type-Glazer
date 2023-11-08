package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTextRequest(val content: String)