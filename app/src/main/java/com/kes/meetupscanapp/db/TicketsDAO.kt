package com.kes.meetupscanapp.db

import androidx.room.*

@Dao
interface TicketsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tickets: List<TicketEntity>)

    @Query("SELECT * FROM tickets WHERE code = :code")
    fun getById(code: Long): TicketEntity

    @Update
    suspend fun update(ticket: TicketEntity)
}