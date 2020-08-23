package com.kes.meetupscanapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey
    val code: String,
    var isActivated: Boolean,
    var activationTime: String
)