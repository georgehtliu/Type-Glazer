package com.typeracer.routes

import com.typeracer.data.model.CreateTextRequest
import com.typeracer.data.schema.Texts
import com.typeracer.data.model.Text
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

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
}