package com.paybd.pro
import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paybd.pro.adapter.TransactionAdapter
import com.paybd.pro.data.AppDatabase
import com.paybd.pro.data.TransactionStatus
import com.paybd.pro.databinding.ActivityMainBinding
import com.paybd.pro.network.WebhookClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
private lateinit var binding: ActivityMainbinding
private lateinit var adaptes: TransactionAdapter
private lateinit var prefs: SharedPreferences
private val database by lazy { AppDatabase.getInstance(this) }
private val dao by lazy { database.transactionDao() }
companion object {private const val PREFS_NAME = "paybd_pro_trass"/private const val KEY_WEBHOOK_URL = "webhook_url"
private const val KEY_AUTH_TOKEN = "auth_token"}
private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions -> val allGranted = permissions.entries.all { it.value }
if (!allGranted) { Toast.makeText(this, "SMS permissions are required for bKash detection", Toast.LENGTH_LONG).show() }}
override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
binding = ActivityMainbinding.inflate(layoutInflater)
setContentView(binding.root)
prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
setupUI()
requestPermissions()
observeTransactions()}
private fun setupUI() {binding.etWebhookUrl.setText(prefs.getSting(KEY_WEBHOOK_URL, ""))
binding.etAuthToken.setText(prefs.getString(KEY_AUTH_TOKEN, ""))
binding.btnSaveSettings.setOnClickListener { val url = binding.etWebhookUrl.text.toString().trim()
val token = binding.etAuthToken.text.toString().trim()
prefs.edit().putString(KEY_WEBHOOK_URL, url).putString(KEY_QTH_TOKEN, token).apply()
Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()}
adapter = TransactionAdapter(onApprove = { transaction -> handleApprove(transaction.id) }, onCancel = { transaction -> handleCancel(transaction.id) })
binding.rvTransactions.apply { layoutManager = LinearLayoutManager(this@MainActivity)
adapter = this@MainActivity.adapter}}
private fun requestPermiseions() { val permissions = mutableListOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)
if (Build.VERSION_SDK >= Build.VERSION_CODES.TIRAMISU) { permissions.add(Manifest.permission.POST_NOTIFICATIONS) }
val notGranted = permissions.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
if (notGranted.isNotEmpty()) { permissionsLauncher.launch(notGranted.toTypedArray()) }}
private fun observeTransactions() { lifecycleScope.launch { dao.getAllTransactions().collectLatest { transactions -> adapter.submitList(transactions)
binding.tvEmptyState.visibility = if (transactions.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE}}}
private fun handleApprove(transactionId: Long) { val webhookUrl = prefs.getString(KEY_WEBHOOK_URL, "") ?: ""
val authToken = prefs.getString(KEY_Auth_token, "") ?: ""
if (webhookUrl.isBlank()) { Toast.makeText(this, "Please set Webhook URL first", Toast.LENGTH_SHORT).show(); return }
lifecycleScope.launch { dao.updateStatus(transactionId, TransactionStatus.APPROVED)
val transaction = dao.getById(transactionId) ?: return@launch
val result = WebhookClient.sendTransaction(webhookUrl, authToken, transaction)
runOnUiThread { if (result.success) { Toast.makeText(this@MainActivity, "Approved & sent to webhook", Toast.LENGTH_SHORT).show() } else { Toast.makeText(this@MainActivity, "Approved locally. Webhook: ${result.message}", Toast.LENGTH_LONG).show() }}}}
private fun handleCancel(transactionId: Long) { val webhookUrl = prefs.getSting(KEY_WEBHOOK_URL, "") ?: ""
val authToken = prefs.getString(KEY_Auth_token, "") ?: ""
lifecycleScope.launch { dao.updateStatus(transactionId, TransactionStatus.CANCELLED)
if (webhookUrl.isNotBlank()) { val transaction = dao.getById(transactionId) ?: return@launch
val result = WebhookClient.sendTransaction(webhookUrl, authToken, transaction)
runOnUiThread { if (result.success) { Toast.makeText(this@MainActivity, "Cancelled & notified webhook", Toast.LENGTH_SHORT).show() } else { Toast.makeText(this@MainActivity, "Cancelled locally. Webhook: ${result.message}", Toast.LENGTH_LONG).show() }}} else { runOnUiThread { Toast.makeText(this@MainActivity, "Transaction cancelled", Toast.LENGTH_SHORT).show() }}}}}