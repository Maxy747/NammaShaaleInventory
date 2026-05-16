package com.example.nammashaaleinventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String, // e.g. "Tablet", "Furniture", "Lab Tool", "Sports Kit"
    val condition: String, // e.g. "Working", "Broken", "Repair"
    val qrCodeHash: String,
    val lastAuditDate: Long
)
