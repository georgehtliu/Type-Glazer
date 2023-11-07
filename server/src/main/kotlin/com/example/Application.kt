package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userID = integer("UserId").autoIncrement()
    val username = varchar("Username", length = 50)
    val email = varchar("Email", length = 100)
    val password = varchar("Password", length = 100)

    override val primaryKey = PrimaryKey(userID)
}

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
