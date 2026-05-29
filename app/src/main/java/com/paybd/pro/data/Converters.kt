package com.paybd.pro.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStatus(status: TransactionStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): TransactionStatus = TransactionStatus.valueOf(value)
}
