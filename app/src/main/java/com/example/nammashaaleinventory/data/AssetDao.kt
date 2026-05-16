package com.example.nammashaaleinventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY lastAuditDate DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE qrCodeHash = :qrCodeHash LIMIT 1")
    suspend fun getAssetByQrCode(qrCodeHash: String): AssetEntity?

    @Query("SELECT * FROM assets WHERE id = :id LIMIT 1")
    fun getAssetByIdFlow(id: Int): Flow<AssetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)
    
    @Query("SELECT COUNT(*) FROM assets")
    fun getTotalAssetsCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM assets WHERE condition = 'Broken'")
    fun getBrokenAssetsCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM assets WHERE condition = 'Repair'")
    fun getRepairAssetsCount(): Flow<Int>
}
