package com.typeracer.data.schema

import org.jetbrains.exposed.sql.Table

object Texts : Table() {
    val textID = integer("TextID").autoIncrement()
    val content = text("Content")

    override val primaryKey = PrimaryKey(textID)
}