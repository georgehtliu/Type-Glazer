package com.typeracer.routes

import com.typeracer.data.schema.Users
import com.typeracer.data.model.User
import com.typeracer.data.model.CreateUserRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.userRoutes() {

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

            val foundUser = transaction {
                Users.select { (Users.username eq username) and (Users.password eq password) }
                    .map { User(it[Users.userID], it[Users.username], it[Users.email], it[Users.password]) }
                    .singleOrNull()
            }

            print(foundUser?.userID)

            call.respondText(Json.encodeToString(foundUser?.userID), ContentType.Application.Json, status = HttpStatusCode.Created)
        } else {
            call.respondText("Invalid user data", status = HttpStatusCode.BadRequest)
        }
    }
}