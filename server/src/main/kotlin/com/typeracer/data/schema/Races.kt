package com.typeracer.data.schema

import org.jetbrains.exposed.sql.Table

object Races : Table() {
    val raceID = integer("RaceID").autoIncrement()
    val userID = integer("UserID").references(Users.userID)
    val textID = integer("TextID").references(Texts.textID)
    val date = text("Date")
    val wpm = integer("WPM")

    override val primaryKey = PrimaryKey(raceID)
}