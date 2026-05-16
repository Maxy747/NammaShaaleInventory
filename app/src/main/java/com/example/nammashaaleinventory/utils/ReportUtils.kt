package com.example.nammashaaleinventory.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.nammashaaleinventory.data.AssetEntity
import com.example.nammashaaleinventory.data.RepairTicketEntity
import java.io.File
import java.io.FileOutputStream

object ReportUtils {

    fun generateInventoryCsvContent(assets: List<AssetEntity>): String {
        val header = "ID,Name,Category,Condition,QR Code,Last Audit\n"
        return header + assets.joinToString("\n") {
            "${it.id},${it.name},${it.category},${it.condition},${it.qrCodeHash},${DateUtils.formatDate(it.lastAuditDate)}"
        }
    }

    fun generateTicketsCsvContent(tickets: List<RepairTicketEntity>): String {
        val header = "TicketID,AssetID,Issue,Priority,AssignedTo,Status,Created,Due\n"
        return header + tickets.joinToString("\n") {
            "${it.ticketId},${it.assetId},${it.issueDescription},${it.priority},${it.assignedTo},${it.status},${DateUtils.formatDate(it.createdDate)},${it.dueDate?.let { d -> DateUtils.formatDate(d) } ?: "N/A"}"
        }
    }

    fun shareCsvReport(context: Context, content: String, title: String) {
        val fileName = "${title.replace(" ", "_").lowercase()}_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            FileOutputStream(file).use { it.write(content.toByteArray()) }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing report: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun writeContentToUri(context: Context, uri: Uri, content: String) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            Toast.makeText(context, "File saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Keep for legacy compatibility if needed
    fun exportInventoryToCsv(context: Context, assets: List<AssetEntity>) {
        shareCsvReport(context, generateInventoryCsvContent(assets), "Inventory Report")
    }
}
