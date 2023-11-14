package com.typeracer.routes
import com.typeracer.data.model.InsertRaceRequest
import com.typeracer.data.model.Race
import com.typeracer.data.schema.Races
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.raceRoutes() {

    // get race by userID
    get("/races") {
        val userID = call.parameters["userID"]?.toIntOrNull()

        if (userID != null) {
            val races = transaction {
                Races.select { Races.userID eq userID }.map {
                    Race(
                        it[Races.raceID],
                        it[Races.userID],
                        it[Races.textID],
                        it[Races.date],
                        it[Races.wpm]
                    )
                }
            }

            call.respond(mapOf("races" to races))
        } else {
            call.respondText("Invalid userID parameter", status = HttpStatusCode.BadRequest)
        }
    }

    // insert newly completed race
    // respond with the raceID
    // body: userID, textID, date, wpm
    post("/insertRace") {
        val raceRequest = call.receive<InsertRaceRequest>()
        val userID = raceRequest.userID
        val textID = raceRequest.textID
        val date = raceRequest.date
        val wpm = raceRequest.wpm

        // Insert the race without getting the ID
        transaction {
            Races.insert {
                it[Races.userID] = userID
                it[Races.textID] = textID
                it[Races.date] = date
                it[Races.wpm] = wpm
            }
        }

        // Fetch the generated raceID using a separate query
        val raceID = transaction {
            Races.select {
                (Races.userID eq userID) and
                        (Races.textID eq textID) and
                        (Races.date eq date) and
                        (Races.wpm eq wpm)
            }.singleOrNull()?.get(Races.raceID)
        }

        // Respond with JSON containing raceID, userID, and textID if successful
        if (raceID != null) {
            val jsonResponse = mapOf(
                "raceID" to raceID,
                "userID" to userID,
                "textID" to textID
            )
            call.respond(jsonResponse)
        } else {
            call.respondText("Failed to retrieve RaceID", status = HttpStatusCode.InternalServerError)
        }
    }

}