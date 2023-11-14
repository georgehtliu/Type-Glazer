package com.typeracer

import com.typeracer.data.schema.Challenges
import com.typeracer.data.schema.Races
import com.typeracer.data.schema.Texts
import com.typeracer.data.schema.Users
import com.typeracer.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 5050, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    Database.connect("jdbc:sqlite:/Users/georgeliu/typeracer.db", driver = "org.sqlite.JDBC")

    // Perform actions when the database connects
    // tmp change
    transaction {
        exec("DROP TABLE IF EXISTS Users")
        exec("DROP TABLE IF EXISTS Races")
        exec("DROP TABLE IF EXISTS Texts")
        exec("DROP TABLE IF EXISTS Challenges")
    }

    // Create new tables
    transaction {
        SchemaUtils.create(Users, Races, Texts, Challenges)

        // Insert sample data
        Users.insert {
            it[username] = "JohnDoe"
            it[email] = "john@example.com"
            it[password] = "password123"
        }

        Races.insert {
            it[userID] = 1
            it[textID] = 1
            it[date] = "2023-01-01"
            it[wpm] = 60
        }

        Texts.insert {
            it[content] = "Sample text for typing"
        }

        Challenges.insert {
            it[textID] = 1
            it[fromUserID] = 1
            it[toUserID] = 2
            it[raceID] = 1
        }
    }

    configureRouting()
    configureSerialization()
}
