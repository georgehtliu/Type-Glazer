package com.typeracer.data.schema

import org.jetbrains.exposed.sql.Table

object Challenges : Table() {
    val challengeID = integer("ChallengeID").autoIncrement()
    val textID = integer("TextID").references(Texts.textID)
    val fromUserID = integer("FromUserID").references(Users.userID)
    val toUserID = integer("ToUserID").references(Users.userID)
    val raceID = integer("RaceID").references(Races.raceID)

    override val primaryKey = PrimaryKey(challengeID)
}