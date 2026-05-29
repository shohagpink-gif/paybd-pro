package com.paybd.pro
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
class PayBDApplication : Application() {
override fun onCreate() { super.onCreate()
createNotificationChannel()}
private fun createNotificationChannel() { if (Build.VERSION_INT >= Build.VERSION_CODES.O) { val channel = NotificationChannel("paybd_pro_transactions", "Transaction Notifications", NotificationManager.IMPORTANCE_HIGH).apply { description = "Notifications for new bKash transactions" }
val manager = getSystemService(NotificationManager::class.java)
manager.createNotificationChannel(channel) }}}