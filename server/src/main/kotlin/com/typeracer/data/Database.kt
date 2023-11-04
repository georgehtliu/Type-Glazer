package com.typeracer.data

import com.typeracer.data.model.Race
import com.typeracer.module
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.coroutine.coroutine
import org.bson.types.ObjectId
import org.litote.kmongo.eq

val connectionString = "mongodb+srv://cs346User:GeorgeRayMarkCindy346@cs346.lptzrpu.mongodb.net/?retryWrites=true&w=majority"

private val client = KMongo.createClient(connectionString).coroutine
private val database = client.getDatabase("Races")

private val races = database.getCollection<Race>()

suspend fun getRaceForId(id: String) : Race? {
    return races.findOneById(id)
}

suspend fun createRaceOrUpdateRaceForId(race: Race) : Boolean {
    val raceExists = races.findOneById(race.id) != null
    return if (raceExists) {
        races.updateOneById(race.id, race).wasAcknowledged()
    } else {
        val newRace = race.copy(id = ObjectId().toString()) // Creating a copy of the race with a new ID
        races.insertOne(newRace).wasAcknowledged()
    }
}

suspend fun deleteRaceForId(raceId : String) : Boolean {
    val race = races.findOne(Race::id eq raceId)
    race?.let { race ->
        return races.deleteOneById(race.id).wasAcknowledged()
    } ?: return false
}