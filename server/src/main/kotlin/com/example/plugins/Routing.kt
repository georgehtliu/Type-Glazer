package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.mongodb.reactivestreams.client.MongoCollection
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
@Serializable
data class Race(val number: Int, val wpm: Int) {
    // Secondary constructor
    constructor(number: String, wpm: String) : this(number.toInt(), wpm.toInt())
}

fun Application.configureRouting(userCollection: MongoCollection<Document>) {
    routing {
        get("/") {
            call.respondText("Received")
        }

        get("/races") {
//            val races = withContext(Dispatchers.IO) {
//                val raceList = mutableListOf<Race>()
//                userCollection.find().forEach { document ->
//                    val race = Race(
//                        document.getInteger("number"),
//                        document.getInteger("wpm")
//                    )
//                    raceList.add(race)
//                }
//                raceList
//            }
//            call.respond(races)
        }


        post("/races") {
            println("HELLOOOFOIJSJODSJFOISFJgoivejfi")

            try {

                println("lsjfsdkfjlasdjf")

                val race = call.receiveText()
                val raceObject = Json.decodeFromString<Race>(race)

                // println("HELLOOOFOIJSJODSJFOISFJgoivejfi")
                println(race)
                println(race)
                println(race)
                println(race)
                println(race)
                println(race)

//
//
//                val document = Document().apply {
//                    append("number", race.number)
//                    append("wpm", race.wpm)
//                }
//
//                val result = userCollection.insertOne(document)
//
//                call.respond("Race added with number: ${race.number}")
            } catch (e: Exception) {
                call.respond("Failed to add the race. Error: ${e.message}")
            }
        }
    }
}
