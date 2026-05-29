package com.paybd.pro.util

import org.junit.Assert.*
import org.junit.Test

class BkashParserTest {

    @Test
    fun `parse standard bKash received SMS`() {
        val message = "You have received Tk 500.00 from 01712345678. Your current bKash balance is Tk 1,500.00. TrxID: ABC123XYZ"

        val result = BkashParser.parse(message)

        assertNotNull(result)
        assertEquals("ABC123XYZ", result!!.trxId)
        assertEquals(500.0, result.amount, 0.01)
        assertEquals(1500.0, result.balance, 0.01)
        assertEquals("01712345678", result.sender)
    }

    @Test
    fun `parse bKash SMS with fee mention`() {
        val message = "You have received Tk 1,000.00 from 01898765432. Fee Tk 0.00. Your bKash a/c balance is Tk 5,000.00. TrxID 9K2M4N7P8Q"

        val result = BkashParser.parse(message)

        assertNotNull(result)
        assertEquals("9K2M4N7P8Q", result!!.trxId)
        assertEquals(1000.0, result.amount, 0.01)
        assertEquals(5000.0, result.balance, 0.01)
        assertEquals("01898765432", result.sender)
    }

    @Test
    fun `parse bKash SMS with large amount`() {
        val message = "You have received Tk 25,000.50 from 01611223344. Your current bKash balance is Tk 50,250.50. TrxID: LARGE1TEST2"

        val result = BkashParser.parse(message)

        assertNotNull(result)
        assertEquals("LARGE1TEST2", result!!.trxId)
        assertEquals(25000.50, result.amount, 0.01)
        assertEquals(50250.50, result.balance, 0.01)
        assertEquals("01611223344", result.sender)
    }

    @Test
    fun `returns null for non-bKash SMS`() {
        val message = "Your Grameenphone balance is 50 Tk."

        val result = BkashParser.parse(message)

        assertNull(result)
    }

    @Test
    fun `returns null for SMS without TrxID`() {
        val message = "You have received Tk 500.00 from 01712345678. Your balance is Tk 1500.00."

        val result = BkashParser.parse(message)

        assertNull(result)
    }

    @Test
    fun `isBkashSms detects bKash sender`() {
        val message = "You have received Tk 500.00 from 01712345678. TrxID: ABC123"

        assertTrue(BkashParser.isBkashSms("bKash", message))
        assertTrue(BkashParser.isBkashSms("16247", message))
        assertFalse(BkashParser.isBkashSms("Nagad", message))
    }

    @Test
    fun `isBkashSms rejects non-received messages`() {
        val message = "You have sent Tk 500.00 to 01712345678. TrxID: ABC123"

        assertFalse(BkashParser.isBkashSms("bKash", message))
    }

    @Test
    fun `parse handles TrxID with colon and space`() {
        val message = "You have received Tk 200.00 from 01555666777. Your bKash balance is Tk 800.00. TrxID: XYZ789ABC"

        val result = BkashParser.parse(message)

        assertNotNull(result)
        assertEquals("XYZ789ABC", result!!.trxId)
    }

    @Test
    fun `parse handles TrxID without colon`() {
        val message = "You have received Tk 300.00 from 01999888777. Your bKash balance is Tk 3,300.00. TrxID 4ABCDE789F"

        val result = BkashParser.parse(message)

        assertNotNull(result)
        assertEquals("4ABCDE789F", result!!.trxId)
        assertEquals(300.0, result.amount, 0.01)
    }
}
