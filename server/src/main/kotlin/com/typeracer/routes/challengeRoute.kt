package com.typeracer.routes
import com.typeracer.data.model.SendChallengeRequest
import com.typeracer.data.schema.Challenges
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.typeracer.data.schema.Users
import io.ktor.http.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

fun Route.challengeRoutes() {

    post("/challenges/send") {
        try {
            val sendChallengeRequest = call.receive<SendChallengeRequest>()
            val fromUserID = sendChallengeRequest.fromUserID
            val toUsername = sendChallengeRequest.toUsername
            val textID = sendChallengeRequest.textID
            val raceID = sendChallengeRequest.raceID

            // first obtain toUserID
            val toUserID = transaction {
                Users.select { Users.username eq toUsername }
                    .singleOrNull()?.get(Users.userID)
            }

            if (toUserID != null) {
                // Use toUserID as needed
                println("toUserID: $toUserID")
            } else {
                throw NoSuchElementException("User with toUsername $toUsername not found")
            }

            // now store the entry
            transaction {
                Challenges.insert {
                    it[Challenges.textID] = textID
                    it[Challenges.fromUserID] = fromUserID
                    it[Challenges.toUserID] = toUserID
                    it[Challenges.raceID] = raceID
                }
            }

            call.respondText("Challenge sent successfully", status = HttpStatusCode.Created)
        } catch (e: Exception) {
            // Handle the exception and return the appropriate status code
            when (e) {
                is NoSuchElementException -> call.respondText("User not found", status = HttpStatusCode.NotFound)
                else -> call.respondText("Failed to send challenge", status = HttpStatusCode.InternalServerError)
            }
        }
    }

    // url has user id parameter
    get("/challenges/get") {

    }
}