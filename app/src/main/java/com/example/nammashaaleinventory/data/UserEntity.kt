package com.example.nammashaaleinventory.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val fullName: String,
    val username: String,
    val password: String,
    val role: String, // "Admin" or "Teacher"
    val department: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isDefaultAccount: Boolean = false
)
