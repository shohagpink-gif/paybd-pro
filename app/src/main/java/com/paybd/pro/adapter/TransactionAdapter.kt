package com.paybd.pro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paybd.pro.R
import com.paybd.pro.data.TransactionEntity
import com.paybd.pro.data.TransactionStatus
import com.paybd.pro.databinding.TransactionItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val onApprove: (TransactionEntity) -> Unit,
    private val onCancel: (TransactionEntity) -> Unit
) : ListAdapter<TransactionEntity, TransactionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TransactionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: TransactionItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        fun bind(transaction: TransactionEntity) {
            binding.apply {
                tvTrxId.text = "TrxID: ${transaction.trxId}"
                tvAmount.text = "৳ ${transaction.amount}"
                tvSender.text = "From: ${transaction.sender}"
                tvBalance.text = "Balance: ৳ ${transaction.balance}"
                tvTimestamp.text = dateFormat.format(Date(transaction.timestamp))

                // Status badge
                tvStatus.text = transaction.status.name
                val statusColor = when (transaction.status) {
                    TransactionStatus.PENDING -> R.color.status_pending
                    TransactionStatus.APPROVED -> R.color.status_approved
                    TransactionStatus.CANCELLED -> R.color.status_cancelled
                }
                tvStatus.setTextColor(ContextCompat.getColor(root.context, statusColor))

                // Show/hide buttons based on status
                val isPending = transaction.status == TransactionStatus.PENDING
                btnApprove.isEnabled = isPending
                btnCancel.isEnabled = isPending
                btnApprove.alpha = if (isPending) 1.0f else 0.4f
                btnCancel.alpha = if (isPending) 1.0f else 0.4f

                btnApprove.setOnClickListener { onApprove(transaction) }
                btnCancel.setOnClickListener { onCancel(transaction) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(old: TransactionEntity, new: TransactionEntity) =
            old.id == new.id

        override fun areContentsTheSame(old: TransactionEntity, new: TransactionEntity) =
            old == new
    }
}
