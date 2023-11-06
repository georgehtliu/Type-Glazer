package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.Races
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Race(val time: String, val wpm: Int)

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("YOOOOO xyz Received")
        }
        get("/getAllRaces") {
            val races = transaction {
                Races.selectAll().map {
                    Race(it[Races.time], it[Races.wpm])
                }
            }

            println("Retrieved races from the database: $races")
            call.respondText(Json.encodeToString(races), ContentType.Application.Json)
            // OR
            // call.respond(Json.encodeToString(races))
        }
    }
}
