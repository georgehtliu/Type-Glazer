package com.typeracer.data.schema

import org.jetbrains.exposed.sql.Table

object Results : Table() {
    val resultID = integer("ResultID").autoIncrement()
    val userID = integer("UserID").references(Users.userID)
    val raceID = integer("RaceID").references(Races.raceID)
    val wpm = integer("WPM")

    override val primaryKey = PrimaryKey(resultID)
}