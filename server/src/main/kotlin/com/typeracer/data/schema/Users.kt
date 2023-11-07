package com.typeracer.data.schema

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userID = integer("UserId").autoIncrement()
    val username = varchar("Username", length = 50)
    val email = varchar("Email", length = 100)
    val password = varchar("Password", length = 100)

    override val primaryKey = PrimaryKey(userID)
}