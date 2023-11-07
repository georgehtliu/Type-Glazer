package com.typeracer.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.typeracer.routes.userRoutes


fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Received")
        }
        userRoutes()
    }
}
