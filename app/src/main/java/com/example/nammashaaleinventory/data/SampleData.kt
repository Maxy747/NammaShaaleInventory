package com.example.nammashaaleinventory.data

import java.util.Calendar

object SampleData {
    private fun getTimestamp(daysAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.timeInMillis
    }

    val assets = listOf(
        // Tablets
        AssetEntity(id = 1, name = "Max's AI Tablet Pro", category = "Tablet", condition = "Working", qrCodeHash = "MAX001TAB", lastAuditDate = getTimestamp(2)),
        AssetEntity(id = 2, name = "Nouri Smart Attendance Tab", category = "Tablet", condition = "Working", qrCodeHash = "NOURI002TAB", lastAuditDate = getTimestamp(5)),
        AssetEntity(id = 3, name = "Mazin Graphics Tablet", category = "Tablet", condition = "Repair", qrCodeHash = "MAZ003TAB", lastAuditDate = getTimestamp(10)),
        AssetEntity(id = 4, name = "Shizza Coding Tablet", category = "Tablet", condition = "Broken", qrCodeHash = "SHI004TAB", lastAuditDate = getTimestamp(15)),
        AssetEntity(id = 5, name = "Purple Student Tab X1", category = "Tablet", condition = "Working", qrCodeHash = "PUR005TAB", lastAuditDate = getTimestamp(1)),

        // Furniture
        AssetEntity(id = 6, name = "Nouri Ergonomic Smart Desk", category = "Furniture", condition = "Working", qrCodeHash = "NOURI006FUR", lastAuditDate = getTimestamp(20)),
        AssetEntity(id = 7, name = "Shizza Library Reading Chair", category = "Furniture", condition = "Working", qrCodeHash = "SHI007FUR", lastAuditDate = getTimestamp(3)),
        AssetEntity(id = 8, name = "Max's Principal Chair", category = "Furniture", condition = "Repair", qrCodeHash = "MAX008FUR", lastAuditDate = getTimestamp(30)),
        AssetEntity(id = 9, name = "Ponnus Wooden Staff Desk", category = "Furniture", condition = "Broken", qrCodeHash = "PON009FUR", lastAuditDate = getTimestamp(45)),
        AssetEntity(id = 10, name = "Mazin Study Table - Set B", category = "Furniture", condition = "Working", qrCodeHash = "MAZ010FUR", lastAuditDate = getTimestamp(12)),
        AssetEntity(id = 11, name = "Purple Bean Bag Lounge", category = "Furniture", condition = "Repair", qrCodeHash = "PUR011FUR", lastAuditDate = getTimestamp(8)),

        // Lab Tools
        AssetEntity(id = 12, name = "Purple Lab Microscope HD", category = "Lab Tool", condition = "Working", qrCodeHash = "PUR012LAB", lastAuditDate = getTimestamp(4)),
        AssetEntity(id = 13, name = "Nouri Chemistry Beaker Set", category = "Lab Tool", condition = "Broken", qrCodeHash = "NOURI013LAB", lastAuditDate = getTimestamp(60)),
        AssetEntity(id = 14, name = "Mazin Robotics Sensor Kit", category = "Lab Tool", condition = "Working", qrCodeHash = "MAZ014LAB", lastAuditDate = getTimestamp(7)),
        AssetEntity(id = 15, name = "Max's Physics Laser Tool", category = "Lab Tool", condition = "Repair", qrCodeHash = "MAX015LAB", lastAuditDate = getTimestamp(14)),
        AssetEntity(id = 16, name = "Shizza Biology Slide Set", category = "Lab Tool", condition = "Working", qrCodeHash = "SHI016LAB", lastAuditDate = getTimestamp(9)),
        AssetEntity(id = 17, name = "Ponnus Digital Multimeter", category = "Lab Tool", condition = "Repair", qrCodeHash = "PON017LAB", lastAuditDate = getTimestamp(11)),

        // Sports Kits
        AssetEntity(id = 18, name = "Ponnus Pro Basketball Kit", category = "Sports Kit", condition = "Working", qrCodeHash = "PON018SPT", lastAuditDate = getTimestamp(6)),
        AssetEntity(id = 19, name = "Pizza Football Training Set", category = "Sports Kit", condition = "Repair", qrCodeHash = "PIZ019SPT", lastAuditDate = getTimestamp(18)),
        AssetEntity(id = 20, name = "Nouri Badminton Racket Set", category = "Sports Kit", condition = "Broken", qrCodeHash = "NOURI020SPT", lastAuditDate = getTimestamp(25)),
        AssetEntity(id = 21, name = "Max's Cricket Leather Ball Box", category = "Sports Kit", condition = "Working", qrCodeHash = "MAX021SPT", lastAuditDate = getTimestamp(22)),
        AssetEntity(id = 22, name = "Shizza Yoga Mat Bundle", category = "Sports Kit", condition = "Working", qrCodeHash = "SHI022SPT", lastAuditDate = getTimestamp(13)),

        // Electronics
        AssetEntity(id = 23, name = "Pizza Projector Unit 4K", category = "Electronics", condition = "Broken", qrCodeHash = "PIZ023ELEC", lastAuditDate = getTimestamp(40)),
        AssetEntity(id = 24, name = "Mazin Smart Board Display", category = "Electronics", condition = "Repair", qrCodeHash = "MAZ024ELEC", lastAuditDate = getTimestamp(50)),
        AssetEntity(id = 25, name = "Ponnus Audio PA System", category = "Electronics", condition = "Working", qrCodeHash = "PON025ELEC", lastAuditDate = getTimestamp(16)),
        AssetEntity(id = 26, name = "Nouri Wireless Router Hub", category = "Electronics", condition = "Working", qrCodeHash = "NOURI026ELEC", lastAuditDate = getTimestamp(28)),
        AssetEntity(id = 27, name = "Purple LED Stage Lights", category = "Electronics", condition = "Broken", qrCodeHash = "PUR027ELEC", lastAuditDate = getTimestamp(35)),

        // Library Items
        AssetEntity(id = 28, name = "Shizza Rare Encyclopedia", category = "Library Item", condition = "Working", qrCodeHash = "SHI028LIB", lastAuditDate = getTimestamp(100)),
        AssetEntity(id = 29, name = "Mazin Sci-Fi Collection", category = "Library Item", condition = "Repair", qrCodeHash = "MAZ029LIB", lastAuditDate = getTimestamp(42)),
        AssetEntity(id = 30, name = "Pizza Graphic Novel Set", category = "Library Item", condition = "Broken", qrCodeHash = "PIZ030LIB", lastAuditDate = getTimestamp(19)),
        AssetEntity(id = 31, name = "Ponnus Audio Book Player", category = "Library Item", condition = "Working", qrCodeHash = "PON031LIB", lastAuditDate = getTimestamp(21)),

        // Classroom Utility
        AssetEntity(id = 32, name = "Pizza Automatic Sanitizer", category = "Classroom Utility", condition = "Repair", qrCodeHash = "PIZ032UTIL", lastAuditDate = getTimestamp(5)),
        AssetEntity(id = 33, name = "Max's Digital Clock Wall", category = "Classroom Utility", condition = "Working", qrCodeHash = "MAX033UTIL", lastAuditDate = getTimestamp(33)),
        AssetEntity(id = 34, name = "Nouri Paper Shredder Pro", category = "Classroom Utility", condition = "Broken", qrCodeHash = "NOURI034UTIL", lastAuditDate = getTimestamp(55)),
        AssetEntity(id = 35, name = "Purple Water Dispenser", category = "Classroom Utility", condition = "Repair", qrCodeHash = "PUR035UTIL", lastAuditDate = getTimestamp(27))
    )

    val tickets = listOf(
        RepairTicketEntity(ticketId = 1, assetId = 4, issueDescription = "Screen cracked during lab session", priority = "High", assignedTo = "Tech Max", status = "In Progress", dueDate = getTimestamp(-3), raisedByUserId = 1),
        RepairTicketEntity(ticketId = 2, assetId = 13, issueDescription = "Glass broken", priority = "Medium", assignedTo = "Unassigned", status = "Pending", dueDate = getTimestamp(-7), raisedByUserId = 1),
        RepairTicketEntity(ticketId = 3, assetId = 23, issueDescription = "Lamp burnt out", priority = "Critical", assignedTo = "Tech Mazin", status = "Delayed", dueDate = getTimestamp(-1), raisedByUserId = 1),
        RepairTicketEntity(ticketId = 4, assetId = 15, issueDescription = "Battery issues", priority = "Low", assignedTo = "Tech Shizza", status = "Completed", dueDate = getTimestamp(-5), completedDate = getTimestamp(0), raisedByUserId = 1),
        RepairTicketEntity(ticketId = 5, assetId = 9, issueDescription = "Leg wobbly", priority = "Medium", assignedTo = "Unassigned", status = "Pending", dueDate = getTimestamp(-10), raisedByUserId = 1)
    )

    val users = listOf(
        UserEntity(userId = 1, fullName = "System Admin", username = "admin", password = "admin", role = "Admin", department = "IT", isDefaultAccount = true),
        UserEntity(userId = 2, fullName = "Demo Teacher", username = "teacher", password = "teacher", role = "Teacher", department = "Science", isDefaultAccount = true)
    )
}
