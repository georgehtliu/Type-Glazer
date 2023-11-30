package com.typeracer

import com.typeracer.plugins.configureRouting
import com.typeracer.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(Netty, port = 5050, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
//    Database.connect("jdbc:sqlite:/Users/georgeliu/typeracer.db", driver = "org.sqlite.JDBC")
//    transaction {
//        exec("DROP TABLE IF EXISTS Users")
//        exec("DROP TABLE IF EXISTS Races")
//        exec("DROP TABLE IF EXISTS Texts")
//        exec("DROP TABLE IF EXISTS Challenges")
//        exec("DROP TABLE IF EXISTS Results")
//    }
//
//    // Create new tables
//    transaction {
//        SchemaUtils.create(Users, Races, Texts, Challenges, Results)
//    }

    Database.connect(
        url = "jdbc:postgresql://db-cs346-do-user-9272876-0.c.db.ondigitalocean.com:25060/defaultdb",
        driver = "org.postgresql.Driver",
        user = "doadmin",
        password = "AVNS_sMG6QJXTtTksksEmNoR"
    )

    configureRouting()
    configureSerialization()
}
