package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.Users
import io.ktor.http.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import io.ktor.server.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert

@Serializable
data class User(val userID: Int, val username: String, val email: String, val password: String)
@Serializable
data class CreateUserRequest(val username: String, val email: String, val password: String)

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("YOOOOO xyz Received")
        }

        get("/users") {
            val users = transaction {
                // Retrieve all users from the Users table
                Users.selectAll().map {
                    User(it[Users.userID], it[Users.username], it[Users.email], it[Users.password])
                }
            }
            call.respondText(Json.encodeToString(users), ContentType.Application.Json)
        }

        post("/createNewUser") {

            val user = call.receive<CreateUserRequest>()
            val username = user.username
            val email = user.email
            val password = user.password

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val newUser = transaction {
                    Users.insert {
                        it[Users.username] = username
                        it[Users.email] = email
                        it[Users.password] = password
                    }
                }

                call.respondText("User created successfully", status = HttpStatusCode.Created)
            } else {
                call.respondText("Invalid user data", status = HttpStatusCode.BadRequest)
            }
        }


    }
}
