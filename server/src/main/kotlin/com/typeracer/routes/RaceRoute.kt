package com.typeracer.routes

import com.typeracer.data.createRaceOrUpdateRaceForId
import com.typeracer.data.deleteRaceForId
import com.typeracer.data.getRaceForId
import com.typeracer.data.model.Race
import com.typeracer.data.requests.DeleteRaceRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import com.typeracer.data.requests.RaceRequest
import io.ktor.http.*
import io.ktor.server.response.*

fun Route.raceRoutes() {

    route("/get-race") {
        get {
            val raceId = call.receive<RaceRequest>().id
            val race = getRaceForId(raceId)
            race?.let {
                call.respond(
                    HttpStatusCode.OK,
                    it
                )
            } ?: call.respond(
                HttpStatusCode.OK,
                "There is no employee with this id"
            )
        }
    }

    route("/create-update-race") {
        post {
            val request = try {
                call.receive<Race>()
            } catch (e : ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (createRaceOrUpdateRaceForId(request)) {
                call.respond(
                    HttpStatusCode.OK,
                    "Race successfully created/updated"
                )
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }

    route("/delete-race") {
        post {
            val request = try {
                call.receive<DeleteRaceRequest>()
            } catch (e : ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (deleteRaceForId(request.id)) {
                call.respond(
                    HttpStatusCode.OK,
                    "Race successfully deleted"
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    "Race not found"
                )
            }
        }
    }
}