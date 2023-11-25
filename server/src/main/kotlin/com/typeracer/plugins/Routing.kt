package com.typeracer.plugins

import com.typeracer.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Received")
        }
        userRoutes()
        textRoutes()
        authRoutes()
        raceRoutes()
        challengeRoutes()
        resultRoutes()
    }
}
