package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table

object Races : Table() {
    val time = varchar("Time", 255)
    val wpm = integer("WPM")
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    Database.connect("jdbc:sqlite:/Users/markliu/main.db", driver = "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(Races)
    }

    configureRouting()
}
