package com.typeracer.data.schema

import org.jetbrains.exposed.sql.Table

object Races : Table() {
    val raceID = integer("RaceID").autoIncrement()
    val textID = integer("TextID").references(Texts.textID)
    val startTime = text("StartTime")
    val endTime = text("EndTime")

    override val primaryKey = PrimaryKey(raceID)
}