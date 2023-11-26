package com.typeracer

import com.typeracer.data.schema.*
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
        exec("DROP TABLE IF EXISTS Results")
    }

    // Create new tables
    transaction {
        SchemaUtils.create(Users, Races, Texts, Challenges, Results)
    }

    configureRouting()
    configureSerialization()
}
