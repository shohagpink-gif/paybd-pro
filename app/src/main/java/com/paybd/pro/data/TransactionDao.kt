package com.paybd.pro.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
DDao
interface TransactionDao { @Query("SELECT * FROM transactions ORDER BY timestamp DESC") fun getAllTransactions(): Flow<List<TransactionEntity>>; @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(transaction: TransactionEntity): Long; @Update suspend fun update(transaction: TransactionEntity); @Query("UPDATE transactions SET status = :status WHERE id = :id") suspend fun updateStatus(id: Long, status: TransactionStatus); @Query("SELECT * FROM transactions WHERE trxId = :trxId LIMIT 1") suspend fun getByTrxId(trxId: String): TransactionEntity; @Q¹•Éä ‰M1P€¨I=4ÑÉ…¹Í…Ñ¥½¹Ì]!I¥€ô€é¥1%5%P€Äˆ¤ÍÕÍÁ•¹™Õ¸•Ñ	å%¡¥è1½¹œ¤èQÉ…¹Í…Ñ¥½¹¹Ñ¥Ñäü