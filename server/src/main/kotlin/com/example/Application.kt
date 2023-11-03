package com.example

import com.example.plugins.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.runBlocking

fun main() {
    println("hello")
    // Replace the placeholders with your credentials and hostname
    val connectionString = "mongodb+srv://cs346User:GeorgeRayMarkCindy346@cs346.lptzrpu.mongodb.net/?retryWrites=true&w=majority"
    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    val mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .serverApi(serverApi)
        .build()

    val mongoClient = MongoClients.create(mongoClientSettings)
    val database = mongoClient.getDatabase("cs346")
    val userCollection = database.getCollection("races")

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting(userCollection)
    }.start(wait = true)
}
