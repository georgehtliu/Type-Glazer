package com.typeracer.routes
import Challenge
import com.typeracer.data.model.SendChallengeRequest
import com.typeracer.data.schema.Challenges
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.typeracer.data.schema.Users
import io.ktor.http.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.deleteWhere

@Serializable
data class ChallengeWithUsername(
    val challengeID: Int,
    val fromUsername: String,
    val toUserID: Int,
    val textID: Int,
    val raceID: Int
)


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

    // Function to retrieve the username by user ID
    fun getUsernameById(userId: Int): String {
        return transaction {
            Users.select { Users.userID eq userId }
                .singleOrNull()
                ?.let { it[Users.username] }
                ?: "Unknown User"
        }
    }

    // get all challenges where toUserID is equal to the url param
    get("/challenges/get") {
        val userId = call.parameters["userId"]?.toIntOrNull()

        println(userId)

        if (userId != null) {
            try {
                val challengesWithUsername = transaction {
                    Challenges.select { Challenges.toUserID eq userId }
                        .map {
                            ChallengeWithUsername(
                                it[Challenges.challengeID],
                                getUsernameById(it[Challenges.fromUserID]),
                                it[Challenges.toUserID],
                                it[Challenges.textID],
                                it[Challenges.raceID]
                            )
                        }
                }
                call.respondText(Json.encodeToString(challengesWithUsername), ContentType.Application.Json)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving challenges: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID parameter")
        }
    }


    // delete challenge when complete
    delete("/challenges/delete") {
        val challengeID = call.parameters["challengeId"]?.toIntOrNull()
        if (challengeID != null) {
            transaction {
                // Use the Challenges table to delete the challenge by ID
                Challenges.deleteWhere { Challenges.challengeID eq challengeID }
            }

            call.respondText("Challenge with ID $challengeID deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Invalid challenge ID", status = HttpStatusCode.BadRequest)
        }
    }
}