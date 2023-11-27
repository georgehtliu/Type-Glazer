package com.typeracer.routes

import com.typeracer.data.schema.Users
import com.typeracer.plugins.configureRouting
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.typeracer.data.model.UserLogin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import kotlin.test.Test
import kotlin.test.assertEquals

//class AuthRouteTest {
//    @Test
//    fun testLoginRoute(): Unit = withTestApplication({
//        configureRouting()
//    }) {
//        transaction {
//            Users.insert {
//                it[username] = "BobSmith"
//                it[email] = "bobsmith@gmail.com"
//                it[password] = "bobpass"
//            }
//        }
//
//        val loginRequest = UserLogin("BobSmith", "bobpass")
//        handleRequest(HttpMethod.Post, "/login") {
//            setBody(Json.encodeToString(loginRequest))
//            addHeader("Content-Type", "application/json")
//        }.apply {
//            assertEquals(HttpStatusCode.Accepted, response.status())
//            assertEquals("1", response.content)
//        }
//    }
//}

// Using non deprecated package
//class AuthRouteTest {
//    @Test
//    fun testLoginRoute() = testApplication {
//        application {
//            configureRouting()
//        }
//        val response = client.post("/login") {
//            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//            setBody(
//                listOf(
//                    "username" to "BobSmith",
//                    "password" to "bobpass"
//                ).formUrlEncode()
//            )
//        }
//        assertEquals(HttpStatusCode.BadRequest, response.status)
//        assertEquals("Invalid username or password", response.bodyAsText())
//    }
//}

class AuthRouteTest {

    @Test
    fun testLoginRoute() = testApplication {

        application {
            configureRouting()
        }

        val loginRequest = UserLogin("BobSmith", "bobpass")

        val response = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(Json.encodeToString(loginRequest))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid username or password", response.bodyAsText())
    }
}


