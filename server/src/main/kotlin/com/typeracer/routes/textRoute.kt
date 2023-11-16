package com.typeracer.routes

import com.typeracer.data.model.CreateTextRequest
import com.typeracer.data.schema.Texts
import com.typeracer.data.model.Text
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

fun Route.textRoutes() {

    get("/texts") {
        val texts = transaction {
            // Retrieve all texts from the Text table
            Texts.selectAll().map {
                Text(it[Texts.textID], it[Texts.content])
            }
        }
        call.respondText(Json.encodeToString(texts), ContentType.Application.Json)
    }

    post("/createNewText") {

        val text = call.receive<CreateTextRequest>()
        val content = text.content
        val newText = transaction {
            Texts.insert {
                it[Texts.content] = content
            }
        }

        call.respondText("Text created successfully", status = HttpStatusCode.Created)
    }

    get("/texts/") {
        val textID = call.parameters["textID"]?.toIntOrNull()
        if (textID != null) {
            var text: ResultRow? = null
            withContext(Dispatchers.IO) {
                transaction {
                    text = Texts.select { Texts.textID eq textID }.singleOrNull()
                }
            }
            if (text != null) {
                call.respond(HttpStatusCode.OK, text!![Texts.content])
            } else {
                call.respond(HttpStatusCode.NotFound, "Text not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid textID format")
        }
    }

    get("/texts/random") {
        var text: ResultRow? = null
        withContext(Dispatchers.IO) {
            transaction {
                val count = Texts.selectAll().count().toInt()
                val randomID = Random.nextInt(1, count + 1)
                text = Texts.select { Texts.textID eq randomID }.singleOrNull()
            }
        }
        if (text != null) {
            call.respond(HttpStatusCode.OK, text!![Texts.content])
        } else {
            call.respond(HttpStatusCode.NotFound, "Text not found")
        }
    }


}