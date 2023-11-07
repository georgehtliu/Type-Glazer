package com.typeracer

import com.typeracer.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(Netty, port = 5050, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    Database.connect("jdbc:sqlite:/Users/georgeliu/typeracer.db", driver = "org.sqlite.JDBC")

//    transaction {
//        SchemaUtils.create(Users)
//    }

    configureRouting()
    configureSerialization()
}
