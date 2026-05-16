package com.example.nammashaaleinventory.data

import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val assetDao: AssetDao,
    private val categoryDao: CategoryDao,
    private val userDao: UserDao,
    private val repairTicketDao: RepairTicketDao
) {
    // Categories
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    suspend fun insertCategory(category: CategoryEntity) = categoryDao.insertCategory(category)
    suspend fun getCategoryByName(name: String): CategoryEntity? = categoryDao.getCategoryByName(name)
    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    // Users
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun getUserById(userId: Int): UserEntity? = userDao.getUserById(userId)
    suspend fun getUserByUsername(username: String): UserEntity? = userDao.getUserByUsername(username)
    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)
    suspend fun checkUsernameExists(username: String): Boolean = userDao.checkUsernameExists(username)
    suspend fun deleteUserById(userId: Int) = userDao.deleteUserById(userId)

    // Repair Tickets
    fun getAllTickets(): Flow<List<RepairTicketEntity>> = repairTicketDao.getAllTickets()
    fun getTicketsByUser(userId: Int): Flow<List<RepairTicketEntity>> = repairTicketDao.getTicketsByUser(userId)
    fun getTicketsByAsset(assetId: Int): Flow<List<RepairTicketEntity>> = repairTicketDao.getTicketsByAsset(assetId)
    fun getPendingTicketsCount(): Flow<Int> = repairTicketDao.getPendingTicketsCount()
    suspend fun insertTicket(ticket: RepairTicketEntity) = repairTicketDao.insertTicket(ticket)
    suspend fun updateTicket(ticket: RepairTicketEntity) = repairTicketDao.updateTicket(ticket)
    suspend fun deleteTicket(ticketId: Int) = repairTicketDao.deleteTicket(ticketId)

    // Assets
    fun getAllAssets(): Flow<List<AssetEntity>> = assetDao.getAllAssets()
    
    fun getAssetByIdStream(id: Int): Flow<AssetEntity?> = assetDao.getAssetByIdFlow(id)

    suspend fun getAssetByQrCode(qrCodeHash: String): AssetEntity? = assetDao.getAssetByQrCode(qrCodeHash)

    suspend fun isQrCodeUnique(qrCodeHash: String, currentAssetId: Int): Boolean {
        if (qrCodeHash.isBlank()) return true
        val asset = assetDao.getAssetByQrCode(qrCodeHash)
        return asset == null || asset.id == currentAssetId
    }

    suspend fun insertAsset(asset: AssetEntity) = assetDao.insertAsset(asset)

    suspend fun updateAsset(asset: AssetEntity) = assetDao.updateAsset(asset)

    suspend fun deleteAsset(asset: AssetEntity) = assetDao.deleteAsset(asset)
    
    fun getTotalAssetsCount(): Flow<Int> = assetDao.getTotalAssetsCount()
    
    fun getBrokenAssetsCount(): Flow<Int> = assetDao.getBrokenAssetsCount()
    
    fun getRepairAssetsCount(): Flow<Int> = assetDao.getRepairAssetsCount()
}
