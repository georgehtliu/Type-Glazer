package com.typeracer.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Race (
    val raceNumber: Int,
    val wpm : Int,
    @BsonId
    val id: String = ObjectId().toString()
)