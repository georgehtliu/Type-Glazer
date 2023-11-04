package com.typeracer.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.typeracer.routes.raceRoutes

fun Application.configureRouting() {
    routing {
        raceRoutes()
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
