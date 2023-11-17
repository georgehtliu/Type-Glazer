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
    Database.connect("jdbc:sqlite:/Users/cindygu/typeracer.db", driver = "org.sqlite.JDBC")

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

        Users.insert {
            it[username] = "JaneDoe"
            it[email] = "jane@example.com"
            it[password] = "pass456"
        }

        Users.insert {
            it[username] = "BobSmith"
            it[email] = "bob@example.com"
            it[password] = "bobpass"
        }

        Races.insert {
            it[userID] = 1
            it[textID] = 1
            it[date] = "2023-01-01"
            it[wpm] = 60
        }

        Races.insert {
            it[userID] = 2
            it[textID] = 1
            it[date] = "2023-02-01"
            it[wpm] = 75
        }

        Races.insert {
            it[userID] = 3
            it[textID] = 1
            it[date] = "2023-03-01"
            it[wpm] = 80
        }

        Texts.insert {
            it[content] = "Sample text for typing"
        }

        Texts.insert {
            it[content] = "The quick brown fox jumped over the lazy dog and cat and mouse and fish"
        }

        Texts.insert {
            it[content] = "The sun is shining, the birds are singing, and the flowers are blooming"
        }

        Challenges.insert {
            it[textID] = 1
            it[fromUserID] = 1
            it[toUserID] = 2
            it[raceID] = 1
        }

        Challenges.insert {
            it[textID] = 1
            it[fromUserID] = 2
            it[toUserID] = 3
            it[raceID] = 2
        }

        Challenges.insert {
            it[textID] = 1
            it[fromUserID] = 3
            it[toUserID] = 1
            it[raceID] = 3
        }
    }

    configureRouting()
    configureSerialization()
}
