package com.typeracer.routes
import com.typeracer.data.model.InsertResultRequest
import com.typeracer.data.model.Result
import com.typeracer.data.schema.Results
import com.typeracer.data.schema.Races
import com.typeracer.data.schema.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.resultRoutes() {

    // get result by userID
    get("/getResult") {
        val userID = call.parameters["userID"]?.toIntOrNull()

        if (userID != null) {
            val results = transaction {
                Results.select {
                    (Results.user1ID eq userID) or (Results.user2ID eq userID)
                }.map {
                    val user1ID = it[Results.user1ID]
                    val user2ID = it[Results.user2ID]
                    val user1WPM = it[Results.user1WPM]
                    val user2WPM = it[Results.user2WPM]

                    val username1 = Users.select { Users.userID eq user1ID }.single()[Users.username]
                    val username2 = Users.select { Users.userID eq user2ID }.single()[Users.username]

                    Result(
                        it[Results.resultID],
                        userID,  // Set userID to user1ID
                        if (user1ID == userID) user2ID else user1ID,  // Set otherUserID
                        if (user1ID == userID) user1WPM else user2WPM,  // Set wpm
                        if (user1ID == userID) user2WPM else user1WPM,   // Set otherUserWPM
                        if (user1ID == userID) username1 else username2,  // Set username1
                        if (user1ID == userID) username2 else username1   // Set username2
                    )
                }
            }
            call.respondText(Json.encodeToString(results), ContentType.Application.Json)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid or missing userID")
        }
    }

    // need fromRaceID, toRaceID
    post("/insertResult") {
        val resultRequest = call.receive<InsertResultRequest>()
        val fromRaceID = resultRequest.fromRaceID
        val toRaceID = resultRequest.toRaceID

        transaction {
            val fromRace = Races.select { Races.raceID eq fromRaceID }.single()
            val toRace = Races.select { Races.raceID eq toRaceID }.single()

            val user1ID = fromRace[Races.userID]
            val user2ID = toRace[Races.userID]
            val user1WPM = fromRace[Races.wpm]
            val user2WPM = toRace[Races.wpm]

            Results.insert {
                it[Results.user1ID] = user1ID
                it[Results.user2ID] = user2ID
                it[Results.user1WPM] = user1WPM
                it[Results.user2WPM] = user2WPM
            }
        }
    }


}