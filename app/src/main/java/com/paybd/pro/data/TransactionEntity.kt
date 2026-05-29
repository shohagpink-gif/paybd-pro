package com.paybd.pro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionStatus {
    PENDING,
    APPROVED,
    CANCELLED
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trxId: String,
    val amount: Double,
    val balance: Double,
    val sender: String,
    val rawMessage: String,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)
