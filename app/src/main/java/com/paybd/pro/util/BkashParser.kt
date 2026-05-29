package com.paybd.pro.util

data class BkashTransaction(
    val trxId: String,
    val amount: Double,
    val balance: Double,
    val sender: String
)

object BkashParser {

    // Matches bKash payment received SMS patterns
    // Example: "You have received Tk 500.00 from 01712345678. Your current bKash balance is Tk 1,500.00. TrxID: ABC123XYZ"
    // Example: "You have received Tk 1,000.00 from 01XXXXXXXXX. Fee Tk 0.00. Your bKash a/c balance is Tk 5,000.00. TrxID 9K2M4N7P8Q"
    private val AMOUNT_REGEX = Regex(
        """(?:received|পেয়েছেন)\s*(?:Tk|৳|BDT)\s*([\d,]+(?:\.\d{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    private val SENDER_REGEX = Regex(
        """from\s+([\d]{11})""",
        RegexOption.IGNORE_CASE
    )

    private val BALANCE_REGEX = Regex(
        """(?:balance|ব্যালেন্স)\s*(?:is\s*)?(?:Tk|৳|BDT)\s*([\d,]+(?:\.\d{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    private val TRXID_REGEX = Regex(
        """(?:TrxID|ট্রানজেকশন নং)[:\s]*([A-Za-z0-9]+)""",
        RegexOption.IGNORE_CASE
    )

    // Check if SMS is from bKash
    fun isBkashSms(sender: String, message: String): Boolean {
        val bkashSenders = listOf("bkash", "16247", "01977")
        val senderLower = sender.lowercase()
        return bkashSenders.any { senderLower.contains(it) } &&
                (message.contains("received", ignoreCase = true) ||
                 message.contains("পেয়েছেন", ignoreCase = false)) &&
                TRXID_REGEX.containsMatchIn(message)
    }

    fun parse(message: String): BkashTransaction? {
        val trxIdMatch = TRXID_REGEX.find(message) ?: return null
        val amountMatch = AMOUNT_REGEX.find(message) ?: return null

        val trxId = trxIdMatch.groupValues[1]
        val amount = parseAmount(amountMatch.groupValues[1])
        val balance = BALANCE_REGEX.find(message)?.let { parseAmount(it.groupValues[1]) } ?: 0.0
        val sender = SENDER_REGEX.find(message)?.groupValues?.get(1) ?: "Unknown"

        return BkashTransaction(
            trxId = trxId,
            amount = amount,
            balance = balance,
            sender = sender
        )
    }

    private fun parseAmount(raw: String): Double {
        return raw.replace(",", "").toDoubleOrNull() ?: 0.0
    }
}
