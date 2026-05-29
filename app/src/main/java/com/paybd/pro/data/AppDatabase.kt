package com.paybd.pro.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
DDatabase(entities = [TransactionEntity::class], version = 1, exportSchema = false)
DTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
abstract fun transactionDao(): TransactionDao
companion object { @Volatile private var INSTANCE: AppDatabase? = null
fun getInstance(context: Context): AppDatabase { return INSTANCE ?: synchronized(this) { val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "paybd_pro_database").build(); INSTANCE = instance; instance }} }}