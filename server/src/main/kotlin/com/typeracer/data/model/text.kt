package com.typeracer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Text(val textID: Int, val content: String)