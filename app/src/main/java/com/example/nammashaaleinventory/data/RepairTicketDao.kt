package com.example.nammashaaleinventory.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairTicketDao {
    @Query("SELECT * FROM repair_tickets ORDER BY createdDate DESC")
    fun getAllTickets(): Flow<List<RepairTicketEntity>>

    @Query("SELECT * FROM repair_tickets WHERE raisedByUserId = :userId ORDER BY createdDate DESC")
    fun getTicketsByUser(userId: Int): Flow<List<RepairTicketEntity>>

    @Query("SELECT * FROM repair_tickets WHERE assetId = :assetId ORDER BY createdDate DESC")
    fun getTicketsByAsset(assetId: Int): Flow<List<RepairTicketEntity>>

    @Query("SELECT * FROM repair_tickets WHERE status != 'Completed' AND dueDate < :currentTime")
    fun getOverdueTickets(currentTime: Long): Flow<List<RepairTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: RepairTicketEntity)

    @Update
    suspend fun updateTicket(ticket: RepairTicketEntity)

    @Query("DELETE FROM repair_tickets WHERE ticketId = :ticketId")
    suspend fun deleteTicket(ticketId: Int)

    @Query("SELECT COUNT(*) FROM repair_tickets WHERE status = 'Pending'")
    fun getPendingTicketsCount(): Flow<Int>
}
