package com.example.nammashaaleinventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repair_tickets")
data class RepairTicketEntity(
    @PrimaryKey(autoGenerate = true)
    val ticketId: Int = 0,
    val assetId: Int?, // Nullable for unregistered assets
    val customAssetName: String? = null,
    val customAssetCategory: String? = null,
    val isUnregisteredAsset: Boolean = false,
    val issueDescription: String,
    val priority: String, // "Low", "Medium", "High", "Critical"
    val assignedTo: String,
    val status: String, // "Pending", "In Progress", "Completed", "Delayed"
    val createdDate: Long = System.currentTimeMillis(),
    val dueDate: Long?,
    val completedDate: Long? = null,
    val raisedByUserId: Int,
    val repairNotes: String = ""
)
