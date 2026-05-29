package com.paybd.pro.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import androidx.core.app.NotificationCompat
import com.paybd.pro.MainActivity
import com.paybd.pro.R
import com.paybd.pro.data.AppDatabase
import com.paybd.pro.data.TransactionEntity
import com.paybd.pro.data.TransactionStatus
import com.paybd.pro.util.BkashParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "paybd_pro_transactions"
        private const val CHANNEL_NAME = "Transaction Notifications"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        // Combine multi-part SMS
        val senderAddress = messages[0].originatingAddress ?: return
        val fullMessage = messages.joinToString("") { it.messageBody ?: "" }

        if (!BkashParser.isBkashSms(senderAddress, fullMessage)) return

        val parsed = BkashParser.parse(fullMessage) ?: return

        val db = AppDatabase.getInstance(context)
        val dao = db.transactionDao()

        CoroutineScope(Dispatchers.IO).launch {
            // Avoid duplicates
            val existing = dao.getByTrxId(parsed.trxId)
            if (existing != null) return@launch

            val entity = TransactionEntity(
                trxId = parsed.trxId,
                amount = parsed.amount,
                balance = parsed.balance,
                sender = parsed.sender,
                rawMessage = fullMessage,
                status = TransactionStatus.PENDING,
                timestamp = System.currentTimeMillis()
            )

            val id = dao.insert(entity)
            showNotification(context, entity, id.toInt())
        }
    }

    private fun showNotification(context: Context, transaction: TransactionEntity, id: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new bKash transactions"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New bKash Payment Received")
            .setContentText("Tk ${transaction.amount} from ${transaction.sender} | TrxID: ${transaction.trxId}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASE + id, notification)
    }
}
