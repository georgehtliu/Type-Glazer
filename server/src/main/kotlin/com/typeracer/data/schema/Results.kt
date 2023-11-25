package com.typeracer.data.schema

import org.jetbrains.exposed.sql.Table

object Results : Table() {
    val resultID = integer("ResultID").autoIncrement()
    val user1ID = integer("User1ID").references(Users.userID)
    val user2ID = integer("User2ID").references(Users.userID)
    val user1WPM = integer("User1WPM")
    val user2WPM = integer("User2WPM")
    override val primaryKey = PrimaryKey(resultID)
}